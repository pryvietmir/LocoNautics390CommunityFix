package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make CopycatBoxesLoader$Geometry loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.client.copycat.CopycatBoxesLoader$Geometry", remap = false)
public abstract class Verify02 {
}
