package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make BogeyItem$1 loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.content.casingpicker.BogeyItem$1", remap = false)
public abstract class Verify11 {
}
