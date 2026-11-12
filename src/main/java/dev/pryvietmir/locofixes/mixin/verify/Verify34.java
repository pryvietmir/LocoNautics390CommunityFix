package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make PortalCutsClient loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.trainphysics.portal.PortalCutsClient", remap = false)
public abstract class Verify34 {
}
