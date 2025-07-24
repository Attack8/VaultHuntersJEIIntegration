package dev.attackeight.just_enough_vh.mixin;

import iskallia.vault.config.altar.entry.AltarIngredientEntry;
import iskallia.vault.config.entry.IntRangeEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AltarIngredientEntry.class, remap = false)
public interface IngredientAmountAccessor {

    @Accessor
    IntRangeEntry getAmount();

}
