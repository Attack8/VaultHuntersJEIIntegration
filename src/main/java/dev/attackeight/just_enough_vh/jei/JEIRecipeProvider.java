package dev.attackeight.just_enough_vh.jei;

import dev.attackeight.just_enough_vh.JustEnoughVH;
import dev.attackeight.just_enough_vh.mixin.raid.*;
import io.github.a1qs.vaultadditions.config.vault.AbstractStatueLootConfig;
import iskallia.vault.config.LootInfoConfig;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.block.entity.challenge.raid.action.*;
import iskallia.vault.config.OmegaSoulShardConfig;
import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.config.entry.ChanceItemStackEntry;
import iskallia.vault.config.entry.ConditionalChanceItemStackEntry;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.config.entry.recipe.ConfigForgeRecipe;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.config.recipe.ForgeRecipesConfig;
import iskallia.vault.core.world.loot.LootTableInfo;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.jei.lootinfo.LootInfo;
import iskallia.vault.tags.ModItemTags;
import iskallia.vault.util.data.WeightedList;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class JEIRecipeProvider {

    public static <R extends VaultForgeRecipe, T extends ConfigForgeRecipe<R>, S extends ForgeRecipesConfig<T, R>> List<ForgeItem> getForgeRecipes(S config) {
        List<ForgeItem> recipes = new ArrayList<>();
        config.getConfigRecipes().forEach(b -> recipes.add(new ForgeItem(b.makeRecipe().getInputs(), b.makeRecipe().getDisplayOutput(100))));
        return recipes;
    }

    public static List<LootInfo> getFromPool(WeightedList<ProductEntry> pool) {
        List<ItemStack> loot = new ArrayList<>();
        int total = pool.getTotalWeight();
        pool.forEach(b -> loot.add(addWeight(b, total)));
        return List.of(new LootInfo(loot));
    }

    public static ItemStack addWeight(WeightedList.Entry<ProductEntry> productEntry, int totalWeight) {
        if (!ForgeRegistries.ITEMS.containsKey(productEntry.value.getItem().getRegistryName()))
            return ItemStack.EMPTY;
        return formatItemStack(productEntry.value.generateItemStack(), productEntry.value.amountMin,
                productEntry.value.amountMax, productEntry.weight, totalWeight);
    }

    public static List<LabeledLootInfo> labelDefaultLootInfo(ResourceLocation location) {
        List<LabeledLootInfo> toReturn = new ArrayList<>();
        Set<ResourceLocation> lootTableKeys = getLootTableKeysForLootInfoGroup(location);
        lootTableKeys.forEach(lootTable -> {
            List<ItemStack> itemStacks = new ArrayList<>();
            LootTableInfo.getItemsForLootTableKey(lootTable).forEach(item ->
                itemStacks.add(ForgeRegistries.ITEMS.getValue(item).getDefaultInstance()));


//            Version version = Version.v1_0;
//            LootTableKey key = VaultRegistry.LOOT_TABLE.getKey(lootTable);
//
//            if (location.equals(VaultMod.id("treasure_chest"))) {
//                LootTableGenerator generator = new LootTableGenerator(version, key, 0.0F);
//                generator.setSource(null);
//                generator.getItems().forEachRemaining(itemStacks::add);
//            } else {
//                TieredLootTableGenerator generator = new TieredLootTableGenerator(version, key, 0.0f, 0.0f, 54);
//                generator.setSource(null);
//                generator.getTable().getEntries().forEach((entry) -> {
//                    iskallia.vault.core.util.WeightedList<LootEntry> entries = entry.getPool().flatten();
//                    double totalWeight = entries.getTotalWeight();
//                    entries.forEach((lootEntry, weight) -> {
//
//                    });
//
//                });
//            }

            String[] splitLocation = lootTable.getPath().split("_");
            String pool = splitLocation[splitLocation.length - 1].replace("lvl", "");
            Component label = new TextComponent("Pool: " + pool);
            try {
                Integer.parseInt(pool);
                label = new TextComponent("Level: " + pool + "+");
            } catch (NumberFormatException ignored) {}

            toReturn.add(LabeledLootInfo.of(itemStacks, label, null));
        });

        return toReturn;
    }

    public static RecipeType<LabeledLootInfo> adapt(RecipeType<LootInfo> oldType) {
        return RecipeType.create(JustEnoughVH.ID, oldType.getUid().getPath(), LabeledLootInfo.class);
    }

    public static Set<ResourceLocation> getLootTableKeysForLootInfoGroup(ResourceLocation lootInfoGroupKey) {
        LootInfoConfig.LootInfo lootInfo = ModConfigs.LOOT_INFO_CONFIG.getLootInfoMap().get(lootInfoGroupKey);
        return lootInfo != null ? lootInfo.getLootTableKeys() : Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public static List<LabeledLootInfo> getShopPedestalLoot() {
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
                offers.add(formatItemStack(currentOffer, offerInfo.getMiddle().getMin(),
                        offerInfo.getMiddle().getMax(), offerInfo.getRight().intValue(), totalWeight.get(), currentOffer.getCount()));
            });
            lootInfo.add(LabeledLootInfo.of(offers, new TextComponent("Level " + minLevel + "+ "), null));
        });
        return lootInfo;
    }

    public static List<LabeledLootInfo> getBlackMarketLoot() {
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
                shardTrades.add(formatItemStack(currentTrade, c.value.getMinPrice(),
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
                shardTrades.add(formatItemStack(currentTrade, c.value.getMinPrice(),
                        c.value.getMaxPrice(), c.weight, totalWeight.get(), c.value.getItemEntry().AMOUNT));
            });
            lootInfo.add(LabeledLootInfo.of(shardTrades, new TextComponent("Omega Slot: Level " + minLevel + "+ "), null));
        });
        return lootInfo;
    }

    public static List<LabeledLootInfo> getModBoxLoot() {
        List<LabeledLootInfo> lootInfo = new ArrayList<>();
        ModConfigs.MOD_BOX.POOL.forEach((mod, k) -> {
            AtomicInteger totalWeight = new AtomicInteger();
            List<ItemStack> results = new ArrayList<>();
            k.forEach(d -> totalWeight.addAndGet(d.weight));
            k.forEach(c -> results.add(formatItemStack(c.value.generateItemStack(), c.value.amountMin,
                        c.value.amountMin, c.weight, totalWeight.get())));
            lootInfo.add(LabeledLootInfo.of(results, new TextComponent("Mod: " + mod), null));
        });
        return lootInfo;
    }

    public static List<LabeledLootInfo> getBountyRewards() {
        List<LabeledLootInfo> toReturn = new ArrayList<>();
        ModConfigs.REWARD_CONFIG.POOLS.forEach((id, entry) -> {
            TreeMap<Integer, LabeledLootInfo> lootInfo = new TreeMap<>();
            if (!id.equals("submission")) {
                entry.forEach((minLevel, rewards) -> {
                    AtomicInteger totalWeight = new AtomicInteger();
                    List<ItemStack> results = new ArrayList<>();
                    IntRangeEntry vaultExp = rewards.vaultExp;
                    rewards.getItemPool().getPool().forEach(stack -> totalWeight.addAndGet(stack.weight));
                    rewards.getItemPool().getPool().forEach(stack ->
                        results.add(formatItemStack(stack.value.getMatchingStack(), stack.value.getMinCount(),
                                stack.value.getMaxCount(), stack.weight, totalWeight.get())));
                    lootInfo.put(minLevel, LabeledLootInfo.of(results,
                            new TextComponent("Reward Pool: " + id + " Level: " + minLevel + "+"),
                            new TextComponent("Vault Exp Reward: " + vaultExp.getMin() + "-" + vaultExp.getMax())));
                });
            }
            lootInfo.forEach((n,i) -> toReturn.add(i));
        });
        return toReturn;
    }

    public static List<LabeledLootInfo> getAltarIngredients() {
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
                        stacks.add(formatItemStack(stackInGroup, amounts.getMin(),
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

    public static List<LabeledLootInfo> getMaterialBoxLoot() {
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
                    stacks.add(formatItemStack(stackInGroup.generateItemStack(), stackInGroup.amountMin,
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

    public static List<RecyclerRecipe> getRecyclerRecipes() {
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
        toReturn.add(getRecyclerRecipe(new ItemStack(ModItems.TRINKET), trinketOutput));

        VaultRecyclerConfig.RecyclerOutput jewelOutput = ModConfigs.VAULT_RECYCLER.getJewelRecyclingOutput();
        toReturn.add(getRecyclerRecipe(new ItemStack(ModItems.JEWEL), jewelOutput));

        VaultRecyclerConfig.RecyclerOutput inscriptionOutput = ModConfigs.VAULT_RECYCLER.getInscriptionRecyclingOutput();
        toReturn.add(getRecyclerRecipe(new ItemStack(ModItems.INSCRIPTION), inscriptionOutput));

        VaultRecyclerConfig.RecyclerOutput charmOutput = ModConfigs.VAULT_RECYCLER.getCharmRecyclingOutput();
        toReturn.add(getRecyclerRecipe(new ItemStack(ModItems.VAULT_GOD_CHARM), charmOutput));

        VaultRecyclerConfig.RecyclerOutput voidStoneOutput = ModConfigs.VAULT_RECYCLER.getVoidStoneRecyclingOutput();
        toReturn.add(getRecyclerRecipe(new ItemStack(ModItems.VOID_STONE), voidStoneOutput));

        return toReturn;
    }

    public static RecyclerRecipe getRecyclerRecipe(ItemStack input, VaultRecyclerConfig.RecyclerOutput output) {
        return RecyclerRecipe.of(input, List.of(
                addLoreToRecyclerOutput(output.getMainOutput()),
                addLoreToRecyclerOutput(output.getExtraOutput1()),
                addLoreToRecyclerOutput(output.getExtraOutput2())));
    }

    public static ItemStack addLoreToRecyclerOutput(ChanceItemStackEntry entry) {
        return addLoreToRecyclerOutput(entry, null);
    }

    public static ItemStack addLoreToRecyclerOutput(ChanceItemStackEntry entry, @Nullable VaultGearRarity rarity) {
        AtomicReference<ItemStack> toReturn = new AtomicReference<>(entry.getMatchingStack());

        AtomicReference<Float> chance = new AtomicReference<>(entry.getChance());

        if (rarity != null && chance.get() < 1f) {
            chance.updateAndGet(v -> v + ModConfigs.VAULT_RECYCLER.getAdditionalOutputRarityChance(rarity));
        }

        try {
            if (entry instanceof ConditionalChanceItemStackEntry ccise) {
                ccise.getConditionalOutputs().forEach(((condition, chanceItemStackEntry) -> {
                    if (condition.matches(rarity, false) || condition.matches(rarity, true)) {
                        toReturn.set(chanceItemStackEntry.getMatchingStack());
                        chance.set(chanceItemStackEntry.getChance());
                    }
                }));
            }
        } catch (NullPointerException ignored) {}

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

    public static List<ItemStack> generatePieceStack(VaultGearRarity rarity) {
        List<ItemStack> toReturn = new ArrayList<>();
        ITag<Item> vaultGear = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(ModItemTags.VAULT_GEAR);
        for (Item gear : vaultGear) {
            ItemStack itemStack = new ItemStack(gear);

            String rollType = rarity.toString().toLowerCase();

            char firstLetter = Character.toTitleCase(rollType.substring(0, 1).charAt(0));
            String restLetters = rollType.substring(1);
            rollType = firstLetter + restLetters;

            AttributeGearData data = AttributeGearData.read(itemStack);
            data.createOrReplaceAttributeValue(ModGearAttributes.GEAR_ROLL_TYPE, rollType);
            data.write(itemStack);

            toReturn.add(itemStack);
        }
        return toReturn;
    }

    @SuppressWarnings("unchecked")
    protected static List<LabeledLootInfo> getChallengeActionLoot() {
        List<LabeledLootInfo> lootInfo = new ArrayList<>();
        Map<String, ChallengeAction<?>> raidValues = ((RaidActionsConfigAccessor)ModConfigs.RAID_ACTIONS).getValues();

        for (var rv : raidValues.entrySet()) {
            String lootTableName = rv.getKey();
            ChallengeAction.Config config = rv.getValue().getConfig();
            if (config instanceof PoolChallengeAction.Config) {
                var poolConfig = (PoolChallengeActionConfigAccessor) config;
                LevelEntryList<LevelEntryList.ILevelEntry> pools = poolConfig.getPools();
                for (LevelEntryList.ILevelEntry levelPool : pools) {
                    var rewardItems = getFromChallengeActionPool(((LevelPoolAccessor) levelPool).getPool());
                    lootInfo.add(LabeledLootInfo.of(rewardItems,
                            new TextComponent(lootTableName + " Level: " + levelPool.getLevel() + "+"),null)
                    );
                }
            }
        }

        return lootInfo;
    }

    protected static List<ItemStack> getFromChallengeActionPool(iskallia.vault.core.util.WeightedList<ChallengeAction<?>> pool) {
        List<ItemStack> loot = new ArrayList<>();
        double total = pool.getTotalWeight();
        pool.forEach((action, weight) -> loot.add(createChallengeActionStack(action, weight, total)));
        return loot;
    }

    protected static ItemStack createChallengeActionStack(ChallengeAction<?> challengeAction, double weight, double totalWeight) {
        ChallengeAction.Config config = challengeAction.getConfig();
        if (config instanceof ReferenceChallengeAction.Config) {
            var refConfig = (ReferenceChallengeActionConfigAccessor)config;
            var derefConfig = ((RaidActionsConfigAccessor) ModConfigs.RAID_ACTIONS).getValues().get(refConfig.getPath());
            return createChallengeActionStack(derefConfig, weight, totalWeight);
        }
        if (config instanceof FloatingItemRewardChallengeAction.Config) {
            var floatConfig = (FloatingItemRewardChallengeActionConfigAccessor)config;
            var is = formatItemStack(floatConfig.getItem(), floatConfig.getItem().getCount(), floatConfig.getItem().getCount(), weight, totalWeight);
            is.setHoverName(new TextComponent(floatConfig.getName()));
            return is;
        }
        if (config instanceof TileRewardChallengeAction.Config) {
            var tileConfig = (TileRewardChallengeConfigAccessor)config;
            Block block = Blocks.BARRIER;
            var state = tileConfig.getTile().getState().asWhole().orElse(null);
            if (state != null){
                block = state.getBlock();
                if (block == ModBlocks.PLACEHOLDER){
                    PlaceholderBlock.Type type = state.getValue(PlaceholderBlock.TYPE);
                    switch (type) {
                        case LIVING_CHEST, LIVING_CHEST_GUARANTEED, LIVING_CHEST_WATERLOGGED -> block = ModBlocks.LIVING_CHEST;
                        case ORNATE_CHEST , ORNATE_CHEST_GUARANTEED, ORNATE_CHEST_WATERLOGGED -> block = ModBlocks.ORNATE_CHEST;
                        case GILDED_CHEST , GILDED_CHEST_GUARANTEED, GILDED_CHEST_WATERLOGGED -> block = ModBlocks.GILDED_CHEST;
                        case WOODEN_CHEST , WOODEN_CHEST_GUARANTEED, WOODEN_CHEST_WATERLOGGED -> block = ModBlocks.WOODEN_CHEST;
                        case COIN_STACKS , COIN_STACKS_GUARANTEED, COIN_STACKS_WATERLOGGED -> block = ModBlocks.COIN_PILE;
                        case ORE  -> block = ModBlocks.LARIMAR_ORE;
                        default -> {/*keep barrier*/}
                    }
                }
            }
            var is = formatItemStack(block.asItem().getDefaultInstance(), tileConfig.getCount(), tileConfig.getCount(), weight, totalWeight);
            is.setHoverName(new TextComponent(tileConfig.getName()));
            return is;
        }
        if (config instanceof VanillaAttributeChallengeAction.Config) {
            var vanillaAttrConfig = (VanillaAttributeChallengeActionConfigAccessor) config;
            AttributeModifier.Operation operation = vanillaAttrConfig.getOperation();
            double amount = vanillaAttrConfig.getAmount().get(0).getMin(); // this is a range, but from configs it doesn't seem to be used
            String name = vanillaAttrConfig.getName();
            Attribute attribute = vanillaAttrConfig.getAttribute();
            int textColor = ((ChallengeActionConfigAccessor)config).getTextColor();
            Component prefix = new TextComponent(switch (operation) {
                case ADDITION -> String.format("%+.0f", amount);
                case MULTIPLY_BASE -> String.format("%+.0f%%", amount * 100.0);
                case MULTIPLY_TOTAL -> "×" + (1.0 + amount);
            });
            MutableComponent text = new TextComponent("").append(prefix);
            if (name == null) {
                Component suffix = new TranslatableComponent(attribute.getDescriptionId());
                text = text.append(config instanceof VanillaAttributeChallengeAction.Config ? " Mob " : " Player ").append(suffix);
            } else {
                text = text.append(" ").append(new TextComponent(name));
            }
            var hover = text.setStyle(Style.EMPTY.withColor(textColor));

            var is = formatItemStack(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.EMPTY), 1, 1, weight, totalWeight);
            is.setHoverName(hover);
            return is;
        }
        var is =  formatItemStack(Blocks.BARRIER.asItem().getDefaultInstance(), 1, 1, weight, totalWeight);
        is.setHoverName(new TextComponent("ERR - UNSUPPORTED"));
        return is;
    }

    public static <T extends AbstractStatueLootConfig> List<LabeledLootInfo> getStatueLoot(T lootConfig) {
        List<LabeledLootInfo> toReturn = new ArrayList<>();
        List<ItemStack> items = new ArrayList<>();
        int interval = lootConfig.getInterval();
        int minCount = lootConfig.getRollRange().getMin();
        int maxCount = lootConfig.getRollRange().getMax();
        int totalWeight = lootConfig.getDrops().getTotalWeight();
        lootConfig.getDrops().forEach((product, weight) ->
            items.add(formatItemStack(product.generateItemStack(), product.amountMin, product.amountMax, weight.intValue(), totalWeight)));
        toReturn.add(LabeledLootInfo.of(items,
                new TextComponent("Interval: " + interval),
                new TextComponent("Count: " + minCount + " - " + maxCount)
        ));
        return toReturn;
    }

    private static ItemStack formatItemStack(ItemStack item, int amountMin, int amountMax, double weight, double totalWeight, @Nullable Integer amount) {
        ItemStack result = item.copy();
        result.setCount(amount == null ? amountMax : amount);
        double chance =  weight / totalWeight * 100;
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

    private static ItemStack formatItemStack(ItemStack item, int amountMin, int amountMax, double weight, double totalWeight) {
        return formatItemStack(item, amountMin, amountMax, weight, totalWeight, null);
    }
}
