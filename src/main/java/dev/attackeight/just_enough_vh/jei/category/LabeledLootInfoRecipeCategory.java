package dev.attackeight.just_enough_vh.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.attackeight.just_enough_vh.jei.LabeledLootInfo;
import iskallia.vault.VaultMod;
import iskallia.vault.util.LootInitialization;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LabeledLootInfoRecipeCategory implements IRecipeCategory<LabeledLootInfo> {

    private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/jei/loot_info.png");
    private final RecipeType<LabeledLootInfo> recipeType;
    private final IDrawable background;
    private final Component titleComponent;
    private final IDrawable icon;
    private final RecipeIngredientRole role;

    public LabeledLootInfoRecipeCategory(IGuiHelper guiHelper, RecipeType<LabeledLootInfo> recipeType, ItemStack icon, RecipeIngredientRole role) {
        this.recipeType = recipeType;
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 162, 108);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, icon);
        this.titleComponent = icon.getItem().getName(icon);
        this.role = role;
    }

    @Override
    public Component getTitle() {
        return titleComponent;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public RecipeType<LabeledLootInfo> getRecipeType() {
        return recipeType;
    }

    @SuppressWarnings("removal")
    @Override
    public ResourceLocation getUid() {
        return recipeType.getUid();
    }

    @SuppressWarnings("removal")
    @Override
    public Class<? extends LabeledLootInfo> getRecipeClass() {
        return recipeType.getRecipeClass();
    }

    public void setRecipe(IRecipeLayoutBuilder builder, LabeledLootInfo recipe, IFocusGroup focuses) {
        List<List<ItemStack>> itemList = recipe.itemStackList();
        int count = itemList.size();

        for (int i = 0; i < count; ++i) {
            if (itemList.get(i).size() == 1) {
                ItemStack base = itemList.get(i).get(0);
                ItemStack initialized = LootInitialization.initializeVaultLoot(base, 0);

                if (initialized.equals(base)) { // didn't change - no random data
                    builder.addSlot(role, 1 + 18 * (i % 9), 1 + 18 * (i / 9)).addIngredients(Ingredient.of(initialized));
                } else {
                    ItemStack[] variants = new ItemStack[10]; // changed - randomly generate 10 variants to loop through
                    variants[0] = initialized;
                    for (int n = 1; n < variants.length; n++) {
                        variants[n] = LootInitialization.initializeVaultLoot(base, 0);
                    }
                    builder.addSlot(role, 1 + 18 * (i % 9), 1 + 18 * (i / 9)).addIngredients(Ingredient.of(variants));
                }
            } else {
                // list of items (eg. altar ingredient accepting all logs) - don't try to gen random list of items
                builder.addSlot(role, 1 + 18 * (i % 9), 1 + 18 * (i / 9)).addIngredients(Ingredient.of(itemList.get(i).stream()
                    .map( is -> LootInitialization.initializeVaultLoot(is, 0))));
            }
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
