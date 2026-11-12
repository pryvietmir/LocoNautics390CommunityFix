package dev.pryvietmir.locofixes.mixin.verify;

import org.spongepowered.asm.mixin.Mixin;

/** Empty: lets LocoFixesMixinPlugin make TurntableBearingBlock loadable on a dedicated server (see VerifierSafety). */
@Mixin(targets = "com.lycoris.loconautics.content.turntablebearing.TurntableBearingBlock", remap = false)
public abstract class Verify27 {
}
