package dev.attackeight.just_enough_vh.mixin.raid;

import iskallia.vault.block.entity.challenge.raid.action.ChallengeAction;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.util.WeightedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "iskallia.vault.block.entity.challenge.raid.action.PoolChallengeAction$Config$LevelPool", remap = false)
public interface LevelPoolAccessor extends LevelEntryList.ILevelEntry {
    @Accessor
    WeightedList<ChallengeAction<?>> getPool();
}
