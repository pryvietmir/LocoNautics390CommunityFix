package dev.pryvietmir.locofixes.mixin;

import com.lycoris.loconautics.content.variantpicker.BlockVariantRegistry;
import com.lycoris.loconautics.network.packets.variantpicker.BlockVariantApplyPacket;
import dev.pryvietmir.locofixes.RejectLog;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Эксплойт: сервер проверял лишь, что и предмет в руке, и запрошенный блок — «какие-то»
 * варианты, но не что они из ОДНОГО семейства. Количество сохранялось, так что
 * подделанным пакетом стак почтовых ящиков превращался в стак двигателей.
 *
 * lambda$handle$1(ServerPlayer, BlockVariantApplyPacket) — тело enqueueWork,
 * выполняется уже на серверном потоке.
 */
@Mixin(BlockVariantApplyPacket.class)
public abstract class BlockVariantApplyPacketMixin {

    @Inject(method = "lambda$handle$1", at = @At("HEAD"), cancellable = true)
    private static void locofixes$requireSameFamily(ServerPlayer player, BlockVariantApplyPacket packet,
                                                    CallbackInfo ci) {
        ItemStack held = player.getItemInHand(packet.hand());
        if (held.isEmpty()) return; // оригинал сам ничего не сделает

        Block from = Block.byItem(held.getItem());
        Block to = BuiltInRegistries.BLOCK.getOptional(packet.blockId()).orElse(null);
        if (to == null) return;

        BlockVariantRegistry.Location a = BlockVariantRegistry.locate(from);
        BlockVariantRegistry.Location b = BlockVariantRegistry.locate(to);
        if (a == null || b == null) return; // оригинал отвергнет неизвестные варианты

        if (a.family() != b.family()) {
            RejectLog.warn(player, "variant swap " + BuiltInRegistries.BLOCK.getKey(from) + " -> " + packet.blockId() + " (different families)");
            ci.cancel();
        }
    }
}
