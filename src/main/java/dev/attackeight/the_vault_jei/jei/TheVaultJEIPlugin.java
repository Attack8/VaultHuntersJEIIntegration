package dev.attackeight.the_vault_jei.jei;

import dev.attackeight.the_vault_jei.TheVaultJEI;
import dev.attackeight.the_vault_jei.jei.category.*;
import static dev.attackeight.the_vault_jei.jei.JEIRecipeProvider.*;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.jei.lootinfo.LootInfo;
import iskallia.vault.integration.jei.lootinfo.LootInfoRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

import javax.annotation.ParametersAreNonnullByDefault;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class TheVaultJEIPlugin implements IModPlugin {

    public static final RecipeType<ForgeItem> CATALYSTS = RecipeType.create("the_vault", "forge_catalysts", ForgeItem.class);
    public static final RecipeType<ForgeItem> TOOLS = RecipeType.create("the_vault", "forge_tools", ForgeItem.class);
    public static final RecipeType<ForgeItem> GEAR = RecipeType.create("the_vault", "forge_gear", ForgeItem.class);
    public static final RecipeType<ForgeItem> INSCRIPTIONS = RecipeType.create("the_vault", "forge_inscription", ForgeItem.class);
    public static final RecipeType<LootInfo> MYSTERY_BOX = RecipeType.create("the_vault", "mystery_box", LootInfo.class);
    public static final RecipeType<LootInfo> MYSTERY_EGG = RecipeType.create("the_vault", "mystery_egg", LootInfo.class);
    public static final RecipeType<LootInfo> HOSTILE_EGG = RecipeType.create("the_vault", "hostile_egg", LootInfo.class);
    public static final RecipeType<LabeledLootInfo> BLACK_MARKET = RecipeType.create("the_vault", "black_market", LabeledLootInfo.class);
    public static final RecipeType<LabeledLootInfo> MOD_BOX = RecipeType.create("the_vault", "mod_box", LabeledLootInfo.class);
    public static final RecipeType<LabeledLootInfo> BOUNTY_REWARDS = RecipeType.create("the_vault", "bounty_rewards", LabeledLootInfo.class);
    public static final RecipeType<LabeledIngredientPool> ALTAR_INGREDIENTS = RecipeType.create("the_vault", "altar_ingredients", LabeledIngredientPool.class);

    public TheVaultJEIPlugin() {}

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CATALYST_INFUSION_TABLE), CATALYSTS);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.TOOL_STATION), TOOLS);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.VAULT_FORGE), GEAR);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.INSCRIPTION_TABLE), INSCRIPTIONS);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MYSTERY_BOX), MYSTERY_BOX);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MYSTERY_EGG), MYSTERY_EGG);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MYSTERY_HOSTILE_EGG), HOSTILE_EGG);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BLACK_MARKET), BLACK_MARKET);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MOD_BOX), MOD_BOX);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BOUNTY_BLOCK), BOUNTY_REWARDS);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.VAULT_ALTAR), ALTAR_INGREDIENTS);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(makeForgeItemCategory(guiHelper, CATALYSTS, ModBlocks.CATALYST_INFUSION_TABLE));
        registration.addRecipeCategories(makeForgeItemCategory(guiHelper, TOOLS, ModBlocks.TOOL_STATION));
        registration.addRecipeCategories(makeForgeItemCategory(guiHelper, GEAR, ModBlocks.VAULT_FORGE));
        registration.addRecipeCategories(makeForgeItemCategory(guiHelper, INSCRIPTIONS, ModBlocks.INSCRIPTION_TABLE));
        registration.addRecipeCategories(makeLootInfoCategory(guiHelper, MYSTERY_BOX, ModItems.MYSTERY_BOX));
        registration.addRecipeCategories(makeLootInfoCategory(guiHelper, MYSTERY_EGG, ModItems.MYSTERY_EGG));
        registration.addRecipeCategories(makeLootInfoCategory(guiHelper, HOSTILE_EGG, ModItems.MYSTERY_HOSTILE_EGG));
        registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, BLACK_MARKET, ModBlocks.BLACK_MARKET));
        registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, MOD_BOX, ModItems.MOD_BOX));
        registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, BOUNTY_REWARDS, ModBlocks.BOUNTY_BLOCK));
        registration.addRecipeCategories(makeLabeledIngredientPoolCategory(guiHelper, ALTAR_INGREDIENTS, ModBlocks.VAULT_ALTAR));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CATALYSTS, getCatalystRecipes());
        registration.addRecipes(TOOLS, getToolRecipes());
        registration.addRecipes(GEAR, getGearRecipes());
        registration.addRecipes(INSCRIPTIONS, getInscriptionRecipes());
        registration.addRecipes(MYSTERY_BOX, getMysteryBoxLoot());
        registration.addRecipes(MYSTERY_EGG, getMysteryEggLoot());
        registration.addRecipes(HOSTILE_EGG, getHostileEggLoot());
        registration.addRecipes(BLACK_MARKET, getBlackMarketLoot());
        registration.addRecipes(MOD_BOX, getModBoxLoot());
        registration.addRecipes(BOUNTY_REWARDS, getBountyRewards());
        registration.addRecipes(ALTAR_INGREDIENTS, getAltarIngredients());
        if (TheVaultJEI.hasWolds()) {

        }
    }

    @Override
    public ResourceLocation getPluginUid() {
        return TheVaultJEI.rl("jei_integration");
    }

    private LootInfoRecipeCategory makeLootInfoCategory(IGuiHelper guiHelper, RecipeType<LootInfo> recipeType, ItemLike icon) {
        return new LootInfoRecipeCategory(guiHelper, recipeType, new ItemStack(icon), icon.asItem().getName(new ItemStack(icon)));
    }

    private LabeledLootInfoRecipeCategory makeLabeledLootInfoCategory(IGuiHelper guiHelper, RecipeType<LabeledLootInfo> recipeType, ItemLike icon) {
        return new LabeledLootInfoRecipeCategory(guiHelper, recipeType, new ItemStack(icon), icon.asItem().getName(new ItemStack(icon)));
    }

    private LabeledIngredientPoolRecipeCategory makeLabeledIngredientPoolCategory(IGuiHelper guiHelper, RecipeType<LabeledIngredientPool> recipeType, ItemLike icon) {
        return new LabeledIngredientPoolRecipeCategory(guiHelper, recipeType, new ItemStack(icon), icon.asItem().getName(new ItemStack(icon)));
    }

    private ForgeItemRecipeCategory makeForgeItemCategory(IGuiHelper guiHelper, RecipeType<ForgeItem> recipeType, ItemLike icon) {
        return new ForgeItemRecipeCategory(guiHelper, recipeType, new ItemStack(icon), icon.asItem().getName(new ItemStack(icon)));
    }
}