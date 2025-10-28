package dev.attackeight.just_enough_vh.jei;

import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.config.vault.AbstractStatueLootConfig;
import io.github.a1qs.vaultadditions.init.ModBlocks;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static dev.attackeight.just_enough_vh.jei.JEIRecipeProvider.formatItemStack;
import static dev.attackeight.just_enough_vh.jei.TheVaultJEIPlugin.*;

public class VaultAdditionsJEIPlugin {

	public static void registerStatueCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.LOOT_STATUE_ARENA.get()), VA_ARENA_STATUE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.LOOT_STATUE_GIFT.get()), VA_GIFT_STATUE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.LOOT_STATUE_GIFT_MEGA.get()), VA_MEGA_GIFT_STATUE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.LOOT_STATUE_VAULT.get()), VA_VAULT_STATUE);
	}

	public static void registerStatueRecipes(IRecipeRegistration registration) {
		registration.addRecipes(VA_ARENA_STATUE, getStatueLoot(Configs.STATUE_LOOT_ARENA));
		registration.addRecipes(VA_GIFT_STATUE, getStatueLoot(Configs.STATUE_LOOT_GIFT));
		registration.addRecipes(VA_MEGA_GIFT_STATUE, getStatueLoot(Configs.STATUE_LOOT_MEGA_GIFT));
		registration.addRecipes(VA_VAULT_STATUE, getStatueLoot(Configs.STATUE_LOOT_VAULT));
	}

	public static void registerStatueCategories(IRecipeCategoryRegistration registration, IGuiHelper guiHelper) {
		registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, VA_ARENA_STATUE, ModBlocks.LOOT_STATUE_ARENA.get()));
		registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, VA_GIFT_STATUE, ModBlocks.LOOT_STATUE_GIFT.get()));
		registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, VA_MEGA_GIFT_STATUE, ModBlocks.LOOT_STATUE_GIFT_MEGA.get()));
		registration.addRecipeCategories(makeLabeledLootInfoCategory(guiHelper, VA_VAULT_STATUE, ModBlocks.LOOT_STATUE_VAULT.get()));
	}


	public static <T extends AbstractStatueLootConfig> List<LabeledLootInfo> getStatueLoot(T lootConfig) {
		List<LabeledLootInfo> toReturn = new ArrayList<>();
		List<ItemStack> items = new ArrayList<>();
		int interval = lootConfig.getInterval();
		int minCount = lootConfig.getRollRange().getMin();
		int maxCount = lootConfig.getRollRange().getMax();
		int totalWeight = lootConfig.getDrops().getTotalWeight();
		lootConfig.getDrops().forEach((product, weight) ->
				items.add(formatItemStack(product.generateItemStack(), product.amountMin, product.amountMax, weight.intValue(), totalWeight, null)));
		toReturn.add(LabeledLootInfo.of(items,
				new TextComponent("Interval: " + interval),
				new TextComponent("Count: " + minCount + " - " + maxCount)
		));
		return toReturn;
	}
}
