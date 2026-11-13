package dev.pryvietmir.locofixes.mixin;

import com.lycoris.loconautics.trainphysics.portal.PortalTwins;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Items from copycat blocks lying on the ground after a train goes through a Nether portal (issue #69).
 * <p>
 * While a train straddles a portal, a copy ("twin") of it is built on the other side. Containers in the copy are
 * emptied, but other block entities (copycat materials, for example) keep their items. When the copy is cleared
 * or a block of it is replaced, the normal block removal drops those items, which duplicates them. The copy owns
 * nothing, so remove its block entity first: then nothing drops.
 */
@Mixin(value = PortalTwins.class, remap = false)
public abstract class PortalTwinNoDropMixin {

    @Redirect(method = {"clearBox", "copyChanges"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"),
            require = 0)
    private static boolean locofixes$setWithoutDrops(ServerLevel level, BlockPos pos, BlockState state, int flags) {
        if (level.getBlockState(pos).hasBlockEntity()) {
            level.removeBlockEntity(pos);
        }
        return level.setBlock(pos, state, flags);
    }
}
