package dev.attackeight.the_vault_jei;

import com.mojang.logging.LogUtils;
import dev.attackeight.the_vault_jei.utils.ModConfig;
import net.minecraft.resources.ResourceLocation;
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

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(TheVaultJEI.ID, path);
    }

}
