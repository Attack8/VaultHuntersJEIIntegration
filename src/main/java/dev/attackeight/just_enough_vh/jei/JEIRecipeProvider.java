package dev.attackeight.just_enough_vh.jei;

import dev.attackeight.just_enough_vh.JustEnoughVH;
import dev.attackeight.just_enough_vh.mixin.*;
import io.github.a1qs.vaultadditions.config.vault.AbstractStatueLootConfig;
import iskallia.vault.config.OmegaSoulShardConfig;
import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.config.entry.IntRangeEntry;
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
import net.minecraftforge.registries.ForgeRegistries;
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
        ItemStack stack = new ItemStack(productEntry.value.getItem(), entry.getAmountMax());
        CompoundTag nbt = stack.getOrCreateTagElement("display");
        ListTag list = nbt.getList("Lore", 8);
        MutableComponent component = new TextComponent("Chance: ");
        double chance = ((double) productEntry.weight / totalWeight) * 100;
        component.append(String.format("%.2f", chance));
        component.append("%");
        list.add(StringTag.valueOf(Component.Serializer.toJson(component.withStyle(ChatFormatting.YELLOW))));
        if (entry.getAmountMax() != entry.getAmountMin()) {
            MutableComponent countLabel = new TextComponent("Count: ");
            countLabel.append(entry.getAmountMin() + " - " + entry.getAmountMax());
            list.add(StringTag.valueOf(Component.Serializer.toJson(countLabel)));
        }
        nbt.put("Lore", list);
        return stack;
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
                int minPrice = offerInfo.getMiddle().getMin();
                int maxPrice = offerInfo.getMiddle().getMax();
                double chance = (offerInfo.getRight() / totalWeight.get()) * 100;
                CompoundTag nbt = currentOffer.getOrCreateTagElement("display");
                ListTag list = nbt.getList("Lore", 8);
                MutableComponent chanceLabel = new TextComponent("Chance: ");
                chanceLabel.append(String.format("%.2f", chance));
                chanceLabel.append("%");
                list.add(StringTag.valueOf(Component.Serializer.toJson(chanceLabel.withStyle(ChatFormatting.YELLOW))));
                MutableComponent costLabel = new TextComponent("Cost: ");
                costLabel.append(minPrice + " - " + maxPrice);
                list.add(StringTag.valueOf(Component.Serializer.toJson(costLabel)));
                nbt.put("Lore", list);
                if (!JustEnoughVH.SHOP_PEDESTAL_ITEMS.containsKey(currentOffer.getItem().getRegistryName()))
                    JustEnoughVH.SHOP_PEDESTAL_ITEMS.put(currentOffer.getItem().getRegistryName(), minLevel);
                offers.add(currentOffer);
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
                int minPrice = c.value.getMinPrice();
                int maxPrice = c.value.getMaxPrice();
                double chance = ((double) c.weight / totalWeight.get()) * 100 * 2;
                CompoundTag nbt = currentTrade.getOrCreateTagElement("display");
                ListTag list = nbt.getList("Lore", 8);
                MutableComponent chanceLabel = new TextComponent("Chance: ");
                chanceLabel.append(String.format("%.2f", chance));
                chanceLabel.append("%");
                list.add(StringTag.valueOf(Component.Serializer.toJson(chanceLabel.withStyle(ChatFormatting.YELLOW))));
                MutableComponent costLabel = new TextComponent("Cost: ");
                costLabel.append(minPrice + " - " + maxPrice);
                list.add(StringTag.valueOf(Component.Serializer.toJson(costLabel)));
                nbt.put("Lore", list);
                if (!JustEnoughVH.BLACK_MARKET_ITEMS.containsKey(currentTrade.getItem().getRegistryName()))
                    JustEnoughVH.BLACK_MARKET_ITEMS.put(currentTrade.getItem().getRegistryName(), minLevel);
                shardTrades.add(currentTrade);
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
                int minPrice = c.value.getMinPrice();
                int maxPrice = c.value.getMaxPrice();
                double chance = ((double) c.weight / totalWeight.get()) * 100;
                CompoundTag nbt = currentTrade.getOrCreateTagElement("display");
                ListTag list = nbt.getList("Lore", 8);
                MutableComponent chanceLabel = new TextComponent("Chance: ");
                chanceLabel.append(String.format("%.2f", chance));
                chanceLabel.append("%");
                list.add(StringTag.valueOf(Component.Serializer.toJson(chanceLabel.withStyle(ChatFormatting.YELLOW))));
                MutableComponent costLabel = new TextComponent("Cost: ");
                costLabel.append(minPrice + " - " + maxPrice);
                list.add(StringTag.valueOf(Component.Serializer.toJson(costLabel)));
                nbt.put("Lore", list);
                if (!JustEnoughVH.OMEGA_BLACK_MARKET_ITEMS.containsKey(currentTrade.getItem().getRegistryName()))
                    JustEnoughVH.OMEGA_BLACK_MARKET_ITEMS.put(currentTrade.getItem().getRegistryName(), minLevel);
                shardTrades.add(currentTrade);
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
                ProductEntryAccessor entry = (ProductEntryAccessor) c.value;
                ItemStack result = new ItemStack(c.value.getItem(), entry.getAmountMax());
                double chance = ((double) c.weight / totalWeight.get()) * 100;
                CompoundTag nbt = result.getOrCreateTagElement("display");
                ListTag list = nbt.getList("Lore", 8);
                MutableComponent chanceLabel = new TextComponent("Chance: ");
                chanceLabel.append(String.format("%.2f", chance));
                chanceLabel.append("%");
                list.add(StringTag.valueOf(Component.Serializer.toJson(chanceLabel.withStyle(ChatFormatting.YELLOW))));
                if (entry.getAmountMin() != entry.getAmountMax()) {
                    MutableComponent countLabel = new TextComponent("Count: ");
                    countLabel.append(entry.getAmountMin() + " - " + entry.getAmountMax());
                    list.add(StringTag.valueOf(Component.Serializer.toJson(countLabel)));
                }
                nbt.put("Lore", list);
                results.add(result);
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
                    rewardEntry.getItemPool().getPool().forEach(stack -> totalWeight.addAndGet(stack.weight));
                    rewardEntry.getItemPool().getPool().forEach(stack -> {
                        ItemStack result = new ItemStack(stack.value.getMatchingStack().getItem(), stack.value.getMaxCount());
                        double chance = ((double) stack.weight / totalWeight.get()) * 100;
                        CompoundTag nbt = result.getOrCreateTagElement("display");
                        ListTag list = nbt.getList("Lore", 8);
                        MutableComponent chanceLabel = new TextComponent("Chance: ");
                        chanceLabel.append(String.format("%.2f", chance));
                        chanceLabel.append("%");
                        list.add(StringTag.valueOf(Component.Serializer.toJson(chanceLabel.withStyle(ChatFormatting.YELLOW))));
                        if (stack.value.getMinCount() != stack.value.getMaxCount()) {
                            MutableComponent countLabel = new TextComponent("Count: ");
                            countLabel.append(stack.value.getMinCount() + " - " + stack.value.getMaxCount());
                            list.add(StringTag.valueOf(Component.Serializer.toJson(countLabel)));
                        }
                        nbt.put("Lore", list);
                        results.add(result);
                    });
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
                        ItemStack result = new ItemStack(stackInGroup.getItem(), amounts.getMax());
                        double chance = ((double) stack.weight / totalWeight.get()) * 100;
                        CompoundTag nbt = result.getOrCreateTagElement("display");
                        ListTag list = nbt.getList("Lore", 8);
                        MutableComponent chanceLabel = new TextComponent("Chance: ");
                        chanceLabel.append(String.format("%.2f", chance));
                        chanceLabel.append("%");
                        list.add(StringTag.valueOf(Component.Serializer.toJson(chanceLabel.withStyle(ChatFormatting.YELLOW))));
                        if (amounts.getMin() != amounts.getMax()) {
                            MutableComponent countLabel = new TextComponent("Count: ");
                            countLabel.append(amounts.getMin() + " - " + amounts.getMax());
                            list.add(StringTag.valueOf(Component.Serializer.toJson(countLabel)));
                        }
                        nbt.put("Lore", list);
                        stacks.add(result);
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

    protected static <T extends AbstractStatueLootConfig> List<LabeledLootInfo> getStatueLoot(T lootConfig) {
        List<LabeledLootInfo> toReturn = new ArrayList<>();
        List<ItemStack> items = new ArrayList<>();
        int interval = lootConfig.getInterval();
        int minCount = lootConfig.getRollRange().getMin();
        int maxCount = lootConfig.getRollRange().getMax();
        int totalWeight = lootConfig.getDrops().getTotalWeight();
        lootConfig.getDrops().forEach((k, v) -> {
            ItemStack result = k.generateItemStack();
            double chance = (v.doubleValue() / totalWeight) * 100;
            CompoundTag nbt = result.getOrCreateTagElement("display");
            ListTag list = nbt.getList("Lore", 8);
            MutableComponent chanceLabel = new TextComponent("Chance: ");
            chanceLabel.append(String.format("%.2f", chance));
            chanceLabel.append("%");
            list.add(StringTag.valueOf(Component.Serializer.toJson(chanceLabel.withStyle(ChatFormatting.YELLOW))));
            nbt.put("Lore", list);
            items.add(result);
        });
        toReturn.add(LabeledLootInfo.of(items,
                new TextComponent("Interval: " + interval),
                new TextComponent("Count: " + minCount + " - " + maxCount)
        ));
        return toReturn;
    }
}
