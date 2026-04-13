package dev.attackeight.just_enough_vh.mixin;

import iskallia.vault.config.greed.GreedTraderConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(GreedTraderConfig.class)
public interface GreedTraderConfigAccessor {

    @Accessor
    Map<Integer, List<GreedTraderConfig.TradeEntry>> getTierPools();

}
