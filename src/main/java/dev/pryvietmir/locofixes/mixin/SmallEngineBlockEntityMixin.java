package dev.pryvietmir.locofixes.mixin;

import com.lycoris.loconautics.content.combustion.SmallEngineBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Intentionally empty: it only makes Mixin process SmallEngineBlockEntity, so that
 * {@link dev.pryvietmir.locofixes.LocoFixesMixinPlugin#preApply} can rewrite the one
 * instruction that crashes the game when Create Propulsion is not installed.
 */
@Mixin(SmallEngineBlockEntity.class)
public abstract class SmallEngineBlockEntityMixin {
}
