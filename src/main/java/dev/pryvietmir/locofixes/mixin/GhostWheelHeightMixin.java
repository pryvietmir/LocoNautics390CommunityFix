package dev.pryvietmir.locofixes.mixin;

import com.lycoris.loconautics.content.ghostwheel.GhostWheelBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * A derailed car stands too high: its wheel collision pads start 0.3125 of a block above the floor of the
 * cell under the bogey, about half a block below where the wheels are drawn, so the car floats above the
 * ground. Raise the pads by half a block (floor 0.8125). Monorail pads keep their own value.
 */
@Mixin(value = GhostWheelBlock.class, remap = false)
public abstract class GhostWheelHeightMixin {

    @ModifyConstant(method = "padShape", constant = @Constant(doubleValue = 0.3125), require = 0)
    private static double locofixes$raisePads(double floor) {
        return 0.8125;
    }
}
