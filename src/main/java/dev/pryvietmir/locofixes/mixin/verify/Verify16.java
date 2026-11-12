package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make LeverHoldInteraction loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.content.controls.panellever.LeverHoldInteraction", remap = false)
public abstract class Verify16 {
}
