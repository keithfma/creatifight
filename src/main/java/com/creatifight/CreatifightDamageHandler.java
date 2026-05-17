package com.creatifight;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/** Zeroes incoming damage for Creatifight players via {@code setAmount(0)}, leaving every vanilla visual/audio effect intact; {@code BYPASSES_INVULNERABILITY} sources pass through. */
@EventBusSubscriber(modid = CreatifightMod.MODID)
public final class CreatifightDamageHandler {
    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!CreatifightFlag.isOn(player)) return;
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
        event.setAmount(0);
    }

    private CreatifightDamageHandler() {}
}
