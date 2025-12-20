package dev.attackeight.just_enough_vh.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public record LabeledLootInfo(List<List<ItemStack>> itemStackList, Component label, @Nullable Component line2) {

    public LabeledLootInfo(List<List<ItemStack>> itemStackList, Component label) {
        this(itemStackList, label, null);
    }

    public static LabeledLootInfo of(List<ItemStack> itemStackList, Component label, @Nullable Component label2) {
        List<List<ItemStack>> toUse = new ArrayList<>();
        for (ItemStack stack: itemStackList) {
            toUse.add(List.of(stack));
        }
        return new LabeledLootInfo(toUse, label, label2);
    }

}
