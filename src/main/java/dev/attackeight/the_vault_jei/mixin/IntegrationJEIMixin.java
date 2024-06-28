package dev.attackeight.the_vault_jei.mixin;

import iskallia.vault.integration.jei.IntegrationJEI;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(IntegrationJEI.class)
public class IntegrationJEIMixin {

    @Redirect(method = "registerRecipes", at = @At(value = "INVOKE", target = "Lmezz/jei/api/registration/IRecipeRegistration;addRecipes(Lmezz/jei/api/recipe/RecipeType;Ljava/util/List;)V", ordinal = 0), remap = false)
    private <T> void the_vault_jei$updateCataTableRecipes(IRecipeRegistration instance, RecipeType<T> tRecipeType, List<T> ts) {}

}
