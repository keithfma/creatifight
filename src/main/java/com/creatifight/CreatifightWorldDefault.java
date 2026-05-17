package com.creatifight;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** World-level state for the world-default Creatifight feature: an "is creatifight world" boolean and the set of players already auto-applied. */
public final class CreatifightWorldDefault {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CreatifightMod.MODID);

    public static final Supplier<AttachmentType<Boolean>> IS_CREATIFIGHT_WORLD =
            ATTACHMENT_TYPES.register("is_creatifight_world",
                    () -> AttachmentType.builder(() -> Boolean.FALSE)
                            .serialize(Codec.BOOL)
                            .build());

    public static final Supplier<AttachmentType<Set<UUID>>> INITIALIZED_PLAYERS =
            ATTACHMENT_TYPES.register("initialized_players",
                    () -> AttachmentType.<Set<UUID>>builder(() -> new HashSet<>())
                            .serialize(Codec.list(UUIDUtil.CODEC).xmap(
                                    list -> (Set<UUID>) new HashSet<>(list),
                                    set -> new ArrayList<>(set)))
                            .build());

    // Client→server handoff: client mixin sets this when the user picks Creatifight in the
    // New World screen; consumed by the LevelEvent.Load handler when the overworld loads.
    public static volatile boolean PENDING_NEW_WORLD = false;

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }

    public static boolean isOn(Level level) {
        return level.getData(IS_CREATIFIGHT_WORLD.get());
    }

    public static void setOn(Level level, boolean value) {
        level.setData(IS_CREATIFIGHT_WORLD.get(), value);
    }

    public static boolean hasInitialized(Level level, UUID playerId) {
        return level.getData(INITIALIZED_PLAYERS.get()).contains(playerId);
    }

    public static void markInitialized(Level level, UUID playerId) {
        Set<UUID> updated = new HashSet<>(level.getData(INITIALIZED_PLAYERS.get()));
        updated.add(playerId);
        level.setData(INITIALIZED_PLAYERS.get(), updated);
    }

    private CreatifightWorldDefault() {}
}
