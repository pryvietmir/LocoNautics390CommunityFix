package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make PanelPartScreens loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.client.gui.selector.PanelPartScreens", remap = false)
public abstract class Verify04 {
}
