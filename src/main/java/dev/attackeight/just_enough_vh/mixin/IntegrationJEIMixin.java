package dev.attackeight.just_enough_vh.mixin;

import iskallia.vault.integration.jei.IntegrationJEI;
import iskallia.vault.integration.jei.lootbox.WeightedListJEICategory;
import iskallia.vault.integration.jei.materialbox.MaterialBoxJEICategory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = IntegrationJEI.class, remap = false)
public class IntegrationJEIMixin {

    @Unique private static List<RecipeType<?>> justEnoughVH$removedRecipeTypes = new ArrayList<>();

    @Redirect(method = "registerCategories", at = @At(value = "INVOKE", target = "Lmezz/jei/api/registration/IRecipeCategoryRegistration;addRecipeCategories([Lmezz/jei/api/recipe/category/IRecipeCategory;)V"))
    private void removeCats(IRecipeCategoryRegistration instance, IRecipeCategory<?>[] iRecipeCategories) {
        if (iRecipeCategories[0] instanceof WeightedListJEICategory || iRecipeCategories[0] instanceof MaterialBoxJEICategory) {
            justEnoughVH$removedRecipeTypes.add(iRecipeCategories[0].getRecipeType());
            return;
        }
        instance.addRecipeCategories(iRecipeCategories);
    }

    @Redirect(method = "registerRecipes", at = @At(value = "INVOKE", target = "Lmezz/jei/api/registration/IRecipeRegistration;addRecipes(Lmezz/jei/api/recipe/RecipeType;Ljava/util/List;)V"))
    private <T> void removeRecipes(IRecipeRegistration registration, RecipeType<T> recipeType, List<T> recipes) {
        if (justEnoughVH$removedRecipeTypes.contains(recipeType))
            return;
        registration.addRecipes(recipeType, recipes);
    }
}
