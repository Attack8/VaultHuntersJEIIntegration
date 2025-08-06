package dev.attackeight.just_enough_vh.jei.category;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.attackeight.just_enough_vh.jei.RecyclerRecipe;
import iskallia.vault.VaultMod;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
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

public class VaultRecyclerRecipeCategory implements IRecipeCategory<RecyclerRecipe> {
    private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/vault_recycler_jei.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
    private final RecipeType<RecyclerRecipe> recipeType;

    public VaultRecyclerRecipeCategory(IGuiHelper guiHelper, RecipeType<RecyclerRecipe> recipeType) {
        this.background = guiHelper.createDrawable(TEXTURE, 33, 30, 104, 26);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.VAULT_RECYCLER));
        this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<>() {
            @Nonnull
            public IDrawableAnimated load(@Nonnull Integer time) {
                return guiHelper.drawableBuilder(TEXTURE, 176, 0, 24, 17).buildAnimated(time, IDrawableAnimated.StartDirection.LEFT, false);
            }
        });
        this.recipeType = recipeType;
    }

    @Nonnull
    public Component getTitle() {
        return ModBlocks.VAULT_RECYCLER.getName();
    }

    @Nonnull
    public IDrawable getBackground() {
        return background;
    }

    @Nonnull
    public IDrawable getIcon() {
        return icon;
    }

    @ParametersAreNonnullByDefault
    public void setRecipe(IRecipeLayoutBuilder builder, RecyclerRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(Ingredient.of(recipe.input().stream()));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 49, 5).addItemStack(recipe.outputs().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 67, 5).addItemStack(recipe.outputs().get(1));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 5).addItemStack(recipe.outputs().get(2));
    }

    @ParametersAreNonnullByDefault
    public void draw(RecyclerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
        IDrawableAnimated arrow = this.cachedArrows.getUnchecked(ModConfigs.VAULT_RECYCLER.getProcessingTickTime());
        arrow.draw(poseStack, 21, 4);
    }

    @Nonnull
    public RecipeType<RecyclerRecipe> getRecipeType() {
        return recipeType;
    }

    @SuppressWarnings("removal")
    @Nonnull
    public ResourceLocation getUid() {
        return this.getRecipeType().getUid();
    }

    @SuppressWarnings("removal")
    @Nonnull
    public Class<? extends RecyclerRecipe> getRecipeClass() {
        return this.getRecipeType().getRecipeClass();
    }
}
