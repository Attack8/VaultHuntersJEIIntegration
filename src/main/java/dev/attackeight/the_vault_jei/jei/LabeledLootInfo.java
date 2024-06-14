package dev.attackeight.the_vault_jei.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public record LabeledLootInfo(List<ItemStack> itemStackList, Component label, @Nullable Component line2) {

    public LabeledLootInfo(List<ItemStack> itemStackList, Component label, Component line2) {
        this.itemStackList = itemStackList;
        this.label = label;
        this.line2 = line2;
    }

    public LabeledLootInfo(List<ItemStack> itemStackList, Component label) {
        this(itemStackList, label, null);
    }

    public List<ItemStack> itemStackList() {
        return this.itemStackList;
    }

    public Component label() {
        return this.label;
    }

    public Component line2() {
        return this.line2;
    }

}
