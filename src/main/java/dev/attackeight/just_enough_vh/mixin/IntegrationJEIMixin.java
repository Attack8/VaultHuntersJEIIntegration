package dev.attackeight.just_enough_vh.mixin;

import iskallia.vault.integration.jei.IntegrationJEI;
import iskallia.vault.integration.jei.VaultRecyclerRecipeJEI;
import iskallia.vault.integration.jei.VaultRecyclerRecipeJEICategory;
import iskallia.vault.integration.jei.lootbox.WeightedListJEI;
import iskallia.vault.integration.jei.lootbox.WeightedListJEICategory;
import iskallia.vault.integration.jei.lootinfo.LootInfoGroupDefinition;
import iskallia.vault.integration.jei.materialbox.MaterialBoxJEI;
import iskallia.vault.integration.jei.materialbox.MaterialBoxJEICategory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = IntegrationJEI.class, remap = false)
public class IntegrationJEIMixin {

    @Redirect(method = "registerCategories", at = @At(value = "INVOKE", target = "Lmezz/jei/api/registration/IRecipeCategoryRegistration;addRecipeCategories([Lmezz/jei/api/recipe/category/IRecipeCategory;)V"))
    private void removeCats(IRecipeCategoryRegistration instance, IRecipeCategory<?>[] iRecipeCategories) {
        if (iRecipeCategories[0] instanceof WeightedListJEICategory || iRecipeCategories[0] instanceof MaterialBoxJEICategory || iRecipeCategories[0] instanceof VaultRecyclerRecipeJEICategory)
            return;
        instance.addRecipeCategories(iRecipeCategories);
    }

    @Redirect(method = "registerRecipes", at = @At(value = "INVOKE", target = "Lmezz/jei/api/registration/IRecipeRegistration;addRecipes(Lmezz/jei/api/recipe/RecipeType;Ljava/util/List;)V"))
    private <T> void removeRecipes(IRecipeRegistration instance, RecipeType<T> tRecipeType, List<T> ts) {
        if (tRecipeType.getRecipeClass().equals(WeightedListJEI.class) || tRecipeType.getRecipeClass().equals(MaterialBoxJEI.class) || tRecipeType.getRecipeClass().equals(VaultRecyclerRecipeJEI.class))
            return;
        instance.addRecipes(tRecipeType, ts);
    }

    @Redirect(method = "registerRecipeCatalysts", at = @At(value = "INVOKE", target = "Liskallia/vault/integration/jei/lootinfo/LootInfoGroupDefinitionRegistry;get()Ljava/util/Map;"))
    private Map<ResourceLocation, LootInfoGroupDefinition> removeLootInfoCatalysts() {
        return new HashMap<>();
    }

    @Redirect(method = "registerCategories", at = @At(value = "INVOKE", target = "Liskallia/vault/integration/jei/lootinfo/LootInfoGroupDefinitionRegistry;get()Ljava/util/Map;"))
    private Map<ResourceLocation, LootInfoGroupDefinition> removeLootInfoCategories() {
        return new HashMap<>();
    }

    @Redirect(method = "registerRecipes", at = @At(value = "INVOKE", target = "Liskallia/vault/integration/jei/lootinfo/LootInfoGroupDefinitionRegistry;get()Ljava/util/Map;"))
    private Map<ResourceLocation, LootInfoGroupDefinition> removeLootInfoRecipes() {
        return new HashMap<>();
    }
}
