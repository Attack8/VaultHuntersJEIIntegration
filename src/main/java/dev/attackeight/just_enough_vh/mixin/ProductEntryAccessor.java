package dev.attackeight.just_enough_vh.mixin;

import iskallia.vault.config.entry.vending.ProductEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProductEntry.class)
public interface ProductEntryAccessor {

    @Accessor(remap = false)
    int getAmountMin();

    @Accessor(remap = false)
    int getAmountMax();

}
