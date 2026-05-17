package com.creatifight.mixin;

import com.creatifight.CreatifightFlag;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Bypasses {@code isCreative()} filters in {@link MeleeAttackGoal} that would otherwise stop
 * mobs from attacking Creatifight players (who remain {@code isCreative() == true} under the hood).
 *
 * <p>{@code canContinueToUse} (with {@code followingTargetEvenIfNotSeen=true}: spiders, vexes,
 * ravagers, etc.) explicitly returns false for creative targets — bypassed. {@code stop} calls
 * {@code NO_CREATIVE_OR_SPECTATOR.test} and clears the target on every goal stop — suppressed
 * for Creatifight targets so zombies don't cycle.
 */
@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin {
    @Shadow @Final protected PathfinderMob mob;

    @ModifyExpressionValue(
            method = "canContinueToUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isCreative()Z"))
    private boolean creatifight$bypassCreativeContinueCheck(boolean original) {
        LivingEntity target = this.mob.getTarget();
        if (target instanceof Player player && CreatifightFlag.isOn(player)) {
            return false;
        }
        return original;
    }

    @WrapOperation(
            method = "stop",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void creatifight$preserveCreatifightTarget(Mob self, LivingEntity newTarget, Operation<Void> original) {
        LivingEntity current = self.getTarget();
        if (current instanceof Player player && CreatifightFlag.isOn(player)) {
            return;
        }
        original.call(self, newTarget);
    }
}
