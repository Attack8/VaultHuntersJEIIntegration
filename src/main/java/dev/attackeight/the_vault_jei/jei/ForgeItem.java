package dev.attackeight.the_vault_jei.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ForgeItem(List<ItemStack> ingredients, ItemStack output) {}
