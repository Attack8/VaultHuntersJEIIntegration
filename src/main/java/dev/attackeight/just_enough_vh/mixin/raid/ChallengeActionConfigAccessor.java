package dev.attackeight.just_enough_vh.mixin.raid;

import iskallia.vault.core.vault.challenge.action.ChallengeAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ChallengeAction.Config.class, remap = false)
public interface ChallengeActionConfigAccessor {
    @Accessor
    int getTextColor();
}
