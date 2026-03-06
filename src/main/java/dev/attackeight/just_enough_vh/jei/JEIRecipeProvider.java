package dev.attackeight.just_enough_vh.jei;

import dev.attackeight.just_enough_vh.JustEnoughVH;
import iskallia.vault.VaultMod;
import iskallia.vault.config.*;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.config.entry.ChanceItemStackEntry;
import iskallia.vault.config.entry.ConditionalChanceItemStackEntry;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.config.entry.recipe.ConfigForgeRecipe;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.config.recipe.ForgeRecipesConfig;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.vault.challenge.action.*;
import iskallia.vault.core.Version;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.loot.LootPool;
import iskallia.vault.core.world.loot.LootTable;
import iskallia.vault.core.world.loot.entry.ItemLootEntry;
import iskallia.vault.core.world.processor.Palette;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.processor.tile.WeightedTileProcessor;
import iskallia.vault.entity.entity.DragonTreasureGoblinEntity;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.integration.jei.lootinfo.LootInfo;
import iskallia.vault.item.gear.RecyclableItem;
import iskallia.vault.tags.ModItemTags;
import iskallia.vault.task.ProgressConfiguredTask;
import iskallia.vault.util.data.WeightedList;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import iskallia.vault.core.vault.challenge.action.PoolChallengeAction.Config.LevelPool;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        LootInfoConfig.LootInfo infoConfigLootInfo = ModConfigs.LOOT_INFO_CONFIG.getLootInfoMap().get(location);
        lootTableKeys.forEach(lootTable -> {
            List<ItemStack> itemStacks = new ArrayList<>();
            LootTable lootTable1 = VaultRegistry.LOOT_TABLE.getKey(lootTable).get(Version.latest());
            List<LootTable.Entry> entries = lootTable1.getEntries();
            int idx = 1;
            for (LootTable.Entry entry : entries) {
                itemStacks.addAll(processLootTableEntry(entry, "Roll #" + idx + " ("+(entry.getRoll().getMin() == entry.getRoll().getMax() ? "" :entry.getRoll().getMin()+"x-")+entry.getRoll().getMax()+"x)"));
                idx++;
            }
            Component label = new TextComponent("Level: " + infoConfigLootInfo.lootTableKeys.get(lootTable).getLevel() + "+");
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

    public static List<LabeledLootInfo> getShopPedestalLoot() {
        List<LabeledLootInfo> lootInfo = new ArrayList<>();
        ModConfigs.SHOP_PEDESTAL.LEVELS.forEach(tierInfo -> {
            iskallia.vault.core.util.WeightedList<ShopPedestalConfig.ShopEntry> tier = tierInfo.TRADE_POOL;
            int minLevel = tierInfo.getLevel();
            List<ItemStack> offers = new ArrayList<>();
            tier.forEach((offerInfo, weight) -> {
                ItemStack currentOffer = offerInfo.OFFER;
                if (!JustEnoughVH.SHOP_PEDESTAL_ITEMS.containsKey(currentOffer.getItem().getRegistryName()))
                    JustEnoughVH.SHOP_PEDESTAL_ITEMS.put(currentOffer.getItem().getRegistryName(), minLevel);
                offers.add(formatItemStack(currentOffer, offerInfo.getCost().getMin(),
                        offerInfo.getCost().getMax(), weight, tier.getTotalWeight(), currentOffer.getCount()));
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
            k.entries.forEach(d -> totalWeight.addAndGet(d.weight));
            k.entries.forEach(c -> results.add(formatItemStack(c.value.generateItemStack(), c.value.amountMin,
                        c.value.amountMin, c.weight, totalWeight.get())));
            lootInfo.add(LabeledLootInfo.of(results, new TextComponent("Mod: " + mod), new TextComponent("Weight: " + k.weight)));
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
        VaultRecyclerConfig config = ModConfigs.VAULT_RECYCLER;
        Map<ResourceLocation, VaultRecyclerConfig.RecyclerOutput> output = config.recyclingOutput;
        VaultRecyclerConfig.RecyclerOutput gearOutput = output.get(VaultMod.id("gear"));

        // non legendary gear
        for (VaultGearRarity rarity : VaultGearRarity.values()) {
            if (rarity.equals(VaultGearRarity.UNIQUE) || rarity.equals(VaultGearRarity.SPECIAL)) continue;
            List<ItemStack> pieceStack = generatePieceStack(rarity, false);
            List<List<ItemStack>> outputs = List.of(
                addLoreToRecyclerOutput(gearOutput.getMainOutput(), rarity, false),
                addLoreToRecyclerOutput(gearOutput.getExtraOutput1(), rarity, false),
                addLoreToRecyclerOutput(gearOutput.getExtraOutput2(), rarity, false)
            );
            toReturn.add(new RecyclerRecipe(pieceStack, outputs));
        }

        // legendary gear
        for (VaultGearRarity rarity : VaultGearRarity.values()) {
            if (rarity.equals(VaultGearRarity.UNIQUE) || rarity.equals(VaultGearRarity.SPECIAL)) continue;
            List<ItemStack> pieceStack = generatePieceStack(rarity, true);
            List<List<ItemStack>> outputsLegend = List.of(
                addLoreToRecyclerOutput(gearOutput.getMainOutput(), rarity, true),
                addLoreToRecyclerOutput(gearOutput.getExtraOutput1(), rarity, true),
                addLoreToRecyclerOutput(gearOutput.getExtraOutput2(), rarity, true)
            );
            toReturn.add(new RecyclerRecipe(pieceStack, outputsLegend));
        }

        // non gear items
        ITag<Item> vaultGearTag = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(ModItemTags.VAULT_GEAR);
        for (Map.Entry<ResourceLocation, VaultRecyclerConfig.RecyclerOutput> recEntry : output.entrySet()) {
            if (recEntry.getKey().equals(VaultMod.id("gear"))) {
                continue;
            }
            Item item = ForgeRegistries.ITEMS.getValue(recEntry.getKey());
            if (item instanceof RecyclableItem recItem && !vaultGearTag.contains(item)) {
                VaultRecyclerConfig.RecyclerOutput itemOutput = recItem.getOutput(new ItemStack(item));
                if (itemOutput == null) {
                    continue;
                }
                toReturn.add(getRecyclerRecipe(new ItemStack(item), itemOutput));
            }
        }

        return toReturn;
    }

    public static RecyclerRecipe getRecyclerRecipe(ItemStack input, VaultRecyclerConfig.RecyclerOutput output) {
        return new RecyclerRecipe(List.of(input), List.of(
                addLoreToRecyclerOutput(output.getMainOutput()),
                addLoreToRecyclerOutput(output.getExtraOutput1()),
                addLoreToRecyclerOutput(output.getExtraOutput2())));
    }

    public static List<ItemStack> addLoreToRecyclerOutput(ChanceItemStackEntry entry) {
        return addLoreToRecyclerOutput(entry, null, false);
    }

    public static List<ItemStack> addLoreToRecyclerOutput(ChanceItemStackEntry entry, @Nullable VaultGearRarity rarity, boolean legendary) {
        ItemStack stack = entry.getMatchingStack();
        float chance = entry.getChance();
        LinkedHashMap<ItemStack, Float> outputs = new LinkedHashMap<>(); // insertion order
        if (rarity != null && chance < 1f) {
            chance += ModConfigs.VAULT_RECYCLER.getAdditionalOutputRarityChance(rarity);
        }
        if (!stack.isEmpty() && chance > 0) {
            outputs.put(stack, entry.getChance());
        }

        try {
            if (entry instanceof ConditionalChanceItemStackEntry ccise) {
                for (var condOut : ccise.getConditionalOutputs().entrySet()) {
                    ConditionalChanceItemStackEntry.Condition condition = condOut.getKey();
                    ChanceItemStackEntry chanceItemStackEntry =  condOut.getValue();
                    if (condition.matches(rarity, false, legendary) || condition.matches(rarity, true, legendary)) {
                        if (!chanceItemStackEntry.getMatchingStack().isEmpty() && chanceItemStackEntry.getChance() > 0) {
                            outputs.put(chanceItemStackEntry.getMatchingStack(), chanceItemStackEntry.getChance());
                        }
                    }
                }
            }
        } catch (NullPointerException ignored) {}

        List<ItemStack> out = new ArrayList<>();
        for (Map.Entry<ItemStack, Float> output: outputs.entrySet()) {
            ItemStack outStack = output.getKey();
            CompoundTag nbt = outStack.getOrCreateTagElement("display");
            ListTag list = nbt.getList("Lore", 8);
            MutableComponent chanceLabel = new TextComponent("Chance: ");

            chanceLabel.append(String.format("%.0f", output.getValue() * 100));
            chanceLabel.append("%");
            list.add(StringTag.valueOf(Component.Serializer.toJson(chanceLabel.withStyle(ChatFormatting.YELLOW))));

            if (entry.getMinCount() != entry.getMaxCount()) {
                MutableComponent countLabel = new TextComponent("Count: ");
                countLabel.append(entry.getMinCount() + " - " + entry.getMaxCount());
                list.add(StringTag.valueOf(Component.Serializer.toJson(countLabel)));
            }
            nbt.put("Lore", list);
            out.add(outStack);
        }

        return out;
    }

    public static List<ItemStack> generatePieceStack(VaultGearRarity rarity, boolean legendary) {
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
            data.createOrReplaceAttributeValue(ModGearAttributes.IS_LEGENDARY, legendary);

            data.write(itemStack);

            toReturn.add(itemStack);
        }

        return toReturn;
    }

    protected static List<LabeledLootInfo> getChallengeActionLoot() {
        List<LabeledLootInfo> lootInfo = new ArrayList<>();
        Map<String, ChallengeAction<?>> raidValues = ModConfigs.CHALLENGE_ACTIONS.values;

        for (var rv : raidValues.entrySet()) {
            @SuppressWarnings("deprecation")
            String lootTableName = WordUtils.capitalize(rv.getKey().replace("_", " "));
            ChallengeAction.Config config = rv.getValue().getConfig();
            if (config instanceof PoolChallengeAction.Config poolConfig) {
                LevelEntryList<LevelPool> pools = poolConfig.pools;
                for (LevelPool levelPool : pools) {
                    List<ItemStack> rewardItems = getFromChallengeActionPool(levelPool.pool);
                    lootInfo.add(LabeledLootInfo.of(rewardItems,
                            new TextComponent(lootTableName + " - Level: " + levelPool.getLevel() + "+"),null)
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
        if (config instanceof ReferenceChallengeAction.Config refConfig) {
            return createChallengeActionStack(ModConfigs.CHALLENGE_ACTIONS.values.get(refConfig.path), weight, totalWeight);
        }

        if (config instanceof FloatingItemRewardChallengeAction.Config floatConfig) {
            ItemStack is = formatItemStack(floatConfig.item, floatConfig.item.getCount(), floatConfig.item.getCount(), weight, totalWeight);
            is.setHoverName(new TextComponent(floatConfig.name));
            return is;
        }

        if (config instanceof TileRewardChallengeAction.Config tileConfig) {
            Block block = Blocks.BARRIER;
            BlockState state = tileConfig.tile.getState().asWhole().orElse(null);
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
            ItemStack is = formatItemStack(block.asItem().getDefaultInstance(), tileConfig.count, tileConfig.count, weight, totalWeight);
            is.setHoverName(new TextComponent(tileConfig.name));
            return is;
        }

        if (config instanceof VanillaAttributeChallengeAction.Config vaca) {
            AttributeModifier.Operation operation = vaca.operation;
            double amount = vaca.amount.get(0).getMin(); // this is a range, but from configs it doesn't seem to be used
            String name = vaca.name;
            Attribute attribute = vaca.attribute;
            int textColor = vaca.textColor;
            Component prefix = new TextComponent(switch (operation) {
                case ADDITION -> String.format("%+.0f", amount);
                case MULTIPLY_BASE -> String.format("%+.0f%%", amount * 100.0);
                case MULTIPLY_TOTAL -> "×" + (1.0 + amount);
            });
            MutableComponent text = new TextComponent("").append(prefix);
            if (name == null) {
                Component suffix = new TranslatableComponent(attribute.getDescriptionId());
                text = text.append(" Mob ").append(suffix);
            } else {
                text = text.append(" ").append(new TextComponent(name));
            }
            MutableComponent hover = text.setStyle(Style.EMPTY.withColor(textColor));

            ItemStack is = formatItemStack(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.EMPTY), 1, 1, weight, totalWeight);
            is.setHoverName(hover);
            return is;
        }

        ItemStack is =  formatItemStack(Blocks.BARRIER.asItem().getDefaultInstance(), 1, 1, weight, totalWeight);
        is.setHoverName(new TextComponent("ERR - UNSUPPORTED"));
        return is;
    }

    protected static List<LabeledLootInfo> getChampionLoot() {
        List<LabeledLootInfo> lootInfos = new ArrayList<>();
        String lastTable = null;
        for (LegacyLootTablesConfig.Level lvlEntry : ModConfigs.LOOT_TABLES.LEVELS) {
            LootTable lootTable = VaultRegistry.LOOT_TABLE.getKey(lvlEntry.CHAMPION).get(Version.latest());
            if (lastTable != null && lastTable.equals(lootTable.getPath())) {
                continue;
            }
            lastTable = lootTable.getPath();
            lootInfos.add(LabeledLootInfo.of(processLegacyLootTable(lootTable), new TextComponent("Level "+lvlEntry.getLevel()+"+") , null));
        }

        return lootInfos;
    }

    protected static List<LabeledLootInfo> getDragonGoblinDrops() {
        List<LabeledLootInfo> lootInfos = new ArrayList<>();
        for (var goblinInfo: getGoblinInfoFromPalette("the_vault:dragon/base").entrySet()) {
            TextComponent typeCompoment = new TextComponent(goblinInfo.getKey());
            try {
                typeCompoment.withStyle(Style.EMPTY.withColor(DragonTreasureGoblinEntity.GoblinType.valueOf(goblinInfo.getKey()).getColour()));
            } catch (IllegalArgumentException ignored) {/* config has invalid enum constant - no color */}

            Component label = new TextComponent("Goblin Type: ").append(typeCompoment);
            LootTable lootTable = VaultRegistry.LOOT_TABLE.getKey(goblinInfo.getValue()).get(Version.latest());
            lootInfos.add(LabeledLootInfo.of(processLegacyLootTable(lootTable), label, null));
        }


        return lootInfos;
    }

    /** returns GoblinType => goblin drop loot table key mapping */
    private static Map<String, String> getGoblinInfoFromPalette(String paletteId) {
        PaletteKey key = VaultRegistry.PALETTE.getKey(paletteId);
        if (key == null) return Collections.emptyMap();
        Palette palette = key.get(Version.latest());
        List<TileProcessor> processors = palette.getTileProcessors();
        LinkedHashMap<String, String> goblins = new LinkedHashMap<>();
        for (TileProcessor tp : processors) {
            if (tp instanceof WeightedTileProcessor wtp) {
                // palette processor responsible for goblin types
                for (PartialTile outputTile : wtp.getOutput().keySet()) {
                    PartialCompoundNbt partialOutputNbt = outputTile.getEntity();
                    if (partialOutputNbt == null){
                        continue;
                    }
                    CompoundTag outputNbt = partialOutputNbt.asWhole().orElse(null);
                    if (outputNbt == null) {
                        continue;
                    }
                    ListTag variantList = outputNbt.getList("goblinVariants", Tag.TAG_COMPOUND);
                    for (int i = 0; i < variantList.size(); i++) {
                        CompoundTag variant = variantList.getCompound(i).getCompound("variant");
                        String goblinType = variant.getCompound("entityNbt").getString("GoblinType");
                        String lootTable = variant.getString("dropLootTable");
                        goblins.put(goblinType, lootTable);
                    }
                }
            }
        }
        return goblins;
    }

    protected static List<LabeledLootInfo> getRuneRewards() {
        List<LabeledLootInfo> lootInfos = new ArrayList<>();
        String lastTable = null;
        for (LegacyLootTablesConfig.Level lvlEntry : ModConfigs.LOOT_TABLES.LEVELS) {
            LootTable lootTable = VaultRegistry.LOOT_TABLE.getKey(lvlEntry.RUNES_LOOT).get(Version.latest());
            if (lastTable != null && lastTable.equals(lootTable.getPath())) {
                continue;
            }
            lastTable = lootTable.getPath();
            lootInfos.add(LabeledLootInfo.of(processLegacyLootTable(lootTable), new TextComponent("Level "+lvlEntry.getLevel()+"+") , null));
        }

        return lootInfos;
    }

    protected static List<LabeledLootInfo> getRoyaleLoot() {
        List<LabeledLootInfo> lootInfos = new ArrayList<>();
        ModConfigs.ROYALE_LOOT.presets.forEach((lootTableId, weight) -> {
            LootTable lootTable = VaultRegistry.LOOT_TABLE.getKey(lootTableId).get(Version.latest());
            @SuppressWarnings("deprecation")
            String name = WordUtils.capitalize(lootTableId.getPath().replace("_", " "));
            lootInfos.add(LabeledLootInfo.of(processLegacyLootTable(lootTable), new TextComponent(name), null));
        });

        return lootInfos;
    }


    public static List<ForgeItem> getDeckRecipes() {
        List<ForgeItem> recipes = new ArrayList<>();
        ModConfigs.DECK_CRAFTING_RECIPES.getConfigRecipes().forEach(b -> {
            ItemStack output = b.makeRecipe().getDisplayOutput(100);

            if (b.getDiscoveryTask() != null) {
                CompoundTag nbt = output.getOrCreateTagElement("display");
                ListTag list = nbt.getList("Lore", 8);
                MutableComponent chanceLabel = new TextComponent("Unlock Task: ");

                chanceLabel.append(b.getDiscoveryTask().getRenderer().writeJson().get().getAsJsonObject("title").get("text").getAsString());

                if (b.getDiscoveryTask() instanceof ProgressConfiguredTask<?,?> pgt) {
                    chanceLabel.append(" (" + pgt.getCounter().writeJson().get().getAsJsonObject("target").get("count").getAsInt() + ")");
                }

                list.add(StringTag.valueOf(Component.Serializer.toJson(chanceLabel.withStyle(ChatFormatting.YELLOW))));

                nbt.put("Lore", list);
            }

            recipes.add(new ForgeItem(b.makeRecipe().getInputs(), output));

        });
        return recipes;
    }

    private static List<ItemStack> processLootTableEntry(LootTable.Entry entry, @Nullable String rollText) {
        return processLootPool(entry.getPool(), rollText,1d);
    }

    private static List<ItemStack> processLootPool(LootPool pool, String rollText, Double weightMultiplier) {
        List<ItemStack> stacks = new ArrayList<>();
        iskallia.vault.core.util.WeightedList<Object> children = pool.getChildren();
        for (Map.Entry<Object, Double> entry : children.entrySet()) {
            Object k = entry.getKey();
            Double weight = entry.getValue();
            if (k instanceof LootPool lootpool) {
                List<ItemStack> nestedStacks = processLootPool(lootpool, rollText, weight / children.getTotalWeight());
                stacks.addAll(nestedStacks);
            }
            if (k instanceof ItemLootEntry lootEntry) {
                ItemStack is = new ItemStack(lootEntry.getItem());
                CompoundTag nbt = lootEntry.getNbt();
                if (nbt != null) {
                    is.setTag(nbt.copy());
                }
                is = formatItemStack(is, lootEntry.getCount().getMin(), lootEntry.getCount().getMax(), weightMultiplier * weight, children.getTotalWeight(), null, rollText);
                stacks.add(is);
            }
        }
        return stacks;
    }

    private static List<ItemStack> processLegacyLootTable(LootTable table) {
        List<ItemStack> itemStacks = new ArrayList<>();
        int idx = 1;
        for (LootTable.Entry entry: table.getEntries()) {
            itemStacks.addAll(processLootTableEntry(entry, "Roll #" + idx + " ("+(entry.getRoll().getMin() == entry.getRoll().getMax() ? "" :entry.getRoll().getMin()+"x-")+entry.getRoll().getMax()+"x)"));
            idx++;
        }
        return itemStacks;
    }


    protected static ItemStack formatItemStack(ItemStack item, int amountMin, int amountMax, double weight, double totalWeight, @Nullable Integer amount) {
        return formatItemStack(item, amountMin, amountMax, weight, totalWeight, amount, null);
    }

    private static ItemStack formatItemStack(ItemStack item, int amountMin, int amountMax, double weight, double totalWeight, @Nullable Integer amount, @Nullable String rollText) {
        ItemStack result = item.copy();
        if (item.isEmpty()) {
            result = new ItemStack(Items.BARRIER);
            result.setHoverName(new TextComponent("Nothing"));
        }
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
        if (rollText != null) {
            MutableComponent rollLabel = new TextComponent(rollText);
            list.add(StringTag.valueOf(Component.Serializer.toJson(rollLabel.withStyle(ChatFormatting.DARK_AQUA))));
        }
        nbt.put("Lore", list);
        return result;
    }

    private static ItemStack formatItemStack(ItemStack item, int amountMin, int amountMax, double weight, double totalWeight) {
        return formatItemStack(item, amountMin, amountMax, weight, totalWeight, null);
    }
}
