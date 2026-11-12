package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make LateParticles loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.foundation.particle.LateParticles", remap = false)
public abstract class Verify30 {
}
