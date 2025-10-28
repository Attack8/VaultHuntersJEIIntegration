package dev.attackeight.just_enough_vh.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record RecyclerRecipe(List<ItemStack> input, List<List<ItemStack>> outputs) {

    public static RecyclerRecipe of(ItemStack input, List<ItemStack> outputs) {
        return new RecyclerRecipe(List.of(input), List.of(outputs));
    }

}
