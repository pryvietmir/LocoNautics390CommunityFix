package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make HoseInteractionHandler loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.content.hose.HoseInteractionHandler", remap = false)
public abstract class Verify18 {
}
