package com.creatifight;

import java.util.Set;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/** Zeroes incoming damage for Creatifight players via {@code setAmount(0)}, leaving every vanilla visual/audio effect intact; {@code BYPASSES_INVULNERABILITY} sources pass through. Environmental hazards in {@link #SUPPRESSED_TYPES} are cancelled outright so no hurt flash/sound fires while the player explores. */
@EventBusSubscriber(modid = CreatifightMod.MODID)
public final class CreatifightDamageHandler {
    private static final Set<ResourceKey<DamageType>> SUPPRESSED_TYPES = Set.of(
            DamageTypes.DROWN,
            DamageTypes.IN_WALL,
            DamageTypes.FREEZE,
            DamageTypes.IN_FIRE,
            DamageTypes.ON_FIRE,
            DamageTypes.LAVA,
            DamageTypes.HOT_FLOOR);

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!CreatifightFlag.isOn(player)) return;
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
        if (event.getSource().typeHolder().is(SUPPRESSED_TYPES::contains)) {
            event.setCanceled(true);
            return;
        }
        event.setAmount(0);
    }

    private CreatifightDamageHandler() {}
}
