package dev.pryvietmir.locofixes.mixin;

import com.lycoris.loconautics.trainphysics.SableTrain;
import com.lycoris.loconautics.trainphysics.wheels.GhostWheelBodies;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Derailed bogey lies on the ground with its wheels buried, while its wheel collider stays behind on the rails.
 * <p>
 * Every bogey has a separate "wheel body" (a small sub level of collision pads) jointed under it. When the bogey
 * derails the pads start touching the world while they are still inside the track, wedge there, and the car slides
 * on without them. While a bogey is on the rails RailSnap keeps pulling the wheel body back under it, but nothing
 * does that for a derailed one, so the car never gets its wheels back.
 * <p>
 * After every sync, move a derailed bogey's wheel body back under the bogey once it has drifted further than the
 * dangle cone can explain (1 block).
 */
@Mixin(value = GhostWheelBodies.class, remap = false)
public abstract class StrayWheelRecallMixin {

    @Shadow @Final
    private static Map RIGS;

    @Shadow @Final
    private static Set DANGLING;

    private static final double LOCOFIXES$STRAY_LIMIT = 1.0;
    private static Method[] locofixes$accessors;

    @Inject(method = "sync", at = @At("TAIL"), require = 0)
    private static void locofixes$recallStrayWheels(SableTrain train, CallbackInfo ci) {
        try {
            SableTrain.Car car = train.car();
            UUID carId = car == null ? null : car.subLevelId();
            if (carId == null || DANGLING.isEmpty()) {
                return;
            }
            ServerSubLevelContainer container = SubLevelContainer.getContainer(train.level());
            if (container == null || !(container.getSubLevel(carId) instanceof ServerSubLevel carSub) || carSub.isRemoved()) {
                return;
            }
            for (Object o : RIGS.entrySet()) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                Object key = entry.getKey();
                Object rig = entry.getValue();
                Method[] m = locofixes$accessors(key, rig);
                if (!carId.equals(m[0].invoke(key)) || !DANGLING.contains(key)) {
                    continue;
                }
                UUID wheelId = (UUID) m[1].invoke(rig);
                if (!(container.getSubLevel(wheelId) instanceof ServerSubLevel wheel) || wheel.isRemoved()) {
                    continue;
                }
                Vector3d pivotInCar = (Vector3d) m[2].invoke(rig);
                Vector3d anchor = carSub.logicalPose().transformPosition(new Vector3d(pivotInCar), new Vector3d());
                BlockPos plot = wheel.getPlot().getCenterBlock();
                Vector3d held = wheel.logicalPose()
                        .transformPosition(new Vector3d(plot.getX() + 0.5, plot.getY() + 0.5, plot.getZ() + 0.5), new Vector3d());
                Vector3d slip = anchor.sub(held);
                if (!slip.isFinite() || slip.lengthSquared() <= LOCOFIXES$STRAY_LIMIT * LOCOFIXES$STRAY_LIMIT) {
                    continue;
                }
                Quaterniond carRest = (Quaterniond) m[3].invoke(rig);
                Quaterniond wheelRest = (Quaterniond) m[4].invoke(rig);
                Quaterniond rest = new Quaterniond(carSub.logicalPose().orientation())
                        .mul(new Quaterniond(carRest).conjugate()).mul(wheelRest).normalize();
                wheel.logicalPose().position().add(slip);
                wheel.logicalPose().orientation().set(rest);
                PhysicsPipeline pipeline = container.physicsSystem().getPipeline();
                pipeline.teleport(wheel, wheel.logicalPose().position(), wheel.logicalPose().orientation());
                pipeline.wakeUp(wheel);
                wheel.updateLastPose();
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // Never let the recovery itself break the train tick.
        }
    }

    private static Method[] locofixes$accessors(Object key, Object rig) throws ReflectiveOperationException {
        Method[] m = locofixes$accessors;
        if (m == null) {
            m = new Method[] {
                    key.getClass().getDeclaredMethod("carSub"),
                    rig.getClass().getDeclaredMethod("wheelSub"),
                    rig.getClass().getDeclaredMethod("pivotInCar"),
                    rig.getClass().getDeclaredMethod("carRest"),
                    rig.getClass().getDeclaredMethod("wheelRest")
            };
            for (Method method : m) {
                method.setAccessible(true);
            }
            locofixes$accessors = m;
        }
        return m;
    }
}
