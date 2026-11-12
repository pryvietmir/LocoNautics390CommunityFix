package dev.pryvietmir.locofixes.mixin;

import com.lycoris.loconautics.content.ghostwheel.GhostWheelCollisionCallback;
import com.lycoris.loconautics.trainphysics.wheels.GhostWheelBodies;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback.CollisionResult;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Derailed bogeys sink into the ground: the wheels have no collision once the car leaves the rails.
 * <p>
 * The wheels of a car are "ghost wheel" pads: a separate small body hung under every bogey. While the
 * bogey is on the rails its pads ignore the world (so they don't snag on the track). Once it derails
 * the pads are supposed to hit the ground, but GhostWheelCollisionCallback throws away every contact
 * that Sable reports without a second block position, before it even looks at whether the bogey is
 * derailed. Contacts with the ground can arrive that way, so the pads fall through and the car ends up
 * resting on the bogey block itself, with the wheels buried.
 * <p>
 * Keep such contacts for pads whose bogey is off the rails (dangling). Railed pads are unchanged.
 */
@Mixin(value = GhostWheelCollisionCallback.class, remap = false)
public abstract class GhostWheelContactMixin {

    @Inject(method = "sable$onCollision", at = @At("HEAD"), cancellable = true, require = 0)
    private void locofixes$keepGroundContactWhenDerailed(BlockPos blockPos, BlockPos otherBlockPos, Vector3d hitPos,
                                                         double impactVelocity, CallbackInfoReturnable<CollisionResult> cir) {
        if (otherBlockPos != null || blockPos == null) {
            return;
        }
        SubLevelPhysicsSystem system = SubLevelPhysicsSystem.getCurrentlySteppingSystem();
        if (system == null) {
            return;
        }
        ServerLevel level = system.getLevel();
        if (level == null) {
            return;
        }
        if (SableCompanion.INSTANCE.getContaining(level, blockPos.getX() >> 4, blockPos.getZ() >> 4) instanceof ServerSubLevel pads) {
            UUID id = pads.getUniqueId();
            if (GhostWheelBodies.isWheelBody(id) && !GhostWheelBodies.isRailedPlate(id)) {
                cir.setReturnValue(CollisionResult.NONE);
            }
        }
    }
}
