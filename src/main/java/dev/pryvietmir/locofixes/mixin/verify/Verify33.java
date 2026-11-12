package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make SableTrainSounds loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.trainphysics.fx.SableTrainSounds", remap = false)
public abstract class Verify33 {
}
