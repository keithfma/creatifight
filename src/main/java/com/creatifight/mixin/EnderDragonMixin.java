package com.creatifight.mixin;

import com.creatifight.CreatifightFlag;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Bypasses the {@code NO_CREATIVE_OR_SPECTATOR} filter in the four bounding-box-based hit
 * detections inside {@link EnderDragon#aiStep} (wing-slap × 2, head-bite, neck-bite) so the
 * dragon's physical-collision attacks land on Creatifight players. Dragon breath and fireballs
 * already work via the standard {@code canAttack} chain — this only addresses the
 * physical hits.
 *
 * <p>One {@code @ModifyExpressionValue} on the {@code GETSTATIC} of the predicate covers all
 * four call sites in the method.
 */
@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin {
    @ModifyExpressionValue(
            method = "aiStep",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/EntitySelector;NO_CREATIVE_OR_SPECTATOR:Ljava/util/function/Predicate;"))
    private Predicate<Entity> creatifight$includeCreatifightInDragonHits(Predicate<Entity> original) {
        return entity -> original.test(entity)
                || (entity instanceof Player player && CreatifightFlag.isOn(player));
    }
}
