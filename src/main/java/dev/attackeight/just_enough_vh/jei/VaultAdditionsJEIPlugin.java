package dev.attackeight.just_enough_vh.jei;

import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.init.ModBlocks;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import static dev.attackeight.just_enough_vh.jei.JEIRecipeProvider.getStatueLoot;
import static dev.attackeight.just_enough_vh.jei.TheVaultJEIPlugin.*;

public class VaultAdditionsJEIPlugin {

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
}
