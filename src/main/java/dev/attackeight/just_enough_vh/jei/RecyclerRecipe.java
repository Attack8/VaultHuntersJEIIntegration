package dev.attackeight.just_enough_vh.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record RecyclerRecipe(List<ItemStack> input, List<List<ItemStack>> outputs) {}
