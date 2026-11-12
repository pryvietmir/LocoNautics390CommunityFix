package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make ScrewLinkGrabClient loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.content.screwlink.ScrewLinkGrabClient", remap = false)
public abstract class Verify24 {
}
