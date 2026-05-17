package com.creatifight.mixin.client;

import java.util.Arrays;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Appends the dynamically-added SelectedGameMode.CREATIFIGHT to the gamemode CycleButton's values list in CreateWorldScreen$GameTab's constructor. */
@Mixin(targets = "net/minecraft/client/gui/screens/worldselection/CreateWorldScreen$GameTab")
public abstract class CreateWorldScreenGameTabMixin {
    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/CycleButton$Builder;withValues([Ljava/lang/Object;)Lnet/minecraft/client/gui/components/CycleButton$Builder;",
                    ordinal = 0))
    private Object[] creatifight$addCreatifightToGameModePicker(Object[] values) {
        Object[] expanded = Arrays.copyOf(values, values.length + 1);
        expanded[values.length] = WorldCreationUiState.SelectedGameMode.valueOf("CREATIFIGHT");
        return expanded;
    }
}
