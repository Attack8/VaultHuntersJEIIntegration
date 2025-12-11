package dev.attackeight.just_enough_vh.mixin.raid;

import iskallia.vault.core.vault.challenge.action.FloatingItemRewardChallengeAction;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = FloatingItemRewardChallengeAction.Config.class, remap = false)
public interface FloatingItemRewardChallengeActionConfigAccessor {
    @Accessor
    String getName();
    @Accessor
    ItemStack getItem();
}
