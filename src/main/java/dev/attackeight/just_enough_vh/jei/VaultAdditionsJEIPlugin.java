package dev.attackeight.just_enough_vh.jei;

import io.github.a1qs.vaultadditions.config.Configs;
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
}
