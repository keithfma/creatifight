package com.creatifight.mixin;

import com.creatifight.CreatifightFlag;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Bypasses the {@code NO_CREATIVE_OR_SPECTATOR.test} filter inside {@link Warden#canTargetEntity}.
 * This single method is the chokepoint for every Warden targeting path (entity sensor, roar
 * target selection, anger management, vibration listener), so one bypass cascades to all
 * Warden behaviors against Creatifight players. The method's other vanilla filters (allied,
 * armor stand, dead, etc.) are preserved — we only flip the gamemode check.
 */
@Mixin(Warden.class)
public abstract class WardenMixin {
    @ModifyExpressionValue(
            method = "canTargetEntity",
            at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private boolean creatifight$bypassCreativeFilter(boolean original, @Local(argsOnly = true) Entity entity) {
        if (entity instanceof Player player && CreatifightFlag.isOn(player)) {
            return true;
        }
        return original;
    }
}
