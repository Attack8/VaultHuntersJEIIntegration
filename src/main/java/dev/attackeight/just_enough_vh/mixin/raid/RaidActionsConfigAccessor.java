package dev.attackeight.just_enough_vh.mixin.raid;

import iskallia.vault.block.entity.challenge.raid.action.ChallengeAction;
import iskallia.vault.config.RaidActionsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = RaidActionsConfig.class, remap = false)
public interface RaidActionsConfigAccessor {
    @Accessor
    Map<String, ChallengeAction<?>> getValues();
}
