package dev.attackeight.just_enough_vh.mixin.raid;

import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.challenge.action.ChallengeAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "iskallia.vault.core.vault.challenge.action.PoolChallengeAction$Config$LevelPool", remap = false)
public interface LevelPoolAccessor extends LevelEntryList.ILevelEntry {
    @Accessor
    WeightedList<ChallengeAction<?>> getPool();
}
