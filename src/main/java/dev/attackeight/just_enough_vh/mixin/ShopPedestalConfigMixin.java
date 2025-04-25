package dev.attackeight.just_enough_vh.mixin;

import dev.attackeight.just_enough_vh.JustEnoughVH;
import iskallia.vault.config.ShopPedestalConfig;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.util.WeightedList;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ShopPedestalConfig.class, remap = false)
public class ShopPedestalConfigMixin {

    @Shadow public LevelEntryList<LevelEntryList.ILevelEntry> LEVELS;

    @Shadow @Final public static ShopPedestalConfig.ShopOffer EMPTY;

    @Unique
    public List<Pair<List<Triple<ItemStack, IntRangeEntry, Double>>, Integer>> getTrades() {
        List<Pair<List<Triple<ItemStack, IntRangeEntry, Double>>, Integer>> toReturn = new ArrayList<>();
        this.LEVELS.forEach(k -> {
            int level = k.getLevel();
            List<WeightedList> entries = new ArrayList<>(); // These are all WeightedList<ShopEntry>
            this.LEVELS.getForLevel(level).map((tier) -> {
                try {
                    return tier.getClass().getDeclaredField("TRADE_POOL").get(tier);
                } catch (Exception e) {
                    JustEnoughVH.LOGGER.error(e.toString());
                    return EMPTY;
                }
            }).ifPresent(x -> entries.add((WeightedList) x));
            entries.forEach(entry -> {
                List<Triple<ItemStack, IntRangeEntry, Double>> offers = new ArrayList<>();
                entry.forEach((offer, weight) -> {
                    try {
                        offers.add(new ImmutableTriple<>(
                                (ItemStack) offer.getClass().getDeclaredField("OFFER").get(offer),
                                new IntRangeEntry(
                                        (int) offer.getClass().getDeclaredField("MIN_COST").get(offer),
                                        (int) offer.getClass().getDeclaredField("MAX_COST").get(offer)
                                ),
                                (Double) weight
                        ));
                    } catch (Exception e) {
                        JustEnoughVH.LOGGER.error(e.toString());
                    }
                });
                toReturn.add(new ImmutablePair<>(offers, level));
            });
        });
        return toReturn;
    }
}
