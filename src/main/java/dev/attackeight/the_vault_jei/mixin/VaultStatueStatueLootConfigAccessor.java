package dev.attackeight.the_vault_jei.mixin;

import io.github.a1qs.vaultadditions.config.vault.VaultStatueLootConfig;
import iskallia.vault.config.entry.SingleItemEntry;
import iskallia.vault.util.data.WeightedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VaultStatueLootConfig.class, remap = false)
public interface VaultStatueStatueLootConfigAccessor {

    @Accessor
    WeightedList<SingleItemEntry> getLOOT();

    @Accessor("MIN_ITEM_GENERATED")
    int getMinItemGenerated();

    @Accessor("MAX_ITEM_GENERATED")
    int getMaxItemGenerated();
}
