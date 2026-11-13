package dev.pryvietmir.locofixes.mixin;

import com.lycoris.loconautics.compat.create.psi.SablePsiDriver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Portable storage interfaces on trains only connect with a 1 block gap, not 2 like in Create (issue #71).
 * <p>
 * The link already supports a reach of 2 (SablePsiFrames.reachOf clamps to 2), but the search only looks
 * 2 blocks ahead and the alignment checks reject anything more than 2.5 blocks centre to centre. Look 3 blocks
 * ahead and accept up to 3.5, i.e. a gap of up to 2 blocks.
 */
@Mixin(value = SablePsiDriver.class, remap = false)
public abstract class PsiRangeMixin {

    @ModifyConstant(method = "connect", constant = @Constant(intValue = 2), require = 0)
    private static int locofixes$searchThreeAhead(int steps) {
        return 3;
    }

    @ModifyConstant(method = {"stillInReach", "squarelyAligned"}, constant = @Constant(doubleValue = 2.5), require = 0)
    private static double locofixes$acceptTwoBlockGap(double maxAlong) {
        return 3.5;
    }
}
