package dev.attackeight.just_enough_vh.mixin;

import iskallia.vault.config.greed.GreedCauldronConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GreedCauldronConfig.DemandEntry.class, remap = false)
public interface GreedCauldronConfigDemandAccessor {

    @Accessor
    String getItem();

    @Accessor
    int getMinAmount();

    @Accessor
    int getMaxAmount();

}
