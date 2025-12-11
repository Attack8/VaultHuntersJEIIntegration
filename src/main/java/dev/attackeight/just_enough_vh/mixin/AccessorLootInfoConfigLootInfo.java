package dev.attackeight.just_enough_vh.mixin;

import iskallia.vault.config.LootInfoConfig;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = LootInfoConfig.LootInfo.class, remap = false)
public interface AccessorLootInfoConfigLootInfo {
    @Accessor("lootTableKeys")
    Map<ResourceLocation, LootInfoConfig.LootTableData> getLootTableMap();

}
