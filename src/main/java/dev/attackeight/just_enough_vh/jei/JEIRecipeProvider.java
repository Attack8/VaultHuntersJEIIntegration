package dev.attackeight.just_enough_vh.jei;

import com.mojang.logging.LogUtils;
import dev.attackeight.just_enough_vh.JustEnoughVH;
import io.github.a1qs.vaultadditions.config.vault.AbstractStatueLootConfig;
import iskallia.vault.config.OmegaSoulShardConfig;
import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.config.entry.ChanceItemStackEntry;
import iskallia.vault.config.entry.ConditionalChanceItemStackEntry;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.config.entry.recipe.ConfigForgeRecipe;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.config.recipe.ForgeRecipesConfig;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.crafting.recipe.*;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.jei.lootinfo.LootInfo;
import iskallia.vault.tags.ModItemTags;
import iskallia.vault.util.data.WeightedList;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
        if (!ForgeRegistries.ITEMS.containsKey(productEntry.value.getItem().getRegistryName()))
            return ItemStack.EMPTY;
        return formatItemStack(productEntry.value.getItem(), productEntry.value.amountMin,
                productEntry.value.amountMax, productEntry.weight, totalWeight);
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
                        offerInfo.getMiddle().getMax(), offerInfo.getRight().intValue(), totalWeight.get(), currentOffer.getCount()));
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
                        c.value.getMaxPrice(), c.weight, totalWeight.get(), c.value.getItemEntry().AMOUNT));
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
                        c.value.getMaxPrice(), c.weight, totalWeight.get(), c.value.getItemEntry().AMOUNT));
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
            k.forEach(c -> results.add(formatItemStack(c.value.getItem(), c.value.amountMin,
                        c.value.amountMin, c.weight, totalWeight.get())));
            lootInfo.add(LabeledLootInfo.of(results, new TextComponent("Mod: " + mod), null));
        });
        return lootInfo;
    }

    protected static List<LabeledLootInfo> getBountyRewards() {
        List<LabeledLootInfo> toReturn = new ArrayList<>();
        ModConfigs.REWARD_CONFIG.POOLS.forEach((id, entry) -> {
            TreeMap<Integer, LabeledLootInfo> lootInfo = new TreeMap<>();
            if (!id.equals("submission")) {
                LogUtils.getLogger().info("Generating bounty rewards for: {}", id);
                entry.forEach((minLevel, rewards) -> {
                    LogUtils.getLogger().info("Generating bounty rewards for: {}, Level {}", id, minLevel);
                    AtomicInteger totalWeight = new AtomicInteger();
                    List<ItemStack> results = new ArrayList<>();
                    IntRangeEntry vaultExp = rewards.vaultExp;
                    rewards.getItemPool().getPool().forEach(stack -> totalWeight.addAndGet(stack.weight));
                    rewards.getItemPool().getPool().forEach(stack -> {
                        LogUtils.getLogger().info("Entry: {}", stack.value.getMatchingStack().getItem().getRegistryName());
                        results.add(formatItemStack(stack.value.getMatchingStack().getItem(), stack.value.getMinCount(),
                                stack.value.getMaxCount(), stack.weight, totalWeight.get()));
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
        TreeMap<Integer, List<LabeledLootInfo>> lootInfo = new TreeMap<>();
        ModConfigs.VAULT_ALTAR_INGREDIENTS.getLEVELS().forEach((minLevel, entry) -> {
            List<LabeledLootInfo> pool = new ArrayList<>();
            entry.forEach((slot, rewards) -> {
                AtomicInteger totalWeight = new AtomicInteger();
                List<List<ItemStack>> results = new ArrayList<>();
                rewards.forEach(stack -> totalWeight.addAndGet(stack.weight));
                rewards.forEach(stack -> {
                    IntRangeEntry amounts = stack.value.amount;
                    List<ItemStack> stacks = new ArrayList<>();
                    for (ItemStack stackInGroup : stack.value.getItems()) {
                        stacks.add(formatItemStack(stackInGroup.getItem(), amounts.getMin(),
                                amounts.getMax(), stack.weight, totalWeight.get()));
                    }
                    results.add(stacks);
                });
                pool.add(new LabeledLootInfo(results, new TextComponent("Reward Pool: " + slot + " Level: " + minLevel + "+")));
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
                    stacks.add(formatItemStack(stackInGroup.getItem(), stackInGroup.amountMin,
                            stackInGroup.amountMax, weight.intValue(), totalWeight.get()));
                }
                results.add(stacks);
                pool.add(new LabeledLootInfo(results, new TextComponent(" Level: " + levelEntry.level + "+")));
            });
            lootInfo.put(levelEntry.getLevel(), pool);
        });
        lootInfo.forEach((i, n) -> toReturn.addAll(n));
        return toReturn;
    }

    protected static List<RecyclerRecipe> getRecyclerRecipes() {
        List<RecyclerRecipe> toReturn = new ArrayList<>();
        VaultRecyclerConfig.RecyclerOutput gearOutput = ModConfigs.VAULT_RECYCLER.getGearRecyclingOutput();
        for (VaultGearRarity rarity : VaultGearRarity.values()) {
            if (rarity.equals(VaultGearRarity.UNIQUE) || rarity.equals(VaultGearRarity.SPECIAL)) continue;
            List<ItemStack> pieceStack = generatePieceStack(rarity);
            List<ItemStack> outputs = List.of(
                    addLoreToRecyclerOutput(gearOutput.getMainOutput(), rarity),
                    addLoreToRecyclerOutput(gearOutput.getExtraOutput1(), rarity),
                    addLoreToRecyclerOutput(gearOutput.getExtraOutput2(), rarity)
            );
            toReturn.add(new RecyclerRecipe(pieceStack, outputs));
        }

        VaultRecyclerConfig.RecyclerOutput trinketOutput = ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput();
        toReturn.add(RecyclerRecipe.of(new ItemStack(ModItems.TRINKET), List.of(
                addLoreToRecyclerOutput(trinketOutput.getMainOutput()),
                addLoreToRecyclerOutput(trinketOutput.getExtraOutput1()),
                addLoreToRecyclerOutput(trinketOutput.getExtraOutput2())
        )));

        VaultRecyclerConfig.RecyclerOutput jewelOutput = ModConfigs.VAULT_RECYCLER.getJewelRecyclingOutput();
        toReturn.add(RecyclerRecipe.of(new ItemStack(ModItems.JEWEL), List.of(
                addLoreToRecyclerOutput(jewelOutput.getMainOutput()),
                addLoreToRecyclerOutput(jewelOutput.getExtraOutput1()),
                addLoreToRecyclerOutput(jewelOutput.getExtraOutput2())
        )));

        VaultRecyclerConfig.RecyclerOutput inscriptionOutput = ModConfigs.VAULT_RECYCLER.getInscriptionRecyclingOutput();
        toReturn.add(RecyclerRecipe.of(new ItemStack(ModItems.INSCRIPTION), List.of(
                addLoreToRecyclerOutput(inscriptionOutput.getMainOutput()),
                addLoreToRecyclerOutput(inscriptionOutput.getExtraOutput1()),
                addLoreToRecyclerOutput(inscriptionOutput.getExtraOutput2())
        )));

        VaultRecyclerConfig.RecyclerOutput charmOutput = ModConfigs.VAULT_RECYCLER.getCharmRecyclingOutput();
        toReturn.add(RecyclerRecipe.of(new ItemStack(ModItems.VAULT_GOD_CHARM), List.of(
                addLoreToRecyclerOutput(charmOutput.getMainOutput()),
                addLoreToRecyclerOutput(charmOutput.getExtraOutput1()),
                addLoreToRecyclerOutput(charmOutput.getExtraOutput2())
        )));

        VaultRecyclerConfig.RecyclerOutput voidStoneOutput = ModConfigs.VAULT_RECYCLER.getVoidStoneRecyclingOutput();
        toReturn.add(RecyclerRecipe.of(new ItemStack(ModItems.VOID_STONE), List.of(
                addLoreToRecyclerOutput(voidStoneOutput.getMainOutput()),
                addLoreToRecyclerOutput(voidStoneOutput.getExtraOutput1()),
                addLoreToRecyclerOutput(voidStoneOutput.getExtraOutput2())
        )));

        return toReturn;
    }

    private static ItemStack addLoreToRecyclerOutput(ChanceItemStackEntry entry) {
        return addLoreToRecyclerOutput(entry, null);
    }

    private static ItemStack addLoreToRecyclerOutput(ChanceItemStackEntry entry, @Nullable VaultGearRarity rarity) {
        AtomicReference<ItemStack> toReturn = new AtomicReference<>(entry.getMatchingStack());

        AtomicReference<Float> chance = new AtomicReference<>(entry.getChance());

        if (rarity != null && chance.get() < 1f) {
            chance.updateAndGet(v -> v + ModConfigs.VAULT_RECYCLER.getAdditionalOutputRarityChance(rarity));
        }

        if (entry instanceof ConditionalChanceItemStackEntry ccise) {
            ccise.getConditionalOutputs().forEach(((condition, chanceItemStackEntry) -> {
                if (condition.matches(rarity, false) || condition.matches(rarity, true)) {
                    toReturn.set(chanceItemStackEntry.getMatchingStack());
                    chance.set(chanceItemStackEntry.getChance());
                }
            }));
        }

        CompoundTag nbt = toReturn.get().getOrCreateTagElement("display");
        ListTag list = nbt.getList("Lore", 8);
        MutableComponent chanceLabel = new TextComponent("Chance: ");

        chanceLabel.append(String.format("%.0f", chance.get() * 100));
        chanceLabel.append("%");
        list.add(StringTag.valueOf(Component.Serializer.toJson(chanceLabel.withStyle(ChatFormatting.YELLOW))));

        if (entry.getMinCount() != entry.getMaxCount()) {
            MutableComponent countLabel = new TextComponent("Count: ");
            countLabel.append(entry.getMinCount() + " - " + entry.getMaxCount());
            list.add(StringTag.valueOf(Component.Serializer.toJson(countLabel)));
        }
        nbt.put("Lore", list);
        return chance.get() == 0f ? ItemStack.EMPTY : toReturn.get();
    }

    private static List<ItemStack> generatePieceStack(VaultGearRarity rarity) {
        List<ItemStack> toReturn = new ArrayList<>();
        ITag<Item> vaultGear = ForgeRegistries.ITEMS.tags().getTag(ModItemTags.VAULT_GEAR);
        for (Item gear : vaultGear) {
            ItemStack itemStack = new ItemStack(gear);

            String rollType = rarity.toString().toLowerCase();

            char firstLetter = Character.toTitleCase(rollType.substring(0, 1).charAt(0));
            String restLetters = rollType.substring(1);
            rollType = firstLetter + restLetters;

            VaultGearData data = VaultGearData.read(itemStack);
            data.createOrReplaceAttributeValue(ModGearAttributes.GEAR_ROLL_TYPE, rollType);
            data.write(itemStack);

            toReturn.add(itemStack);
        }
        return toReturn;
    }

    protected static <T extends AbstractStatueLootConfig> List<LabeledLootInfo> getStatueLoot(T lootConfig) {
        List<LabeledLootInfo> toReturn = new ArrayList<>();
        List<ItemStack> items = new ArrayList<>();
        int interval = lootConfig.getInterval();
        int minCount = lootConfig.getRollRange().getMin();
        int maxCount = lootConfig.getRollRange().getMax();
        int totalWeight = lootConfig.getDrops().getTotalWeight();
        lootConfig.getDrops().forEach((product, weight) ->
            items.add(formatItemStack(product.getItem(), product.amountMin, product.amountMax, weight.intValue(), totalWeight)));
        toReturn.add(LabeledLootInfo.of(items,
                new TextComponent("Interval: " + interval),
                new TextComponent("Count: " + minCount + " - " + maxCount)
        ));
        return toReturn;
    }

    private static ItemStack formatItemStack(ItemLike item, int amountMin, int amountMax, int weight, int totalWeight, @Nullable Integer amount) {
        ItemStack result = new ItemStack(item, amount == null ? amountMax : amount);
        double chance = ((double) weight / totalWeight) * 100;
        CompoundTag nbt = result.getOrCreateTagElement("display");
        ListTag list = nbt.getList("Lore", 8);
        MutableComponent chanceLabel = new TextComponent("Chance: ");
        chanceLabel.append(String.format("%.2f", chance));
        chanceLabel.append("%");
        list.add(StringTag.valueOf(Component.Serializer.toJson(chanceLabel.withStyle(ChatFormatting.YELLOW))));
        if (amountMin != amountMax) {
            MutableComponent countLabel = new TextComponent(amount == null ? "Count: " : "Cost: ");
            countLabel.append(amountMin + " - " + amountMax);
            list.add(StringTag.valueOf(Component.Serializer.toJson(countLabel)));
        }
        nbt.put("Lore", list);
        return result;
    }

    private static ItemStack formatItemStack(ItemLike item, int amountMin, int amountMax, int weight, int totalWeight) {
        return formatItemStack(item, amountMin, amountMax, weight, totalWeight, null);
    }
}
