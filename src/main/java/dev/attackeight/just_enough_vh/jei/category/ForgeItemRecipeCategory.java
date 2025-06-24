package dev.attackeight.just_enough_vh.jei.category;

import dev.attackeight.just_enough_vh.JustEnoughVH;
import dev.attackeight.just_enough_vh.jei.ForgeItem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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

public class ForgeItemRecipeCategory implements IRecipeCategory<ForgeItem> {
    private static final ResourceLocation TEXTURE = JustEnoughVH.rl("textures/gui/forge_table_base.png");
    private final RecipeType<ForgeItem> recipeType;
    private final IDrawable background;
    private final Component titleComponent;
    private final IDrawable icon;

    public ForgeItemRecipeCategory(IGuiHelper guiHelper, RecipeType<ForgeItem> recipeType, ItemStack icon) {
        this.recipeType = recipeType;
        this.titleComponent = icon.getItem().getName(icon);
        this.background = guiHelper.createDrawable(TEXTURE, 46, 15, 100, 55);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, icon);
    }

    @Nonnull
    public Component getTitle() {
        return titleComponent;
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
    public void setRecipe(IRecipeLayoutBuilder builder, ForgeItem recipe, IFocusGroup focuses) {
        for (int x = 0; x < recipe.ingredients().size(); x++){
            builder.addSlot(RecipeIngredientRole.INPUT, getX(x), getY(x)).addIngredients(Ingredient.of(recipe.ingredients().get(x)));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 20).addIngredients(Ingredient.of(recipe.output()));
    }

    @Nonnull
    public RecipeType<ForgeItem> getRecipeType() {
        return recipeType;
    }

    @SuppressWarnings("removal")
    @Nonnull
    public ResourceLocation getUid() {
        return this.getRecipeType().getUid();
    }

    @SuppressWarnings("removal")
    @Nonnull
    public Class<? extends ForgeItem> getRecipeClass() {
        return this.getRecipeType().getRecipeClass();
    }

    public static int getX(int index) {
        return switch (index) {
            case 3,4,5 -> 20;
            case 6,7,8 -> 38;
            default -> 2;
        };
    }

    public static int getY(int index) {
        return switch (index) {
            case 1,4,7 -> 20;
            case 2,5,8 -> 38;
            default -> 2;
        };
    }
}
