package dev.attackeight.the_vault_jei;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import dev.attackeight.the_vault_jei.utils.ModConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import org.slf4j.Logger;

@Mod(TheVaultJEI.ID)
public class TheVaultJEI {

    public static final String ID = "the_vault_jei";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public TheVaultJEI() {
        ModLoadingContext.get().registerConfig(Type.CLIENT, ModConfig.SPEC, ID + "-client.toml");
    }

    public static final Multimap<ResourceLocation, Integer> BLACK_MARKET_ITEMS = HashMultimap.create();
    public static final Multimap<ResourceLocation, Integer> OMEGA_BLACK_MARKET_ITEMS = HashMultimap.create();
    public static final Multimap<ResourceLocation, Integer> SHOP_PEDESTAL_ITEMS = HashMultimap.create();

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(TheVaultJEI.ID, path);
    }

    public static boolean vaLoaded() {
        boolean loaded = ModList.get().isLoaded("vaultadditions");
        LOGGER.info("a/8: Loaded: {}", loaded);
        return loaded;
    }

}
