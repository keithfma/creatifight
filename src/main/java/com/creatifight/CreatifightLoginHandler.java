package com.creatifight;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

/** Marks the world's Creatifight flag on overworld load when client-side selection set it, and auto-applies Creatifight to players on their first join to a flagged world. */
@EventBusSubscriber(modid = CreatifightMod.MODID)
public final class CreatifightLoginHandler {

    // Consumes the client-side PENDING_NEW_WORLD handoff: when the freshly-created overworld
    // loads, write the persistent world flag and clear the handoff.
    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(Level.OVERWORLD)) return;
        if (!CreatifightWorldDefault.PENDING_NEW_WORLD) return;
        CreatifightWorldDefault.setOn(level, true);
        CreatifightWorldDefault.PENDING_NEW_WORLD = false;
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ServerLevel overworld = player.getServer().overworld();
        if (!CreatifightWorldDefault.isOn(overworld)) return;
        if (CreatifightWorldDefault.hasInitialized(overworld, player.getUUID())) return;

        // Mark first so re-entries (e.g., crash recovery mid-handler) don't loop.
        CreatifightWorldDefault.markInitialized(overworld, player.getUUID());
        if (CreatifightFlag.isOn(player)) return;

        player.setGameMode(GameType.CREATIVE);
        CreatifightFlag.setOn(player, true);
        player.getAbilities().invulnerable = false;
        player.onUpdateAbilities();
        player.sendSystemMessage(Component.literal("Welcome to Creatifight!"));
    }

    private CreatifightLoginHandler() {}
}
