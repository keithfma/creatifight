package com.creatifight.mixin;

import com.creatifight.CreatifightFlag;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Bypasses the hardcoded {@code isCreative()} check inside
 * {@code Phantom$PhantomSweepAttackGoal.canContinueToUse}. It's a redundant check separate
 * from the {@code canAttack} chain — without bypassing it, phantoms acquire a Creatifight
 * player as target, enter the SWOOP phase, then abort the dive on the very first
 * {@code canContinueToUse} tick and circle indefinitely.
 */
@Mixin(targets = "net/minecraft/world/entity/monster/Phantom$PhantomSweepAttackGoal")
public abstract class PhantomSweepAttackGoalMixin {
    @ModifyExpressionValue(
            method = "canContinueToUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isCreative()Z"))
    private boolean creatifight$bypassCreativeSweepFilter(boolean original, @Local LivingEntity livingentity) {
        if (livingentity instanceof Player player && CreatifightFlag.isOn(player)) {
            return false;
        }
        return original;
    }
}
