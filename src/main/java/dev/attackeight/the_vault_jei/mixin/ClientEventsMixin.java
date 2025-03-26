package dev.attackeight.the_vault_jei.mixin;

import dev.attackeight.the_vault_jei.TheVaultJEI;
import iskallia.vault.core.world.loot.LootTableInfo;
import iskallia.vault.event.ClientEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ClientEvents.class, remap = false)
public class ClientEventsMixin {

    @Redirect(method = "addLootTableInfoToTooltip", at = @At(value = "INVOKE", target = "Liskallia/vault/core/world/loot/LootTableInfo;containsInfoForItem(Lnet/minecraft/resources/ResourceLocation;)Z"))
    private static boolean checkAgainstMoreLists(ResourceLocation rl) {
        return LootTableInfo.containsInfoForItem(rl) ||
                TheVaultJEI.BLACK_MARKET_ITEMS.keySet().contains(rl) ||
                TheVaultJEI.OMEGA_BLACK_MARKET_ITEMS.keySet().contains(rl) ||
                TheVaultJEI.SHOP_PEDESTAL_ITEMS.keySet().contains(rl);
    }

    @Inject(method = "addLootTableInfoToTooltip", at = @At(value = "INVOKE", target = "Liskallia/vault/core/world/loot/LootTableInfo;getLootTableKeysForItem(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Set;"), cancellable = true)
    private static void addOtherTooltips(ItemStack itemStack, List<Component> toolTip, CallbackInfo ci) {
        ResourceLocation item = itemStack.getItem().getRegistryName();
        if (TheVaultJEI.BLACK_MARKET_ITEMS.containsKey(item)) {
            toolTip.add((new TextComponent("  - Black Market (Level: " +
                    TheVaultJEI.BLACK_MARKET_ITEMS.get(item).toArray()[0] + "+)")).withStyle(ChatFormatting.GRAY));
        }
        if (TheVaultJEI.OMEGA_BLACK_MARKET_ITEMS.containsKey(item)) {
            toolTip.add((new TextComponent("  - Omega Black Market (Level: " +
                    TheVaultJEI.OMEGA_BLACK_MARKET_ITEMS.get(item).toArray()[0] + "+)")).withStyle(ChatFormatting.GRAY));
        }
        if (TheVaultJEI.SHOP_PEDESTAL_ITEMS.containsKey(item)) {
            toolTip.add((new TextComponent("  - Shop Pedestal (Level: " +
                    TheVaultJEI.SHOP_PEDESTAL_ITEMS.get(item).toArray()[0] + "+)")).withStyle(ChatFormatting.GRAY));
        }
        if (!LootTableInfo.containsInfoForItem(item)) ci.cancel();
    }
}
