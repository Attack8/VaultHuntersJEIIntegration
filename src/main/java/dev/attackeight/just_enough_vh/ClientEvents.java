package dev.attackeight.just_enough_vh;

import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tooltipEvent(ItemTooltipEvent event) {
        ItemStack current = event.getItemStack();
        List<Component> tooltip = event.getToolTip();

        if (!current.isEmpty() && Screen.hasShiftDown()) {
            if (current.getItem() instanceof VaultGearItem) {
                VaultGearState state = VaultGearData.read(current).getState();
                if (state != VaultGearState.UNIDENTIFIED) {
                    return;
                }
            }
            ResourceLocation item = current.getItem().getRegistryName();
            if (JustEnoughVH.BLACK_MARKET_ITEMS.containsKey(item)) {
                tooltip.add((new TextComponent("  - Black Market (Level: " +
                        JustEnoughVH.BLACK_MARKET_ITEMS.get(item).toArray()[0] + "+)")).withStyle(ChatFormatting.GRAY));
            }
            if (JustEnoughVH.OMEGA_BLACK_MARKET_ITEMS.containsKey(item)) {
                tooltip.add((new TextComponent("  - Omega Black Market (Level: " +
                        JustEnoughVH.OMEGA_BLACK_MARKET_ITEMS.get(item).toArray()[0] + "+)")).withStyle(ChatFormatting.GRAY));
            }
            if (JustEnoughVH.SHOP_PEDESTAL_ITEMS.containsKey(item)) {
                tooltip.add((new TextComponent("  - Shop Pedestal (Level: " +
                        JustEnoughVH.SHOP_PEDESTAL_ITEMS.get(item).toArray()[0] + "+)")).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
