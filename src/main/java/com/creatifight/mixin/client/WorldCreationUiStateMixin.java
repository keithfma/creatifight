package com.creatifight.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Defaults "Allow Commands" to ON when the user picks CREATIFIGHT in the New World screen,
 * mirroring vanilla's default-ON behavior for CREATIVE. Without this, Creatifight worlds spawn
 * with cheats off and {@code /gamemode} is rejected — defeating the point of the whole feature.
 *
 * <p>Only kicks in when the user hasn't explicitly toggled the cheats button; explicit
 * OFF still wins.
 */
@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin {
    @Shadow public abstract WorldCreationUiState.SelectedGameMode getGameMode();
    @Shadow public abstract boolean isDebug();
    @Shadow public abstract boolean isHardcore();
    @Shadow private Boolean allowCommands;

    @ModifyReturnValue(method = "isAllowCommands", at = @At("RETURN"))
    private boolean creatifight$defaultAllowCommandsForCreatifight(boolean original) {
        if (original) return true;
        if (allowCommands != null) return original;
        if (isDebug() || isHardcore()) return original;
        if (getGameMode() == WorldCreationUiState.SelectedGameMode.valueOf("CREATIFIGHT")) {
            return true;
        }
        return original;
    }
}
