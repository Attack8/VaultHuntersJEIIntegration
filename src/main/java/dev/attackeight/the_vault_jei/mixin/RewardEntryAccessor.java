package dev.attackeight.the_vault_jei.mixin;

import iskallia.vault.config.bounty.RewardConfig;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.config.entry.ItemStackPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RewardConfig.RewardEntry.class)
public interface RewardEntryAccessor {

    @Accessor(remap = false)
    IntRangeEntry getVaultExp();

    @Accessor(remap = false)
    ItemStackPool getItemPool();

}
