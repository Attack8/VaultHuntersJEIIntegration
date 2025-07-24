package dev.attackeight.just_enough_vh.mixin;

import iskallia.vault.config.bounty.RewardConfig;
import iskallia.vault.config.entry.LevelEntryMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(value = RewardConfig.class, remap = false)
public interface RewardPoolAccessor {

    @Accessor
    HashMap<String, LevelEntryMap<RewardConfig.RewardEntry>> getPOOLS();

}
