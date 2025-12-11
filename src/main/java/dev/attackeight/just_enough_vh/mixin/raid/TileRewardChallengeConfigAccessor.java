package dev.attackeight.just_enough_vh.mixin.raid;

import iskallia.vault.core.vault.challenge.action.TileRewardChallengeAction;
import iskallia.vault.core.world.data.tile.PartialTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TileRewardChallengeAction.Config.class, remap = false)
public interface TileRewardChallengeConfigAccessor {
    @Accessor
    String getName();
    @Accessor
    PartialTile getTile();
    @Accessor
    int getCount();
}
