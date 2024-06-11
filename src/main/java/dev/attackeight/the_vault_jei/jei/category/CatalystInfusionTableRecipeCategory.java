package dev.attackeight.the_vault_jei.jei.category;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.attackeight.the_vault_jei.TheVaultJEI;
import dev.attackeight.the_vault_jei.utils.SlotPlacer;
import iskallia.vault.VaultMod;
import iskallia.vault.gear.crafting.recipe.CatalystForgeRecipe;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.recipe.CatalystInfusionTableRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class CatalystInfusionTableRecipeCategory implements IRecipeCategory<CatalystForgeRecipe> {
    public static final RecipeType<CatalystForgeRecipe> RECIPE_TYPE = RecipeType.create("the_vault", "catalyst_forge", CatalystForgeRecipe.class);
    private static final ResourceLocation TEXTURE = TheVaultJEI.rl("textures/gui/forge_table_base.png");
    private final IDrawable background;
    private final IDrawable icon;

    public CatalystInfusionTableRecipeCategory(final IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 46, 15, 100, 55);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CATALYST_INFUSION_TABLE));
    }

    @Nonnull
    public Component getTitle() {
        return ModBlocks.CATALYST_INFUSION_TABLE.getName();
    }

    @Nonnull
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    public IDrawable getIcon() {
        return this.icon;
    }

    @ParametersAreNonnullByDefault
    public void setRecipe(IRecipeLayoutBuilder builder, CatalystInfusionTableRecipe recipe, IFocusGroup focuses) {
        CatalystForgeRecipe catalystRecipe = ModConfigs.CATALYST_RECIPES.getConfigRecipes().get(0).makeRecipe();
        for (int x = 0; x < catalystRecipe.getInputs().size(); x++){
            builder.addSlot(RecipeIngredientRole.INPUT, SlotPlacer.ForgeRecipe.getX(x), SlotPlacer.ForgeRecipe.getY(x)).addIngredients(Ingredient.of(catalystRecipe.getInputs().get(x)));
        }
    }

    @Nonnull
    public RecipeType<CatalystForgeRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Nonnull
    public ResourceLocation getUid() {
        return this.getRecipeType().getUid();
    }

    @Nonnull
    public Class<? extends CatalystForgeRecipe> getRecipeClass() {
        return this.getRecipeType().getRecipeClass();
    }
}
