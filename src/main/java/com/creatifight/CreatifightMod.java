package com.creatifight;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/** Mod entrypoint; registers the {@link CreatifightFlag} attachment. */
@Mod(CreatifightMod.MODID)
public class CreatifightMod {
    public static final String MODID = "creatifight";

    public CreatifightMod(IEventBus modEventBus) {
        CreatifightFlag.register(modEventBus);
    }
}
