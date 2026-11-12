package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make BogeyWheelAnimator loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.trainphysics.visual.BogeyWheelAnimator", remap = false)
public abstract class Verify35 {
}
