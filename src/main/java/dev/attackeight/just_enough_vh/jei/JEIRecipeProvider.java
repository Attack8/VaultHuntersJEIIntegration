package dev.attackeight.just_enough_vh.jei;

import dev.attackeight.just_enough_vh.JustEnoughVH;
import dev.attackeight.just_enough_vh.mixin.*;
import io.github.a1qs.vaultadditions.config.vault.AbstractStatueLootConfig;
import iskallia.vault.config.OmegaSoulShardConfig;
import iskallia.vault.config.ShopPedestalConfig;
import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.config.entry.recipe.ConfigForgeRecipe;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.config.recipe.ForgeRecipesConfig;
import iskallia.vault.gear.crafting.recipe.*;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.integration.jei.lootinfo.LootInfo;
import iskallia.vault.util.data.WeightedList;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class JEIRecipeProvider {

    protected static <R extends VaultForgeRecipe, T extends ConfigForgeRecipe<R>, S extends ForgeRecipesConfig<T, R>> List<ForgeItem> getForgeRecipes(S config) {
        List<ForgeItem> recipes = new ArrayList<>();
        config.getConfigRecipes().forEach(b -> recipes.add(new ForgeItem(b.makeRecipe().getInputs(), b.makeRecipe().getDisplayOutput(100))));
        return recipes;
    }

    protected static List<LootInfo> getFromPool(WeightedList<ProductEntry> pool) {
        List<ItemStack> loot = new ArrayList<>();
        int total = pool.getTotalWeight();
        pool.forEach(b -> loot.add(addWeight(b, total)));
        return List.of(new LootInfo(loot));
    }

    protected static ItemStack addWeight(WeightedList.Entry<ProductEntry> productEntry, int totalWeight) {
        ProductEntryAccessor entry = (ProductEntryAccessor) productEntry.value;
        if (!ForgeRegistries.ITEMS.containsKey(productEntry.value.getItem().getRegistryName()))
            return ItemStack.EMPTY;
        return formatItemStack(productEntry.value.getItem(), entry.getAmountMin(),
                entry.getAmountMax(), productEntry.weight, totalWeight);
    }

    @SuppressWarnings("unchecked")
    protected static List<LabeledLootInfo> getShopPedestalLoot() {
        List<LabeledLootInfo> lootInfo = new ArrayList<>();
        List<Pair<List<Triple<ItemStack, IntRangeEntry, Double>>, Integer>> pedestalInfo;
        try {
            pedestalInfo = (List<Pair<List<Triple<ItemStack, IntRangeEntry, Double>>, Integer>>)
                    ModConfigs.SHOP_PEDESTAL.getClass().getMethod("getTrades").invoke(ModConfigs.SHOP_PEDESTAL);
        } catch (Exception e) {
            JustEnoughVH.LOGGER.error(e.toString());
            return new ArrayList<>();
        }
        pedestalInfo.forEach(tierInfo -> {
            List<Triple<ItemStack, IntRangeEntry, Double>> tier = tierInfo.getLeft();
            int minLevel = tierInfo.getRight();
            AtomicInteger totalWeight = new AtomicInteger();
            tier.forEach(d -> totalWeight.addAndGet(d.getRight().intValue()));
            List<ItemStack> offers = new ArrayList<>();
            tier.forEach(offerInfo -> {
                ItemStack currentOffer = offerInfo.getLeft();
                if (!JustEnoughVH.SHOP_PEDESTAL_ITEMS.containsKey(currentOffer.getItem().getRegistryName()))
                    JustEnoughVH.SHOP_PEDESTAL_ITEMS.put(currentOffer.getItem().getRegistryName(), minLevel);
                offers.add(formatItemStack(currentOffer.getItem(), offerInfo.getMiddle().getMin(),
                        offerInfo.getMiddle().getMax(), offerInfo.getRight().intValue(), totalWeight.get(), true));
            });
            lootInfo.add(LabeledLootInfo.of(offers, new TextComponent("Level " + minLevel + "+ "), null));
        });
        return lootInfo;
    }

    protected static List<LabeledLootInfo> getBlackMarketLoot() {
        List<LabeledLootInfo> lootInfo = new ArrayList<>();
        Set<SoulShardConfig.Trades> tradesList = ModConfigs.SOUL_SHARD.getTrades();
        tradesList.forEach(b -> {
            int minLevel = b.getMinLevel();
            int randomPrice = b.getShardTradePrice();
            List<ItemStack> shardTrades = new ArrayList<>();
            AtomicInteger totalWeight = new AtomicInteger();
            b.getShardTrades().forEach(d -> totalWeight.addAndGet(d.weight));
            b.getShardTrades().forEach( c -> {
                ItemStack currentTrade = c.value.getItemEntry().createItemStack();
                if (!JustEnoughVH.BLACK_MARKET_ITEMS.containsKey(currentTrade.getItem().getRegistryName()))
                    JustEnoughVH.BLACK_MARKET_ITEMS.put(currentTrade.getItem().getRegistryName(), minLevel);
                shardTrades.add(formatItemStack(currentTrade.getItem(), c.value.getMinPrice(),
                        c.value.getMaxPrice(), c.weight, totalWeight.get(), true));
            });
            lootInfo.add(LabeledLootInfo.of(shardTrades, new TextComponent("Common Slot: Level " + minLevel + "+ "), new TextComponent("Soul Trade Price: " + randomPrice)));
        });
        Set<OmegaSoulShardConfig.Trades> omegaTradesList = ModConfigs.OMEGA_SOUL_SHARD.getTrades();
        omegaTradesList.forEach(b -> {
            int minLevel = b.getMinLevel();
            List<ItemStack> shardTrades = new ArrayList<>();
            AtomicInteger totalWeight = new AtomicInteger();
            b.getShardTrades().forEach(d -> totalWeight.addAndGet(d.weight));
            b.getShardTrades().forEach( c -> {
                ItemStack currentTrade = c.value.getItem();
                if (!JustEnoughVH.OMEGA_BLACK_MARKET_ITEMS.containsKey(currentTrade.getItem().getRegistryName()))
                    JustEnoughVH.OMEGA_BLACK_MARKET_ITEMS.put(currentTrade.getItem().getRegistryName(), minLevel);
                shardTrades.add(formatItemStack(currentTrade.getItem(), c.value.getMinPrice(),
                        c.value.getMaxPrice(), c.weight, totalWeight.get(), true));
            });
            lootInfo.add(LabeledLootInfo.of(shardTrades, new TextComponent("Omega Slot: Level " + minLevel + "+ "), null));
        });
        return lootInfo;
    }

    protected static List<LabeledLootInfo> getModBoxLoot() {
        List<LabeledLootInfo> lootInfo = new ArrayList<>();
        ModConfigs.MOD_BOX.POOL.forEach((mod, k) -> {
            AtomicInteger totalWeight = new AtomicInteger();
            List<ItemStack> results = new ArrayList<>();
            k.forEach(d -> totalWeight.addAndGet(d.weight));
            k.forEach(c -> {
                ProductEntryAccessor accessor = (ProductEntryAccessor) c;
                results.add(formatItemStack(c.value.getItem(), accessor.getAmountMin(),
                        accessor.getAmountMax(), c.weight, totalWeight.get()));
            });
            lootInfo.add(LabeledLootInfo.of(results, new TextComponent("Mod: " + mod), null));
        });
        return lootInfo;
    }

    protected static List<LabeledLootInfo> getBountyRewards() {
        List<LabeledLootInfo> toReturn = new ArrayList<>();
        RewardPoolAccessor rewardPools = (RewardPoolAccessor) ModConfigs.REWARD_CONFIG;
        rewardPools.getPOOLS().forEach((id, entry) -> {
            TreeMap<Integer, LabeledLootInfo> lootInfo = new TreeMap<>();
            if (!id.equals("submission")) {
                entry.forEach((minLevel, rewards) -> {
                    AtomicInteger totalWeight = new AtomicInteger();
                    List<ItemStack> results = new ArrayList<>();
                    RewardEntryAccessor rewardEntry = (RewardEntryAccessor) rewards;
                    IntRangeEntry vaultExp = rewardEntry.getVaultExp();
                    rewards.getItemPool().getPool().forEach(stack -> totalWeight.addAndGet(stack.weight));
                    rewards.getItemPool().getPool().forEach(stack ->
                        results.add(formatItemStack(stack.value.getMatchingStack().getItem(), stack.value.getMinCount(),
                                stack.value.getMaxCount(), stack.weight, totalWeight.get()))
                    );
                    lootInfo.put(minLevel, LabeledLootInfo.of(results,
                            new TextComponent("Reward Pool: " + id + " Level: " + minLevel + "+"),
                            new TextComponent("Vault Exp Reward: " + vaultExp.getMin() + "-" + vaultExp.getMax())));
                });
            }
            lootInfo.forEach((n,i) -> toReturn.add(i));
        });
        return toReturn;
    }

    protected static List<LabeledLootInfo> getAltarIngredients() {
        List<LabeledLootInfo> toReturn = new ArrayList<>();
        AltarIngredientAccessor rewardPools = (AltarIngredientAccessor) ModConfigs.VAULT_ALTAR_INGREDIENTS;
        TreeMap<Integer, List<LabeledLootInfo>> lootInfo = new TreeMap<>();
        rewardPools.getLEVELS().forEach((minLevel, entry) -> {
            List<LabeledLootInfo> pool = new ArrayList<>();
            entry.forEach((slot, rewards) -> {
                AtomicInteger totalWeight = new AtomicInteger();
                List<List<ItemStack>> results = new ArrayList<>();
                rewards.forEach(stack -> totalWeight.addAndGet(stack.weight));
                rewards.forEach(stack -> {
                    IntRangeEntry amounts = ((IngredientAmountAccessor) stack.value).getAmount();
                    List<ItemStack> stacks = new ArrayList<>();
                    for (ItemStack stackInGroup : stack.value.getItems()) {
                        stacks.add(formatItemStack(stackInGroup.getItem(), amounts.getMin(),
                                amounts.getMax(), stack.weight, totalWeight.get()));
                    }
                    results.add(stacks);
                });
                pool.add(new LabeledLootInfo(results,
                        new TextComponent("Reward Pool: " + slot + " Level: " + minLevel + "+")));
            });
            lootInfo.put(minLevel, pool);
        });
        lootInfo.forEach((i, n) -> toReturn.addAll(n));
        return toReturn;
    }

    protected static List<LabeledLootInfo> getMaterialBoxLoot() {
        List<LabeledLootInfo> toReturn = new ArrayList<>();
        TreeMap<Integer, List<LabeledLootInfo>> lootInfo = new TreeMap<>();
        ModConfigs.MATERIAL_BOX.LEVELS.forEach((levelEntry) -> {
            List<LabeledLootInfo> pool = new ArrayList<>();
            AtomicInteger totalWeight = new AtomicInteger();
            List<List<ItemStack>> results = new ArrayList<>();
            levelEntry.pool.forEach(stack -> totalWeight.addAndGet(stack.weight));
            levelEntry.pool.forEach((group, weight) -> {
                List<ItemStack> stacks = new ArrayList<>();
                for (ProductEntry stackInGroup : group.entries) {
                    ProductEntryAccessor accessor = (ProductEntryAccessor) stackInGroup;
                    stacks.add(formatItemStack(stackInGroup.getItem(), accessor.getAmountMin(),
                            accessor.getAmountMax(), weight.intValue(), totalWeight.get()));
                }
                results.add(stacks);
                pool.add(new LabeledLootInfo(results,
                        new TextComponent(" Level: " + levelEntry.level + "+")));
            });
            lootInfo.put(levelEntry.getLevel(), pool);
        });
        lootInfo.forEach((i, n) -> toReturn.addAll(n));
        return toReturn;
    }

    protected static <T extends AbstractStatueLootConfig> List<LabeledLootInfo> getStatueLoot(T lootConfig) {
        List<LabeledLootInfo> toReturn = new ArrayList<>();
        List<ItemStack> items = new ArrayList<>();
        int interval = lootConfig.getInterval();
        int minCount = lootConfig.getRollRange().getMin();
        int maxCount = lootConfig.getRollRange().getMax();
        int totalWeight = lootConfig.getDrops().getTotalWeight();
        lootConfig.getDrops().forEach((product, weight) -> {
            ProductEntryAccessor accessor = (ProductEntryAccessor) product;
            items.add(formatItemStack(product.getItem(), accessor.getAmountMin(),
                    accessor.getAmountMax(), weight.intValue(), totalWeight));
        });
        toReturn.add(LabeledLootInfo.of(items,
                new TextComponent("Interval: " + interval),
                new TextComponent("Count: " + minCount + " - " + maxCount)
        ));
        return toReturn;
    }

    private static ItemStack formatItemStack(ItemLike item, int amountMin, int amountMax, int weight, int totalWeight, boolean cost) {
        ItemStack result = new ItemStack(item, amountMax);
        double chance = ((double) weight / totalWeight) * 100;
        CompoundTag nbt = result.getOrCreateTagElement("display");
        ListTag list = nbt.getList("Lore", 8);
        MutableComponent chanceLabel = new TextComponent("Chance: ");
        chanceLabel.append(String.format("%.2f", chance));
        chanceLabel.append("%");
        list.add(StringTag.valueOf(Component.Serializer.toJson(chanceLabel.withStyle(ChatFormatting.YELLOW))));
        if (amountMin != amountMax) {
            MutableComponent countLabel = new TextComponent(cost ? "Cost: " : "Count: ");
            countLabel.append(amountMin + " - " + amountMax);
            list.add(StringTag.valueOf(Component.Serializer.toJson(countLabel)));
        }
        nbt.put("Lore", list);
        return result;
    }

    private static ItemStack formatItemStack(ItemLike item, int amountMin, int amountMax, int weight, int totalWeight) {
        return formatItemStack(item, amountMin, amountMax, weight, totalWeight, false);
    }
}
