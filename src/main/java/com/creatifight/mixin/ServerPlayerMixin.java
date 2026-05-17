package com.creatifight.mixin;

import com.creatifight.CreatifightFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Handles the {@code /gamemode creative} exit case from Creatifight. Without this mixin,
 * {@code setGameMode(CREATIVE)} on a player already in CREATIVE (Creatifight's underlying
 * gamemode) is a vanilla no-op and leaves the flag stuck — there's no NeoForge event for
 * "gamemode change requested but didn't actually change." This mixin detects the case,
 * clears the flag, restores vanilla invulnerability, and returns {@code true} so the
 * command registers as a successful change.
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Inject(method = "setGameMode", at = @At("HEAD"), cancellable = true)
    private void creatifight$exitOnGamemodeCreative(GameType newMode, CallbackInfoReturnable<Boolean> cir) {
        if (newMode != GameType.CREATIVE) return;
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (!CreatifightFlag.isOn(self)) return;

        CreatifightFlag.setOn(self, false);
        self.getAbilities().invulnerable = true;
        self.onUpdateAbilities();
        self.sendSystemMessage(Component.literal("Exited Creatifight; now in regular Creative."));
        cir.setReturnValue(true);
    }
}
