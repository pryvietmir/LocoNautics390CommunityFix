package dev.pryvietmir.locofixes.mixin;

import com.lycoris.loconautics.LoconauticsClient;
import com.lycoris.loconautics.client.LoconauticsPartialModels;
import com.lycoris.loconautics.client.LoconauticsSpriteShifts;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Invisible gauges/buttons on panels (issue #8), a magenta-black "missing model" cube on the turntable bearing,
 * and other missing moving parts.
 * <p>
 * Loconautics creates its PartialModels and sprite shifts in FMLClientSetupEvent. By then the game may already
 * be loading models in parallel, and a PartialModel created after model registration is never baked: it renders
 * as nothing or as the missing model until the next F3+T. Flywheel and Create expect these to be created in
 * the mod constructor, so create them there.
 */
@Mixin(value = LoconauticsClient.class, remap = false)
public abstract class EarlyPartialModelsMixin {

    @Inject(method = "<init>", at = @At("RETURN"), require = 0)
    private void locofixes$registerModelsEarly(ModContainer container, IEventBus modBus, CallbackInfo ci) {
        LoconauticsPartialModels.init();
        LoconauticsSpriteShifts.init();
    }
}
