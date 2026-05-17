package com.creatifight.mixin.client;

import java.util.Arrays;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds a {@code CREATIFIGHT} entry to vanilla's {@code WorldCreationUiState.SelectedGameMode}
 * inner enum so Creatifight appears as a 5th option in the New World screen's gamemode picker.
 *
 * <p>The enum has no {@code IExtensibleEnum}, so we use {@code @Invoker} on the private enum
 * constructor + {@code @Inject} at the tail of {@code <clinit>} to expand the synthetic
 * {@code $VALUES} array. The new entry's gameType is {@code CREATIVE} (Creatifight is built on
 * top of vanilla creative + a per-player flag). Display name + info come from the
 * {@code selectWorld.gameMode.creatifight} translation keys in the mod's en_us.json.
 */
@Mixin(WorldCreationUiState.SelectedGameMode.class)
public abstract class SelectedGameModeMixin {

    @Invoker("<init>")
    public static WorldCreationUiState.SelectedGameMode creatifight$create(
            String enumName, int ordinal, String id, GameType gameType) {
        throw new AssertionError();
    }

    @Shadow @Final @Mutable
    private static WorldCreationUiState.SelectedGameMode[] $VALUES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void creatifight$addCreatifight(CallbackInfo ci) {
        WorldCreationUiState.SelectedGameMode creatifight = creatifight$create(
                "CREATIFIGHT", $VALUES.length, "creatifight", GameType.CREATIVE);
        WorldCreationUiState.SelectedGameMode[] expanded = Arrays.copyOf($VALUES, $VALUES.length + 1);
        expanded[$VALUES.length] = creatifight;
        $VALUES = expanded;
    }
}
