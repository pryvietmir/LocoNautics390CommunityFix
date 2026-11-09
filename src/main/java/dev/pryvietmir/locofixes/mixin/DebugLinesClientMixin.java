package dev.pryvietmir.locofixes.mixin;

import com.lycoris.loconautics.foundation.debug.DebugLinesClient;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client-side half of the wheel debug line fix: drops the "wl_*" lines (green/red wheel columns and
 * yellow hinge lines) the server sends, so they disappear even on servers without this patch
 * or running another Loconautics build. The server-side half is {@link WheelSupportsMixin}.
 */
@Mixin(DebugLinesClient.class)
public abstract class DebugLinesClientMixin {

    @Inject(method = "put", at = @At("HEAD"), cancellable = true)
    private static void locofixes$dropWheelLines(String slot, Vec3 start, Vec3 end, int color, float width, int ttl, CallbackInfo ci) {
        if (slot != null && slot.startsWith("wl_")) {
            ci.cancel();
        }
    }
}
