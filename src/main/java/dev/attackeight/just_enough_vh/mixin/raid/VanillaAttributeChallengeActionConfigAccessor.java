package dev.attackeight.just_enough_vh.mixin.raid;

import iskallia.vault.core.vault.challenge.action.VanillaAttributeChallengeAction;
import iskallia.vault.core.world.roll.DoubleRoll;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = VanillaAttributeChallengeAction.Config.class, remap = false)
public interface VanillaAttributeChallengeActionConfigAccessor {
    @Accessor
    String getName();
    @Accessor
    Attribute getAttribute();
    @Accessor
    AttributeModifier.Operation getOperation();
    @Accessor
    List<DoubleRoll> getAmount();
}
