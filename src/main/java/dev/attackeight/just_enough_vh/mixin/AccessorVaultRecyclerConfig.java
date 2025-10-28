package dev.attackeight.just_enough_vh.mixin;

import iskallia.vault.config.VaultRecyclerConfig;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = VaultRecyclerConfig.class, remap = false)
public interface AccessorVaultRecyclerConfig {
    @Accessor
    Map<ResourceLocation, VaultRecyclerConfig.RecyclerOutput> getRecyclingOutput();
}
