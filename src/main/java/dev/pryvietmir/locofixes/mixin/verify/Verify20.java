package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make CreativeTrainRelocatorItem$1 loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.content.relocator.CreativeTrainRelocatorItem$1", remap = false)
public abstract class Verify20 {
}
