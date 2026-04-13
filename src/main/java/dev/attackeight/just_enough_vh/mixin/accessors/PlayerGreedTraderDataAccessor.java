package dev.attackeight.just_enough_vh.mixin.accessors;

import iskallia.vault.config.greed.GreedTraderConfig;
import iskallia.vault.world.data.PlayerGreedTraderData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(value = PlayerGreedTraderData.class, remap = false)
public interface PlayerGreedTraderDataAccessor {
    @Invoker
    PlayerGreedTraderData.TradeOffer callRollSingleOffer(GreedTraderConfig.TradeEntry entry, int greedTier, GreedTraderConfig config, Random random,
                                                         int playerVaultLevel);
}
