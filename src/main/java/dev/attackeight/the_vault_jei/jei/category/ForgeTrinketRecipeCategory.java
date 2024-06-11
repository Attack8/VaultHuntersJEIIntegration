package dev.attackeight.the_vault_jei.jei.category;

import dev.attackeight.the_vault_jei.TheVaultJEI;
import dev.attackeight.the_vault_jei.utils.SlotPlacer;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.gear.crafting.recipe.TrinketForgeRecipe;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class ForgeTrinketRecipeCategory implements IRecipeCategory<TrinketForgeRecipe> {
    public static final RecipeType<TrinketForgeRecipe> RECIPE_TYPE = RecipeType.create("the_vault", "trinket_forge", TrinketForgeRecipe.class);
    private static final ResourceLocation TEXTURE = TheVaultJEI.rl("textures/gui/forge_table_base.png");
    private final IDrawable background;
    private final IDrawable icon;

    public ForgeTrinketRecipeCategory(final IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 46, 15, 100, 55);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.TRINKET));
    }

    @Nonnull
    public Component getTitle() {
        return ModBlocks.VAULT_FORGE.getName();
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
    public void setRecipe(IRecipeLayoutBuilder builder, TrinketForgeRecipe recipe, IFocusGroup focuses) {
        for (int x = 0; x < recipe.getInputs().size(); x++){
            builder.addSlot(RecipeIngredientRole.INPUT, SlotPlacer.ForgeRecipe.getX(x), SlotPlacer.ForgeRecipe.getY(x)).addIngredients(Ingredient.of(recipe.getInputs().get(x)));
        }
        List<OverSizedItemStack> overSized = new ArrayList<>();
        recipe.getInputs().forEach(b -> overSized.add(OverSizedItemStack.of(b)));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 20).addIngredients(Ingredient.of(recipe.createOutput(overSized, getFakePlayer(), 0)));
    }

    @Nonnull
    public RecipeType<TrinketForgeRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Nonnull
    public ResourceLocation getUid() {
        return this.getRecipeType().getUid();
    }

    @Nonnull
    public Class<? extends TrinketForgeRecipe> getRecipeClass() {
        return this.getRecipeType().getRecipeClass();
    }

    private FakePlayer getFakePlayer() {
        ServerLevel serverLevel = Minecraft.getInstance().hasSingleplayerServer() ? Minecraft.getInstance().getSingleplayerServer().overworld() : Minecraft.getInstance().player.getServer().overworld();
        return new FakePlayer(serverLevel, Minecraft.getInstance().getUser().getGameProfile());
    }
}
