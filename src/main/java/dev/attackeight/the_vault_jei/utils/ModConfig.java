package dev.attackeight.the_vault_jei.utils;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
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
