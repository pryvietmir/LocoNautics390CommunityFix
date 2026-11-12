package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make SelectorLabelBar loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.client.gui.selector.SelectorLabelBar", remap = false)
public abstract class Verify06 {
}
