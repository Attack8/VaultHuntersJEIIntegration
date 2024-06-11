package dev.attackeight.the_vault_jei.jei;

import dev.attackeight.the_vault_jei.TheVaultJEI;
import dev.attackeight.the_vault_jei.jei.category.*;
import iskallia.vault.gear.crafting.recipe.*;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class TheVaultJEIPlugin implements IModPlugin {

    public TheVaultJEIPlugin() {}

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CATALYST_INFUSION_TABLE), new RecipeType[]{ForgeCatalystRecipeCategory.RECIPE_TYPE});
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.TOOL_STATION), new RecipeType[]{ForgeToolRecipeCategory.RECIPE_TYPE});
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.TOOL_STATION), new RecipeType[]{ForgeJewelRecipeCategory.RECIPE_TYPE});
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.VAULT_FORGE), new RecipeType[]{ForgeGearRecipeCategory.RECIPE_TYPE});
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.INSCRIPTION_TABLE), new RecipeType[]{ForgeInscriptionRecipeCategory.RECIPE_TYPE});
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new IRecipeCategory[]{new ForgeCatalystRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
        registration.addRecipeCategories(new IRecipeCategory[]{new ForgeToolRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
        registration.addRecipeCategories(new IRecipeCategory[]{new ForgeJewelRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
        registration.addRecipeCategories(new IRecipeCategory[]{new ForgeGearRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
        registration.addRecipeCategories(new IRecipeCategory[]{new ForgeInscriptionRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(ForgeCatalystRecipeCategory.RECIPE_TYPE, getCatalystRecipes());
        registration.addRecipes(ForgeToolRecipeCategory.RECIPE_TYPE, getToolRecipes());
        registration.addRecipes(ForgeJewelRecipeCategory.RECIPE_TYPE, getJewelRecipes());
        registration.addRecipes(ForgeGearRecipeCategory.RECIPE_TYPE, getGearRecipes());
        registration.addRecipes(ForgeInscriptionRecipeCategory.RECIPE_TYPE, getInscriptionRecipes());
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
}