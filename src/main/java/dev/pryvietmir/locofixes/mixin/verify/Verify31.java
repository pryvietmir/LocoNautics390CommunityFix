package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make SmokeParticle$Provider loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.foundation.smoke.SmokeParticle$Provider", remap = false)
public abstract class Verify31 {
}
