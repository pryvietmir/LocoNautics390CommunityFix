package dev.pryvietmir.locofixes.mixin;

import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Sable trains run through Steam 'n' Rails track buffers (issue #96).
 * <p>
 * Create trains stop at buffers because Steam 'n' Rails hooks Create's train navigation, which Sable trains don't
 * use. Their own scan of the track ahead (for signals) sees the buffer but ignores it. Treat a buffer like a
 * permanently red signal in that scan, so a scheduled train brakes and stops in front of it.
 */
@Mixin(targets = "com.lycoris.loconautics.trainphysics.signal.TrainSignals$SignalScan", remap = false)
public abstract class TrackBufferStopMixin {

    private static final String LOCOFIXES$BUFFER = "com.railwayteam.railways.content.buffer.TrackBuffer";

    @Shadow @Final private double range;
    @Shadow @Final private boolean reserve;
    @Shadow double stopAt;

    @Inject(method = "visit", at = @At("HEAD"), cancellable = true, require = 0)
    private void locofixes$stopAtBuffer(double distance, TrackEdgePoint point, TrackNode ahead, CallbackInfoReturnable<Boolean> cir) {
        if (this.reserve && point != null && distance <= this.range && locofixes$isBuffer(point.getClass())) {
            if (Double.isNaN(this.stopAt) || distance < this.stopAt) {
                this.stopAt = distance;
            }
            cir.setReturnValue(true);
        }
    }

    private static boolean locofixes$isBuffer(Class<?> type) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            if (LOCOFIXES$BUFFER.equals(c.getName())) {
                return true;
            }
        }
        return false;
    }
}
