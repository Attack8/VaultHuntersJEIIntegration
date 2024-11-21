package dev.attackeight.the_vault_jei;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TheVaultJEI.ID)
public class TheVaultJEI {

    public static final String ID = "the_vault_jei";

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public TheVaultJEI() {}

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(TheVaultJEI.ID, path);
    }

    public static boolean hasWolds() {
        return ModList.get().isLoaded("woldsvaults");
    }

}
