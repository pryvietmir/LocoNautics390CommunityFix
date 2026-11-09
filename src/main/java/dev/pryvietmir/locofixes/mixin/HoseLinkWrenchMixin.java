package dev.pryvietmir.locofixes.mixin;

import com.lycoris.loconautics.content.hose.link.HoseLinkWrench;
import com.simibubi.create.AllItems;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * HoseLinkWrench handles every right-click on the server, but asks the client-only
 * SteelCableHover (it references LocalPlayer) whether the item is a wrench. On a
 * dedicated server that class cannot load, the event throws, and no block can be
 * placed. Do the same check without touching the client class.
 */
@Mixin(value = HoseLinkWrench.class, remap = false)
public abstract class HoseLinkWrenchMixin {
    @Redirect(method = "onRightClickBlock",
            at = @At(value = "INVOKE",
                    target = "Lcom/lycoris/loconautics/content/steelcable/SteelCableHover;isWrench(Lnet/minecraft/world/item/ItemStack;)Z"),
            require = 0)
    private static boolean locofixes$serverSafeIsWrench(ItemStack stack) {
        return AllItems.WRENCH.isIn(stack);
    }
}
