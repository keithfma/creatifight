package com.creatifight;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Per-player Boolean flag indicating Creatifight mode; persists across save/load and respawn. */
public final class CreatifightFlag {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CreatifightMod.MODID);

    public static final Supplier<AttachmentType<Boolean>> IN_CREATIFIGHT =
            ATTACHMENT_TYPES.register("in_creatifight",
                    () -> AttachmentType.builder(() -> Boolean.FALSE)
                            .serialize(Codec.BOOL)
                            .copyOnDeath()
                            .build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }

    public static boolean isOn(Player player) {
        return player.getData(IN_CREATIFIGHT.get());
    }

    public static void setOn(Player player, boolean value) {
        player.setData(IN_CREATIFIGHT.get(), value);
    }

    private CreatifightFlag() {}
}
