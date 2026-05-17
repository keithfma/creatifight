package com.creatifight;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Per-tick check that keeps {@code abilities.invulnerable = false} for Creatifight players, defeating vanilla's gamemode-driven ability resets on respawn/setGameMode. */
@EventBusSubscriber(modid = CreatifightMod.MODID)
public final class CreatifightAbilitiesEnforcer {
    @SubscribeEvent
    public static void onServerTickPost(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            for (ServerPlayer player : level.players()) {
                if (CreatifightFlag.isOn(player) && player.getAbilities().invulnerable) {
                    player.getAbilities().invulnerable = false;
                    player.onUpdateAbilities();
                }
            }
        }
    }

    private CreatifightAbilitiesEnforcer() {}
}
