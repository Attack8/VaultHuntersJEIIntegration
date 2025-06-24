package dev.attackeight.just_enough_vh;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import org.slf4j.Logger;

@Mod(JustEnoughVH.ID)
public class JustEnoughVH {

    public static final String ID = "just_enough_vh";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public JustEnoughVH() {
        ModLoadingContext.get().registerConfig(Type.CLIENT, ModConfig.SPEC, ID + "-client.toml");
    }

    public static final Multimap<ResourceLocation, Integer> BLACK_MARKET_ITEMS = HashMultimap.create();
    public static final Multimap<ResourceLocation, Integer> OMEGA_BLACK_MARKET_ITEMS = HashMultimap.create();
    public static final Multimap<ResourceLocation, Integer> SHOP_PEDESTAL_ITEMS = HashMultimap.create();

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(JustEnoughVH.ID, path);
    }

    public static boolean vaLoaded() {
        return ModList.get().isLoaded("vaultadditions");
    }

    public static class ModConfig {
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec SPEC;

        private static final ForgeConfigSpec.BooleanValue SHOW;

        public static boolean shouldShow() {
            return SHOW.get();
        }

        static {
            SHOW = BUILDER.comment("Should JEI show loot tables for items not used in the base modpack")
                    .define("enable_hidden", false);
            SPEC = BUILDER.build();
        }
    }

}
