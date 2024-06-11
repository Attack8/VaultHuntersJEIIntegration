package dev.attackeight.the_vault_jei;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.stream.Collectors;

@Mod(TheVaultJEI.ID)
public class TheVaultJEI {

    public static final String ID = "the_vault_jei";

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public TheVaultJEI() {



    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(TheVaultJEI.ID, path);
    }

}
