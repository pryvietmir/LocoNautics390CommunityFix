package dev.pryvietmir.locofixes.mixin;

import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.lycoris.loconautics.trainphysics.SableTrain;
import com.lycoris.loconautics.trainphysics.rail.RailPick;
import com.lycoris.loconautics.trainphysics.spawn.SableTrainSpawner;
import com.lycoris.loconautics.trainphysics.state.SableTrainRegistry;
import dev.pryvietmir.locofixes.RejectLog;
import dev.pryvietmir.locofixes.LocoFixesConfig;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Эксплойт: что в руке реловатор, проверял только клиент. Сервер (relocate → planRelocation)
 * смотрел лишь на конфиг и на то, что ТОЧКА НАЗНАЧЕНИЯ в 24 блоках от игрока. Подделанный
 * SableTrainRelocatePacket позволял утащить к себе любой поезд по UUID его подуровня.
 */
@Mixin(SableTrainSpawner.class)
public abstract class SableTrainSpawnerMixin {

    @Inject(method = "relocate", at = @At("HEAD"), cancellable = true)
    private static void locofixes$checkRelocate(ServerPlayer player, UUID subLevelId, RailPick pick, Vec3 look,
                                                CallbackInfo ci) {
        Item relocator = LoconauticsRegistries.CREATIVE_TRAIN_RELOCATOR.get();
        boolean holding = player.getMainHandItem().is(relocator) || player.getOffhandItem().is(relocator);
        if (!holding) {
            reject(player, ci, "relocate without a relocator in hand", "message.loconautics_fixes.need_relocator");
            return;
        }

        if (LocoFixesConfig.REQUIRE_OWNERSHIP.get() && !player.hasPermissions(2)) {
            SableTrain train = SableTrainRegistry.bySubLevel(subLevelId);
            UUID owner = train == null ? null : train.owner();
            if (owner != null && !owner.equals(player.getUUID())) {
                reject(player, ci, "relocate of someone else's train", "message.loconautics_fixes.not_owner");
            }
        }
    }

    private static void reject(ServerPlayer player, CallbackInfo ci, String why, String messageKey) {
        RejectLog.warn(player, why);
        player.displayClientMessage(Component.translatable(messageKey), true);
        ci.cancel();
    }
}
