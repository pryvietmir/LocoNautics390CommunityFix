package dev.pryvietmir.locofixes.mixin;

import com.lycoris.loconautics.trainphysics.glue.WheelSupports;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Loconautics 0.39 draws debug lines over every train car (green/red columns at each wheel and
 * yellow lines between the end wheels), for every player within 128 blocks, with no setting to
 * turn them off. They come from WheelSupports.traceColumns; skip it entirely.
 */
@Mixin(WheelSupports.class)
public abstract class WheelSupportsMixin {

    @Inject(method = "traceColumns", at = @At("HEAD"), cancellable = true)
    private static void locofixes$hideWheelDebugLines(ServerSubLevel sub, WheelSupports.Prepared prepared, CallbackInfo ci) {
        ci.cancel();
    }
}
