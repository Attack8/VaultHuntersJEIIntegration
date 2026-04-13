package dev.attackeight.just_enough_vh.mixin.accessors;

import iskallia.vault.config.greed.GreedTraderConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GreedTraderConfig.TradeEntry.class, remap = false)
public interface TradeEntryAccessor {
    @Accessor
    int getMinCoinCost();

    @Accessor
    int getMaxCoinCost();
}
