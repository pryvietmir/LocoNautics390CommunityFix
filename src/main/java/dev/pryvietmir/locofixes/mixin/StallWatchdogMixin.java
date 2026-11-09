package dev.pryvietmir.locofixes.mixin;

import com.lycoris.loconautics.foundation.debug.StallWatchdog;
import com.lycoris.loconautics.trainphysics.portal.PortalTrace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * StallWatchdog на первом же тике сервера запускает вечный фоновый поток, который
 * 10 раз в секунду проверяет зависания — хотя пишет в лог только при включённом PortalTrace.
 * Пропускаем его тик (и, значит, запуск потока), пока трассировка выключена.
 * Сам поток при этом тоже сначала проверяет PortalTrace.on(), так что ложных
 * дампов после выключения трассировки не будет.
 */
@Mixin(StallWatchdog.class)
public abstract class StallWatchdogMixin {

    @Inject(method = "onServerTick", at = @At("HEAD"), cancellable = true)
    private static void locofixes$onlyWhenTracing(CallbackInfo ci) {
        if (!PortalTrace.on()) ci.cancel();
    }
}
