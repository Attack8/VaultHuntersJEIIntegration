package dev.attackeight.the_vault_jei.jei;

import dev.attackeight.the_vault_jei.TheVaultJEI;
import dev.attackeight.the_vault_jei.jei.category.*;
import dev.attackeight.the_vault_jei.mixin.ProductEntryAccessor;
import iskallia.vault.config.OmegaSoulShardConfig;
import iskallia.vault.config.SoulShardConfig;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.gear.crafting.recipe.*;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.jei.lootinfo.LootInfo;
import iskallia.vault.integration.jei.lootinfo.LootInfoRecipeCategory;
import iskallia.vault.util.data.WeightedList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class TheVaultJEIPlugin implements IModPlugin {

    public static final RecipeType<LootInfo> MYSTERY_BOX = RecipeType.create("the_vault", "mystery_box", LootInfo.class);
    public static final RecipeType<LootInfo> MYSTERY_EGG = RecipeType.create("the_vault", "mystery_egg", LootInfo.class);
    public static final RecipeType<LootInfo> HOSTILE_EGG = RecipeType.create("the_vault", "hostile_egg", LootInfo.class);
    public static final RecipeType<LabeledLootInfo> BLACK_MARKET = RecipeType.create("the_vault", "black_market", LabeledLootInfo.class);
    public static final RecipeType<LabeledLootInfo> MOD_BOX = RecipeType.create("the_vault", "mod_box", LabeledLootInfo.class);

    public TheVaultJEIPlugin() {}

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CATALYST_INFUSION_TABLE), ForgeCatalystRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.TOOL_STATION), ForgeToolRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.TOOL_STATION), ForgeJewelRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.VAULT_FORGE), ForgeGearRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.VAULT_FORGE), ForgeTrinketRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.INSCRIPTION_TABLE), ForgeInscriptionRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MYSTERY_BOX), MYSTERY_BOX);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MYSTERY_EGG), MYSTERY_EGG);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MYSTERY_HOSTILE_EGG), HOSTILE_EGG);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BLACK_MARKET), BLACK_MARKET);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MOD_BOX), MOD_BOX);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new ForgeCatalystRecipeCategory(guiHelper));
        registration.addRecipeCategories(new ForgeToolRecipeCategory(guiHelper));
        registration.addRecipeCategories(new ForgeJewelRecipeCategory(guiHelper));
        registration.addRecipeCategories(new ForgeGearRecipeCategory(guiHelper));
        registration.addRecipeCategories(new ForgeTrinketRecipeCategory(guiHelper));
        registration.addRecipeCategories(new ForgeInscriptionRecipeCategory(guiHelper));
        registration.addRecipeCategories(makeLootInfoCategory(guiHelper, MYSTERY_BOX, ModItems.MYSTERY_BOX));
        registration.addRecipeCategories(makeLootInfoCategory(guiHelper, MYSTERY_EGG, ModItems.MYSTERY_EGG));
        registration.addRecipeCategories(makeLootInfoCategory(guiHelper, HOSTILE_EGG, ModItems.MYSTERY_HOSTILE_EGG));
        registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, BLACK_MARKET, ModBlocks.BLACK_MARKET));
        registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, MOD_BOX, ModItems.MOD_BOX));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(ForgeCatalystRecipeCategory.RECIPE_TYPE, getCatalystRecipes());
        registration.addRecipes(ForgeToolRecipeCategory.RECIPE_TYPE, getToolRecipes());
        registration.addRecipes(ForgeJewelRecipeCategory.RECIPE_TYPE, getJewelRecipes());
        registration.addRecipes(ForgeGearRecipeCategory.RECIPE_TYPE, getGearRecipes());
        registration.addRecipes(ForgeTrinketRecipeCategory.RECIPE_TYPE, getTrinketRecipes());
        registration.addRecipes(ForgeInscriptionRecipeCategory.RECIPE_TYPE, getInscriptionRecipes());
        registration.addRecipes(MYSTERY_BOX, getMysteryBoxLoot());
        registration.addRecipes(MYSTERY_EGG, getMysteryEggLoot());
        registration.addRecipes(HOSTILE_EGG, getHostileEggLoot());
        registration.addRecipes(BLACK_MARKET, getBlackMarketLoot());
        registration.addRecipes(MOD_BOX, getModBoxLoot());
    }

    @Override
    public ResourceLocation getPluginUid() {
        return TheVaultJEI.rl("jei_integration");
    }

    private List<CatalystForgeRecipe> getCatalystRecipes() {
        List<CatalystForgeRecipe> recipes = new ArrayList<>();
        ModConfigs.CATALYST_RECIPES.getConfigRecipes().forEach(b -> recipes.add(b.makeRecipe()));
        return recipes;
    }

    private List<ToolForgeRecipe> getToolRecipes() {
        List<ToolForgeRecipe> recipes = new ArrayList<>();
        ModConfigs.TOOL_RECIPES.getConfigRecipes().forEach(b -> recipes.add(b.makeRecipe()));
        return recipes;
    }

    private List<JewelForgeRecipe> getJewelRecipes(){
        List<JewelForgeRecipe> recipes = new ArrayList<>();
        ModConfigs.JEWEL_RECIPES.getConfigRecipes().forEach(b -> recipes.add(b.makeRecipe()));
        return recipes;
    }

    private List<TrinketForgeRecipe> getTrinketRecipes(){
        List<TrinketForgeRecipe> recipes = new ArrayList<>();
        ModConfigs.TRINKET_RECIPES.getConfigRecipes().forEach(b -> recipes.add(b.makeRecipe()));
        return recipes;
    }

    private List<GearForgeRecipe> getGearRecipes(){
        List<GearForgeRecipe> recipes = new ArrayList<>();
        ModConfigs.GEAR_RECIPES.getConfigRecipes().forEach(b -> recipes.add(b.makeRecipe()));
        return recipes;
    }

    private List<InscriptionForgeRecipe> getInscriptionRecipes() {
        List<InscriptionForgeRecipe> recipes = new ArrayList<>();
        ModConfigs.INSCRIPTION_RECIPES.getConfigRecipes().forEach(b -> recipes.add(b.makeRecipe()));
        return recipes;
    }

    private List<LootInfo> getMysteryBoxLoot() {
        List<LootInfo> lootInfo = new ArrayList<>();
        List<ItemStack> loot = new ArrayList<>();
        ModConfigs.MYSTERY_BOX.POOL.forEach(b -> loot.add(b.value.generateItemStack()));
        lootInfo.add(new LootInfo(loot));
        return lootInfo;
    }

    private List<LootInfo> getMysteryEggLoot() {
        List<LootInfo> lootInfo = new ArrayList<>();
        List<ItemStack> loot = new ArrayList<>();
        int total = ModConfigs.MYSTERY_EGG.POOL.getTotalWeight();
        ModConfigs.MYSTERY_EGG.POOL.forEach(b -> loot.add(addWeight(b, total)));
        lootInfo.add(new LootInfo(loot));
        return lootInfo;
    }

    private List<LootInfo> getHostileEggLoot() {
        List<LootInfo> lootInfo = new ArrayList<>();
        List<ItemStack> loot = new ArrayList<>();
        int total = ModConfigs.MYSTERY_HOSTILE_EGG.POOL.getTotalWeight();
        ModConfigs.MYSTERY_HOSTILE_EGG.POOL.forEach(b -> loot.add(addWeight(b, total)));
        lootInfo.add(new LootInfo(loot));
        return lootInfo;
    }

    private ItemStack addWeight(WeightedList.Entry<ProductEntry> productEntry, int totalWeight) {
        ItemStack stack = productEntry.value.generateItemStack();
        CompoundTag nbt = stack.getOrCreateTagElement("display");
        ListTag list = nbt.getList("Lore", 8);
        MutableComponent component = new TextComponent("Chance: ");
        double chance = ((double) productEntry.weight / totalWeight) * 100;
        component.append(String.format("%.2f", chance));
        component.append("%");
        list.add(StringTag.valueOf(Component.Serializer.toJson(component.withStyle(ChatFormatting.YELLOW))));
        nbt.put("Lore", list);
        return stack;
    }

    private List<LabeledLootInfo> getBlackMarketLoot() {
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

    private List<LabeledLootInfo> getModBoxLoot() {
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

    private LootInfoRecipeCategory makeLootInfoCategory(IGuiHelper guiHelper, RecipeType<LootInfo> recipeType, ItemLike icon) {
        return new LootInfoRecipeCategory(guiHelper, recipeType, new ItemStack(icon), icon.asItem().getName(new ItemStack(icon)));
    }

    private LabeledLootInfoRecipeCategory makeLabeledLootInfoCategory(IGuiHelper guiHelper, RecipeType<LabeledLootInfo> recipeType, ItemLike icon) {
        return new LabeledLootInfoRecipeCategory(guiHelper, recipeType, new ItemStack(icon), icon.asItem().getName(new ItemStack(icon)));
    }
}