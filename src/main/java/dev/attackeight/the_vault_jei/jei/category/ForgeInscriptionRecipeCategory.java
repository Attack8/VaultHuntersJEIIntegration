package dev.attackeight.the_vault_jei.jei.category;

import dev.attackeight.the_vault_jei.TheVaultJEI;
import dev.attackeight.the_vault_jei.utils.SlotPlacer;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.gear.crafting.recipe.InscriptionForgeRecipe;
import iskallia.vault.init.ModBlocks;
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

public class ForgeInscriptionRecipeCategory implements IRecipeCategory<InscriptionForgeRecipe> {
    public static final RecipeType<InscriptionForgeRecipe> RECIPE_TYPE = RecipeType.create("the_vault", "inscription_forge", InscriptionForgeRecipe.class);
    private static final ResourceLocation TEXTURE = TheVaultJEI.rl("textures/gui/forge_table_base.png");
    private final IDrawable background;
    private final IDrawable icon;

    public ForgeInscriptionRecipeCategory(final IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 46, 15, 100, 55);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.INSCRIPTION_TABLE));
    }

    @Nonnull
    public Component getTitle() {
        return ModBlocks.INSCRIPTION_TABLE.getName();
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
    public void setRecipe(IRecipeLayoutBuilder builder, InscriptionForgeRecipe recipe, IFocusGroup focuses) {
        for (int x = 0; x < recipe.getInputs().size(); x++){
            builder.addSlot(RecipeIngredientRole.INPUT, SlotPlacer.ForgeRecipe.getX(x), SlotPlacer.ForgeRecipe.getY(x)).addIngredients(Ingredient.of(recipe.getInputs().get(x)));
        }
        List<OverSizedItemStack> overSized = new ArrayList<>();
        recipe.getInputs().forEach(b -> overSized.add(OverSizedItemStack.of(b)));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 20).addIngredients(Ingredient.of(recipe.createOutput(overSized, null, 100)));
    }

    @Nonnull
    public RecipeType<InscriptionForgeRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Nonnull
    public ResourceLocation getUid() {
        return this.getRecipeType().getUid();
    }

    @Nonnull
    public Class<? extends InscriptionForgeRecipe> getRecipeClass() {
        return this.getRecipeType().getRecipeClass();
    }
}
