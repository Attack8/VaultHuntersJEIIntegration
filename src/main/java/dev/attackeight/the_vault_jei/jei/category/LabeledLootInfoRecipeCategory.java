package dev.attackeight.the_vault_jei.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.attackeight.the_vault_jei.jei.LabeledLootInfo;
import iskallia.vault.VaultMod;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class LabeledLootInfoRecipeCategory implements IRecipeCategory<LabeledLootInfo> {

    private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/jei/loot_info.png");
    private final RecipeType<LabeledLootInfo> recipeType;
    private final IDrawable background;
    private final Component titleComponent;
    private final IDrawable icon;

    public LabeledLootInfoRecipeCategory(IGuiHelper guiHelper, RecipeType<LabeledLootInfo> recipeType, ItemStack icon, Component title) {
        this.recipeType = recipeType;
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 162, 108);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, icon);
        this.titleComponent = title;
    }

    @Override
    public @NotNull Component getTitle() {
        return titleComponent;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public @NotNull RecipeType<LabeledLootInfo> getRecipeType() {
        return recipeType;
    }

    @Override
    public ResourceLocation getUid() {
        return recipeType.getUid();
    }

    @Override
    public Class<? extends LabeledLootInfo> getRecipeClass() {
        return recipeType.getRecipeClass();
    }

    @ParametersAreNonnullByDefault
    public void setRecipe(IRecipeLayoutBuilder builder, LabeledLootInfo recipe, IFocusGroup focuses) {
        List<ItemStack> itemList = recipe.itemStackList();
        int count = itemList.size();

        for(int i = 0; i < count; ++i) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 1 + 18 * (i % 9), 1 + 18 * (i / 9)).addItemStack((ItemStack)itemList.get(i));
        }

    }

    @Override
    public void draw(LabeledLootInfo recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, stack, mouseX, mouseY);
        Minecraft minecraft = Minecraft.getInstance();
        int xPos = 0;
        int yPos = - (minecraft.font.lineHeight + 4);
        if (recipe.line2() != null) {
            minecraft.font.draw(stack, recipe.line2(), xPos, yPos, 0xFF000000);
            yPos -= minecraft.font.lineHeight + 2;
        }
        minecraft.font.draw(stack, recipe.label(), xPos, yPos, 0xFF000000);
    }
}
