package dev.attackeight.just_enough_vh.mixin.raid;

import iskallia.vault.block.entity.challenge.raid.action.PoolChallengeAction;
import iskallia.vault.config.entry.LevelEntryList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PoolChallengeAction.Config.class, remap = false)
public interface PoolChallengeActionConfigAccessor {
    @Accessor
    LevelEntryList getPools();
}
