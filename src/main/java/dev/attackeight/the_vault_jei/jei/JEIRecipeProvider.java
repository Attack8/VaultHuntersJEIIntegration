package dev.attackeight.the_vault_jei.jei;

import com.mojang.logging.LogUtils;
import dev.attackeight.the_vault_jei.TheVaultJEI;
import dev.attackeight.the_vault_jei.mixin.*;
import iskallia.vault.config.OmegaSoulShardConfig;
import iskallia.vault.config.ShopPedestalConfig;
import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.config.entry.recipe.ConfigForgeRecipe;
import iskallia.vault.config.entry.vending.ProductEntry;
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

    protected static List<ForgeItem> getCatalystRecipes() {
        return getForgeRecipes(ModConfigs.CATALYST_RECIPES.getConfigRecipes());
    }

    protected static List<ForgeItem> getToolRecipes() {
        List<ForgeItem> recipes = getForgeRecipes(ModConfigs.TOOL_RECIPES.getConfigRecipes());
        recipes.addAll(getForgeRecipes(ModConfigs.JEWEL_RECIPES.getConfigRecipes()));
        return recipes;
    }

    protected static List<ForgeItem> getGearRecipes(){
        List<ForgeItem> recipes = getForgeRecipes(ModConfigs.GEAR_RECIPES.getConfigRecipes());
        recipes.addAll(getForgeRecipes(ModConfigs.TRINKET_RECIPES.getConfigRecipes()));
        return recipes;
    }

    protected static List<ForgeItem> getInscriptionRecipes() {
        return getForgeRecipes(ModConfigs.INSCRIPTION_RECIPES.getConfigRecipes());
    }

    private static <R extends VaultForgeRecipe, T extends ConfigForgeRecipe<R>> List<ForgeItem> getForgeRecipes(List<T> configRecipes) {
        List<ForgeItem> recipes = new ArrayList<>();
        configRecipes.forEach(b -> recipes.add(new ForgeItem(b.makeRecipe().getInputs(), b.makeRecipe().getDisplayOutput(100))));
        return recipes;
    }

    protected static List<LootInfo> getMysteryBoxLoot() {
        List<LootInfo> lootInfo = new ArrayList<>();
        List<ItemStack> loot = new ArrayList<>();
        int total = ModConfigs.MYSTERY_BOX.POOL.getTotalWeight();
        ModConfigs.MYSTERY_BOX.POOL.forEach(b -> loot.add(addWeight(b, total)));
        lootInfo.add(new LootInfo(loot));
        return lootInfo;
    }

    protected static List<LootInfo> getMysteryEggLoot() {
        List<LootInfo> lootInfo = new ArrayList<>();
        List<ItemStack> loot = new ArrayList<>();
        int total = ModConfigs.MYSTERY_EGG.POOL.getTotalWeight();
        ModConfigs.MYSTERY_EGG.POOL.forEach(b -> loot.add(addWeight(b, total)));
        lootInfo.add(new LootInfo(loot));
        return lootInfo;
    }

    protected static List<LootInfo> getHostileEggLoot() {
        List<LootInfo> lootInfo = new ArrayList<>();
        List<ItemStack> loot = new ArrayList<>();
        int total = ModConfigs.MYSTERY_HOSTILE_EGG.POOL.getTotalWeight();
        ModConfigs.MYSTERY_HOSTILE_EGG.POOL.forEach(b -> loot.add(addWeight(b, total)));
        lootInfo.add(new LootInfo(loot));
        return lootInfo;
    }

    protected static List<LootInfo> getPandorasBoxLoot() {
        List<LootInfo> lootInfo = new ArrayList<>();
        List<ItemStack> loot = new ArrayList<>();
        int total = ModConfigs.PANDORAS_BOX.POOL.getTotalWeight();
        ModConfigs.PANDORAS_BOX.POOL.forEach(b -> loot.add(addWeight(b, total)));
        lootInfo.add(new LootInfo(loot));
        return lootInfo;
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

    protected static List<LabeledLootInfo> getShopPedestalLoot() {
        List<LabeledLootInfo> lootInfo = new ArrayList<>();
        List<Pair<List<Triple<ItemStack, IntRangeEntry, Double>>, Integer>> pedestalInfo;
        try {
            pedestalInfo = (List<Pair<List<Triple<ItemStack, IntRangeEntry, Double>>, Integer>>)
                    ModConfigs.SHOP_PEDESTAL.getClass().getMethod("getTrades").invoke(ModConfigs.SHOP_PEDESTAL);
        } catch (Exception e) {
            TheVaultJEI.LOGGER.error(e.toString());
            return new ArrayList<>();
        }
        pedestalInfo.forEach(tierInfo -> {
            List<Triple<ItemStack, IntRangeEntry, Double>> tier = tierInfo.getLeft();
            LogUtils.getLogger().info("tier Length: {}", tier.size());
            int minLevel = tierInfo.getRight();
            AtomicInteger totalWeight = new AtomicInteger();
            tier.forEach(d -> totalWeight.addAndGet(d.getRight().intValue()));
            List<ItemStack> offers = new ArrayList<>();
            tier.forEach(offerInfo -> {
                LogUtils.getLogger().info("Item Name: {}", offerInfo.getLeft().getDisplayName());
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
                offers.add(currentOffer);
            });
            lootInfo.add(new LabeledLootInfo(offers, new TextComponent("Level " + minLevel + "+ ")));
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
                shardTrades.add(currentTrade);
            });
            lootInfo.add(new LabeledLootInfo(shardTrades, new TextComponent("Common Slot: Level " + minLevel + "+ "), new TextComponent("Soul Trade Price: " + randomPrice)));
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
                shardTrades.add(currentTrade);
            });
            lootInfo.add(new LabeledLootInfo(shardTrades, new TextComponent("Omega Slot: Level " + minLevel + "+ ")));
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
            lootInfo.add(new LabeledLootInfo(results, new TextComponent("Mod: " + mod)));
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
                    lootInfo.put(minLevel, new LabeledLootInfo(results,
                            new TextComponent("Reward Pool: " + id + " Level: " + minLevel + "+"),
                            new TextComponent("Vault Exp Reward: " + vaultExp.getMin() + "-" + vaultExp.getMax())));
                });
            }
            lootInfo.forEach((n,i) -> toReturn.add(i));
        });
        return toReturn;
    }

    protected static List<LabeledIngredientPool> getAltarIngredients() {
        List<LabeledIngredientPool> toReturn = new ArrayList<>();
        AltarIngredientAccessor rewardPools = (AltarIngredientAccessor) ModConfigs.VAULT_ALTAR_INGREDIENTS;
        TreeMap<Integer, List<LabeledIngredientPool>> lootInfo = new TreeMap<>();
        rewardPools.getLEVELS().forEach((minLevel, entry) -> {
            List<LabeledIngredientPool> pool = new ArrayList<>();
            entry.forEach((slot, rewards) -> {
                AtomicInteger totalWeight = new AtomicInteger();
                List<ItemStack> results = new ArrayList<>();
                rewards.forEach(stack -> totalWeight.addAndGet(stack.weight));
                rewards.forEach(stack -> {
                    IntRangeEntry amounts = ((IngredientAmountAccessor) stack.value).getAmount();
                    ItemStack result = new ItemStack(stack.value.getItems().get(0).getItem(), amounts.getMax());
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
                    results.add(result);
                });
                pool.add(new LabeledIngredientPool(results,
                        new TextComponent("Reward Pool: " + slot + " Level: " + minLevel + "+")));
            });
            lootInfo.put(minLevel, pool);
        });
        lootInfo.forEach((i, n) -> toReturn.addAll(n));
        return toReturn;
    }

}
