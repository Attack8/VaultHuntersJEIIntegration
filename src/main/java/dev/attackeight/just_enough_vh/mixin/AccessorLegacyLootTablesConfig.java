package dev.attackeight.just_enough_vh.mixin;

import iskallia.vault.config.LegacyLootTablesConfig;
import iskallia.vault.config.entry.LevelEntryList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = LegacyLootTablesConfig.class, remap = false)
public interface AccessorLegacyLootTablesConfig {
    @Accessor LevelEntryList<LegacyLootTablesConfig.Level> getLEVELS();
}
