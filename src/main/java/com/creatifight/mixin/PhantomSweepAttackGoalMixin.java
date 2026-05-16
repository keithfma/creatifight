package com.creatifight.mixin;

import com.creatifight.CreatifightFlag;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
