package dev.attackeight.the_vault_jei.jei.category;

import iskallia.vault.VaultMod;
import iskallia.vault.config.MysteryEggConfig;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.data.WeightedList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class MysteryEggRecipeCategory implements IRecipeCategory<MysteryEggConfig> {
    private static final ResourceLocation TEXTURE = VaultMod.id("textures/gui/jei/loot_info.png");
    public static final RecipeType<MysteryEggConfig> RECIPE_TYPE = RecipeType.create("the_vault", "mystery_egg_info", MysteryEggConfig.class);

    private static final TextComponent TITLE = new TextComponent("Mystery Egg");
    private final IDrawable background;
    private final IDrawable icon;

    public MysteryEggRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 162, 108);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ModItems.MYSTERY_EGG.getDefaultInstance());
    }

    @Nonnull
    public Component getTitle() {
        return TITLE;
    }

    @Nonnull
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    public IDrawable getIcon() {
        return this.icon;
    }

    @Nonnull
    public RecipeType<MysteryEggConfig> getRecipeType() {
        return this.RECIPE_TYPE;
    }

    @Nonnull
    public ResourceLocation getUid() {
        return this.getRecipeType().getUid();
    }

    @Nonnull
    public Class<? extends MysteryEggConfig> getRecipeClass() {
        return this.getRecipeType().getRecipeClass();
    }

    @ParametersAreNonnullByDefault
    public void setRecipe(IRecipeLayoutBuilder builder, MysteryEggConfig recipe, IFocusGroup focuses) {
        List<ItemStack> itemList = new ArrayList<>();
        recipe.POOL.forEach(b -> itemList.add(b.value.generateItemStack()));

        int count = itemList.size();

        for(int i = 0; i < count; ++i) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 1 + 18 * (i % 9), 1 + 18 * (i / 9)).addItemStack(addChanceTooltip((ItemStack)itemList.get(i)));
        }

    }

    public static ItemStack addChanceTooltip(ItemStack stack)
    {
        int totalWeight = ModConfigs.MYSTERY_EGG.POOL.getTotalWeight();
        CompoundTag nbt = stack.getOrCreateTagElement("display");
        ListTag list = nbt.getList("Lore", 8);

        WeightedList<ProductEntry> entries = ModConfigs.MYSTERY_EGG.POOL;
        for(WeightedList.Entry<ProductEntry> entry : entries) {
            if(entry.value.generateItemStack().getItem().equals(stack.getItem())) {
                MutableComponent component = new TextComponent("Chance: ");
                double chance = ((double) entry.weight / totalWeight) * 100;
                component.append(String.format("%.2f", chance));
                component.append("%");
                list.add(StringTag.valueOf(Component.Serializer.toJson(component.withStyle(ChatFormatting.YELLOW))));
            }
        }


        nbt.put("Lore", list);
        return stack;
    }

}
