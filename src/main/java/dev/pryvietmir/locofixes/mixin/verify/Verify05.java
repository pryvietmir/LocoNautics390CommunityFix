package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make PartModelViewer loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.client.gui.selector.PartModelViewer", remap = false)
public abstract class Verify05 {
}
