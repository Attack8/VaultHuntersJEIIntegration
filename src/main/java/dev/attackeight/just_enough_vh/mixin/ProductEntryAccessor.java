package dev.attackeight.just_enough_vh.mixin;

import iskallia.vault.config.entry.vending.ProductEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ProductEntry.class, remap = false)
public interface ProductEntryAccessor {

    @Accessor
    int getAmountMin();

    @Accessor
    int getAmountMax();

}
