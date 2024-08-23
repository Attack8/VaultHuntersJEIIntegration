package dev.attackeight.the_vault_jei.mixin;

import iskallia.vault.config.altar.VaultAltarIngredientsConfig;
import iskallia.vault.config.altar.entry.AltarIngredientEntry;
import iskallia.vault.config.entry.LevelEntryMap;
import iskallia.vault.util.data.WeightedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(VaultAltarIngredientsConfig.class)
public interface AltarIngredientAccessor {

    @Accessor(remap = false)
    LevelEntryMap<Map<String, WeightedList<AltarIngredientEntry>>> getLEVELS();
}
