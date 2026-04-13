package dev.attackeight.just_enough_vh.mixin.accessors;

import iskallia.vault.config.greed.GreedCauldronConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = GreedCauldronConfig.class, remap = false)
public interface GreedCauldronAccessor {
    @Accessor
    int getGlobalCoinOutputMin();

    @Accessor
    int getGlobalCoinOutputMax();

    @Accessor
    List<GreedCauldronConfig.DemandEntry> getDemands();
}
