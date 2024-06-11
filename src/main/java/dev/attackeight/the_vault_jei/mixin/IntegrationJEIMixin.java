package dev.attackeight.the_vault_jei.mixin;

import dev.attackeight.the_vault_jei.jei.category.CatalystInfusionTableRecipeCategory;
import iskallia.vault.gear.crafting.recipe.CatalystForgeRecipe;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.integration.jei.IntegrationJEI;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IntegrationJEI.class)
public class IntegrationJEIMixin {

    @Redirect(method = "registerRecipeCatalysts", at = @At(value = "INVOKE", target = "Lmezz/jei/api/registration/IRecipeCatalystRegistration;addRecipeCatalyst(Lnet/minecraft/world/item/ItemStack;[Lmezz/jei/api/recipe/RecipeType;)V"), remap = false)
    private void the_vault_jei$updateCataTableRecipeCat(IRecipeCatalystRegistration instance, ItemStack ingredient, RecipeType<?>[] recipeTypes) {
        instance.addRecipeCatalyst(new ItemStack(ModBlocks.CATALYST_INFUSION_TABLE), new RecipeType[]{CatalystInfusionTableRecipeCategory.RECIPE_TYPE});

    }
}
