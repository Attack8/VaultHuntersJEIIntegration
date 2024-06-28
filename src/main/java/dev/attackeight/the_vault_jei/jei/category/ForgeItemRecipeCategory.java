package dev.attackeight.the_vault_jei.jei.category;

import dev.attackeight.the_vault_jei.TheVaultJEI;
import dev.attackeight.the_vault_jei.jei.ForgeItem;
import dev.attackeight.the_vault_jei.utils.SlotPlacer;
import iskallia.vault.container.oversized.OverSizedItemStack;
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
import java.util.ArrayList;
import java.util.List;

public class ForgeItemRecipeCategory implements IRecipeCategory<ForgeItem> {
    private static final ResourceLocation TEXTURE = TheVaultJEI.rl("textures/gui/forge_table_base.png");
    private final RecipeType<ForgeItem> recipeType;
    private final IDrawable background;
    private final Component titleComponent;
    private final IDrawable icon;

    public ForgeItemRecipeCategory(IGuiHelper guiHelper, RecipeType<ForgeItem> recipeType, ItemStack icon, Component title) {
        this.recipeType = recipeType;
        this.titleComponent = title;
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
            builder.addSlot(RecipeIngredientRole.INPUT, SlotPlacer.ForgeRecipe.getX(x), SlotPlacer.ForgeRecipe.getY(x)).addIngredients(Ingredient.of(recipe.ingredients().get(x)));
        }
        List<OverSizedItemStack> overSized = new ArrayList<>();
        recipe.ingredients().forEach(b -> overSized.add(OverSizedItemStack.of(b)));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 20).addIngredients(Ingredient.of(recipe.output()));
    }

    @Nonnull
    public RecipeType<ForgeItem> getRecipeType() {
        return recipeType;
    }

    @Nonnull
    public ResourceLocation getUid() {
        return this.getRecipeType().getUid();
    }

    @Nonnull
    public Class<? extends ForgeItem> getRecipeClass() {
        return this.getRecipeType().getRecipeClass();
    }
}
