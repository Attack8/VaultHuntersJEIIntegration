package dev.attackeight.just_enough_vh.mixin.raid;

import iskallia.vault.block.entity.challenge.raid.action.ReferenceChallengeAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ReferenceChallengeAction.Config.class, remap = false)
public interface ReferenceChallengeActionConfigAccessor {
    @Accessor
    String getPath();
}
