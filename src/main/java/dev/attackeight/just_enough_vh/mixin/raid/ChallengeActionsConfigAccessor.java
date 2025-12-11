package dev.attackeight.just_enough_vh.mixin.raid;

import iskallia.vault.config.ChallengeActionsConfig;
import iskallia.vault.core.vault.challenge.action.ChallengeAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ChallengeActionsConfig.class, remap = false)
public interface ChallengeActionsConfigAccessor {
    @Accessor
    Map<String, ChallengeAction<?>> getValues();
}
