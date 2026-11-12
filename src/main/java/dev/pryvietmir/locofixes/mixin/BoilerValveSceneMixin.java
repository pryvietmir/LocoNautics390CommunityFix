package dev.pryvietmir.locofixes.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * The pressure Ponder scene swaps in a Smart Safety Valve with its default state, which is the
 * lowered variant; on a boiler barrel the valve sits raised. Keep it raised so it doesn't jump.
 */
@Mixin(targets = "com.lycoris.loconautics.client.ponder.BoilerScenes", remap = false)
public abstract class BoilerValveSceneMixin {
    @ModifyArg(method = "pressure",
            at = @At(value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/ponder/CreateSceneBuilder$WorldInstructions;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V"),
            index = 1, require = 0)
    private static BlockState locofixes$keepValveRaised(BlockState state) {
        Property<?> raised = state.getBlock().getStateDefinition().getProperty("raised");
        return raised == null ? state : locofixes$raise(state, raised);
    }

    private static <T extends Comparable<T>> BlockState locofixes$raise(BlockState state, Property<T> property) {
        return property.getValue("true").map(v -> state.setValue(property, v)).orElse(state);
    }
}
