package dev.attackeight.the_vault_jei.mixin;

import iskallia.vault.config.bounty.RewardConfig;
import iskallia.vault.config.entry.LevelEntryMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(RewardConfig.class)
public interface RewardPoolAccessor {

    @Accessor(remap = false)
    HashMap<String, LevelEntryMap<RewardConfig.RewardEntry>> getPOOLS();

}
