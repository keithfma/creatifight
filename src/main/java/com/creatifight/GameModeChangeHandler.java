package com.creatifight;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = CreatifightMod.MODID)
public final class GameModeChangeHandler {
    @SubscribeEvent
    public static void onChangeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (event.getNewGameMode() == GameType.CREATIVE) {
            return;
        }
        Player player = event.getEntity();
        if (CreatifightFlag.isOn(player)) {
            CreatifightFlag.setOn(player, false);
            player.sendSystemMessage(Component.literal("You're no longer in Creatifight."));
        }
    }

    private GameModeChangeHandler() {}
}
