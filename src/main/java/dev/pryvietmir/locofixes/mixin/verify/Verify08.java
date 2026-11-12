package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make StrandKnotPicker loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.common.strand.StrandKnotPicker", remap = false)
public abstract class Verify08 {
}
