package dev.attackeight.just_enough_vh.jei;

import dev.attackeight.just_enough_vh.JustEnoughVH;
import dev.attackeight.just_enough_vh.jei.category.*;
import static dev.attackeight.just_enough_vh.jei.JEIRecipeProvider.*;

import dev.attackeight.just_enough_vh.JustEnoughVH.ModConfig;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.jei.lootinfo.LootInfo;
import iskallia.vault.integration.jei.lootinfo.LootInfoRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import static mezz.jei.api.recipe.RecipeIngredientRole.*;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class TheVaultJEIPlugin implements IModPlugin {

    public static final RecipeType<ForgeItem> CATALYSTS = RecipeType.create(JustEnoughVH.ID, "forge_catalysts", ForgeItem.class);
    public static final RecipeType<ForgeItem> TOOLS = RecipeType.create(JustEnoughVH.ID, "forge_tools", ForgeItem.class);
    public static final RecipeType<ForgeItem> GEAR = RecipeType.create(JustEnoughVH.ID, "forge_gear", ForgeItem.class);
    public static final RecipeType<ForgeItem> TRINKETS = RecipeType.create(JustEnoughVH.ID, "trinket", ForgeItem.class);
    public static final RecipeType<ForgeItem> INSCRIPTIONS = RecipeType.create(JustEnoughVH.ID, "forge_inscription", ForgeItem.class);
    public static final RecipeType<ForgeItem> JEWEL_CRAFTING = RecipeType.create(JustEnoughVH.ID, "jewel_crafting", ForgeItem.class);
    public static final RecipeType<LootInfo> MYSTERY_BOX = RecipeType.create(JustEnoughVH.ID, "mystery_box", LootInfo.class);
    public static final RecipeType<LootInfo> MYSTERY_EGG = RecipeType.create(JustEnoughVH.ID, "mystery_egg", LootInfo.class);
    public static final RecipeType<LootInfo> HOSTILE_EGG = RecipeType.create(JustEnoughVH.ID, "hostile_egg", LootInfo.class);
    public static final RecipeType<LootInfo> PANDORAS_BOX = RecipeType.create(JustEnoughVH.ID, "pandoras_box", LootInfo.class);
    public static final RecipeType<LabeledLootInfo> BLACK_MARKET = RecipeType.create(JustEnoughVH.ID, "black_market", LabeledLootInfo.class);
    public static final RecipeType<LabeledLootInfo> MOD_BOX = RecipeType.create(JustEnoughVH.ID, "mod_box", LabeledLootInfo.class);
    public static final RecipeType<LabeledLootInfo> BOUNTY_REWARDS = RecipeType.create(JustEnoughVH.ID, "bounty_rewards", LabeledLootInfo.class);
    public static final RecipeType<LabeledLootInfo> SHOP_PEDESTAL = RecipeType.create(JustEnoughVH.ID, "shop_pedestal", LabeledLootInfo.class);
    public static final RecipeType<LabeledLootInfo> VA_ARENA_STATUE = RecipeType.create("vaultadditions", "arena_statue", LabeledLootInfo.class);
    public static final RecipeType<LabeledLootInfo> VA_GIFT_STATUE = RecipeType.create("vaultadditions", "gift_statue", LabeledLootInfo.class);
    public static final RecipeType<LabeledLootInfo> VA_MEGA_GIFT_STATUE = RecipeType.create("vaultadditions", "mega_gift_statue", LabeledLootInfo.class);
    public static final RecipeType<LabeledLootInfo> VA_VAULT_STATUE = RecipeType.create("vaultadditions", "vault_statue", LabeledLootInfo.class);
    public static final RecipeType<LabeledLootInfo> ALTAR_INGREDIENTS = RecipeType.create(JustEnoughVH.ID, "altar_ingredients", LabeledLootInfo.class);
    public static final RecipeType<LabeledLootInfo> MATERIAL_BOX = RecipeType.create(JustEnoughVH.ID, "material_box", LabeledLootInfo.class);

    public TheVaultJEIPlugin() {}

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CATALYST_INFUSION_TABLE), CATALYSTS);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.TOOL_STATION), TOOLS);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.VAULT_FORGE), GEAR);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.TRINKET_FORGE), TRINKETS);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.INSCRIPTION_TABLE), INSCRIPTIONS);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.JEWEL_CRAFTING_TABLE), JEWEL_CRAFTING);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MYSTERY_BOX), MYSTERY_BOX);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MYSTERY_EGG), MYSTERY_EGG);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MYSTERY_HOSTILE_EGG), HOSTILE_EGG);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BLACK_MARKET), BLACK_MARKET);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MOD_BOX), MOD_BOX);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BOUNTY_BLOCK), BOUNTY_REWARDS);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SHOP_PEDESTAL), SHOP_PEDESTAL);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.VAULT_ALTAR), ALTAR_INGREDIENTS);

        if (ModConfig.shouldShow()) {
            registration.addRecipeCatalyst(new ItemStack(ModItems.PANDORAS_BOX), PANDORAS_BOX);
            registration.addRecipeCatalyst(new ItemStack(ModItems.MATERIAL_BOX), MATERIAL_BOX);
        }

        if (JustEnoughVH.vaLoaded()) {
            registration.addRecipeCatalyst(new ItemStack(io.github.a1qs.vaultadditions.init.ModBlocks.LOOT_STATUE_ARENA.get()), VA_ARENA_STATUE);
            registration.addRecipeCatalyst(new ItemStack(io.github.a1qs.vaultadditions.init.ModBlocks.LOOT_STATUE_GIFT.get()), VA_GIFT_STATUE);
            registration.addRecipeCatalyst(new ItemStack(io.github.a1qs.vaultadditions.init.ModBlocks.LOOT_STATUE_GIFT_MEGA.get()), VA_MEGA_GIFT_STATUE);
            registration.addRecipeCatalyst(new ItemStack(io.github.a1qs.vaultadditions.init.ModBlocks.LOOT_STATUE_VAULT.get()), VA_VAULT_STATUE);
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(makeForgeItemCategory(guiHelper, CATALYSTS, ModBlocks.CATALYST_INFUSION_TABLE));
        registration.addRecipeCategories(makeForgeItemCategory(guiHelper, TOOLS, ModBlocks.TOOL_STATION));
        registration.addRecipeCategories(makeForgeItemCategory(guiHelper, GEAR, ModBlocks.VAULT_FORGE));
        registration.addRecipeCategories(makeForgeItemCategory(guiHelper, TRINKETS, ModBlocks.TRINKET_FORGE));
        registration.addRecipeCategories(makeForgeItemCategory(guiHelper, INSCRIPTIONS, ModBlocks.INSCRIPTION_TABLE));
        registration.addRecipeCategories(makeForgeItemCategory(guiHelper, JEWEL_CRAFTING, ModBlocks.JEWEL_CRAFTING_TABLE));
        registration.addRecipeCategories(makeLootInfoCategory(guiHelper, MYSTERY_BOX, ModItems.MYSTERY_BOX));
        registration.addRecipeCategories(makeLootInfoCategory(guiHelper, MYSTERY_EGG, ModItems.MYSTERY_EGG));
        registration.addRecipeCategories(makeLootInfoCategory(guiHelper, HOSTILE_EGG, ModItems.MYSTERY_HOSTILE_EGG));
        registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, BLACK_MARKET, ModBlocks.BLACK_MARKET));
        registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, MOD_BOX, ModItems.MOD_BOX));
        registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, BOUNTY_REWARDS, ModBlocks.BOUNTY_BLOCK));
        registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, SHOP_PEDESTAL, ModBlocks.SHOP_PEDESTAL));
        registration.addRecipeCategories(makeLabeledIngredientPoolCategory(guiHelper, ALTAR_INGREDIENTS, ModBlocks.VAULT_ALTAR));

        if (ModConfig.shouldShow()) {
            registration.addRecipeCategories(makeLootInfoCategory(guiHelper, PANDORAS_BOX, ModItems.PANDORAS_BOX));
            registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, MATERIAL_BOX, ModItems.MATERIAL_BOX));
        }
        if (JustEnoughVH.vaLoaded()) {
            VaultAdditionsJEIPlugin.registerStatueCategories(registration, guiHelper);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CATALYSTS, getForgeRecipes(ModConfigs.CATALYST_RECIPES));
        registration.addRecipes(TOOLS, getForgeRecipes(ModConfigs.TOOL_RECIPES));
        registration.addRecipes(GEAR, getForgeRecipes(ModConfigs.GEAR_RECIPES));
        registration.addRecipes(TRINKETS, getForgeRecipes(ModConfigs.TRINKET_RECIPES));
        registration.addRecipes(INSCRIPTIONS, getForgeRecipes(ModConfigs.INSCRIPTION_RECIPES));
        registration.addRecipes(JEWEL_CRAFTING, getForgeRecipes(ModConfigs.JEWEL_CRAFTING_RECIPES));
        registration.addRecipes(MYSTERY_BOX, getFromPool(ModConfigs.MYSTERY_BOX.POOL));
        registration.addRecipes(MYSTERY_EGG, getFromPool(ModConfigs.MYSTERY_EGG.POOL));
        registration.addRecipes(HOSTILE_EGG, getFromPool(ModConfigs.MYSTERY_HOSTILE_EGG.POOL));
        registration.addRecipes(BLACK_MARKET, getBlackMarketLoot());
        registration.addRecipes(MOD_BOX, getModBoxLoot());
        registration.addRecipes(BOUNTY_REWARDS, getBountyRewards());
        registration.addRecipes(SHOP_PEDESTAL, getShopPedestalLoot());
        registration.addRecipes(ALTAR_INGREDIENTS, getAltarIngredients());

        if (ModConfig.shouldShow()) {
            registration.addRecipes(PANDORAS_BOX, getFromPool(ModConfigs.PANDORAS_BOX.POOL));
            registration.addRecipes(MATERIAL_BOX, getMaterialBoxLoot());
        }
        if (JustEnoughVH.vaLoaded()) {
            // Register Vault Additions recipes in a separate class to avoid ClassNotFoundException
            VaultAdditionsJEIPlugin.registerStatueRecipes(registration);
        }
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return JustEnoughVH.rl("jei_integration");
    }

    public static LootInfoRecipeCategory makeLootInfoCategory(IGuiHelper guiHelper, RecipeType<LootInfo> recipeType, ItemLike icon) {
        return new LootInfoRecipeCategory(guiHelper, recipeType, new ItemStack(icon), icon.asItem().getName(new ItemStack(icon)));
    }

    public static LabeledLootInfoRecipeCategory makeLabeledLootInfoCategory(IGuiHelper guiHelper, RecipeType<LabeledLootInfo> recipeType, ItemLike icon) {
        return new LabeledLootInfoRecipeCategory(guiHelper, recipeType, new ItemStack(icon), OUTPUT);
    }

    public static LabeledLootInfoRecipeCategory makeLabeledIngredientPoolCategory(IGuiHelper guiHelper, RecipeType<LabeledLootInfo> recipeType, ItemLike icon) {
        return new LabeledLootInfoRecipeCategory(guiHelper, recipeType, new ItemStack(icon), INPUT);
    }

    public static ForgeItemRecipeCategory makeForgeItemCategory(IGuiHelper guiHelper, RecipeType<ForgeItem> recipeType, ItemLike icon) {
        return new ForgeItemRecipeCategory(guiHelper, recipeType, new ItemStack(icon));
    }
}