package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make StrandRenderer loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.common.strand.StrandRenderer", remap = false)
public abstract class Verify09 {
}
