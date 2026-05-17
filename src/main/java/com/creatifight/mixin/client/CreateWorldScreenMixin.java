package com.creatifight.mixin.client;

import com.creatifight.CreatifightWorldDefault;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Detects when the user picks Creatifight in the New World screen and sets the client→server handoff flag for the level-load handler to consume. */
@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow @Final WorldCreationUiState uiState;

    @Inject(method = "onCreate", at = @At("HEAD"))
    private void creatifight$detectCreatifightSelection(CallbackInfo ci) {
        if (uiState.getGameMode() == WorldCreationUiState.SelectedGameMode.valueOf("CREATIFIGHT")) {
            CreatifightWorldDefault.PENDING_NEW_WORLD = true;
        }
    }
}
