package dev.attackeight.just_enough_vh.mixin;

import io.github.a1qs.vaultadditions.config.vault.AbstractStatueLootConfig;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.util.data.WeightedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractStatueLootConfig.class, remap = false)
public interface AbstractStatueLootConfigAccessor {

    @Accessor("DROPS")
    WeightedList<ProductEntry> getDrops();

    @Accessor("MIN_ITEM_GENERATED")
    int getMinItemGenerated();

    @Accessor("MAX_ITEM_GENERATED")
    int getMaxItemGenerated();
}
