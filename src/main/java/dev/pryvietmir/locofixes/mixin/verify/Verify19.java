package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make HoseSprayParticle$Provider loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.content.hose.spray.entity.HoseSprayParticle$Provider", remap = false)
public abstract class Verify19 {
}
