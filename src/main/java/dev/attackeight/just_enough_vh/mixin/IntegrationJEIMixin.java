package dev.attackeight.just_enough_vh.mixin;

import iskallia.vault.integration.jei.IntegrationJEI;
import iskallia.vault.integration.jei.lootbox.WeightedListJEICategory;
import iskallia.vault.integration.jei.materialbox.MaterialBoxJEICategory;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = IntegrationJEI.class, remap = false)
public class IntegrationJEIMixin {

    @Redirect(method = "registerCategories", at = @At(value = "INVOKE", target = "Lmezz/jei/api/registration/IRecipeCategoryRegistration;addRecipeCategories([Lmezz/jei/api/recipe/category/IRecipeCategory;)V"))
    private void removeCats(IRecipeCategoryRegistration instance, IRecipeCategory<?>[] iRecipeCategories) {
        if (iRecipeCategories[0] instanceof WeightedListJEICategory || iRecipeCategories[0] instanceof MaterialBoxJEICategory)
            return;
        instance.addRecipeCategories(iRecipeCategories);
    }
}
