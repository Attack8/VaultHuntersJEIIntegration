package dev.attackeight.the_vault_jei.mixin;

import iskallia.vault.config.altar.entry.AltarIngredientEntry;
import iskallia.vault.config.entry.IntRangeEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AltarIngredientEntry.class)
public interface IngredientAmountAccessor {

    @Accessor(remap = false)
    IntRangeEntry getAmount();

}
