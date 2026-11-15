package com.weaverdeath;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WeaverDeathMod.MODID)
public class WeaverDeathMod {
    public static final String MODID = "weaverdeath";

    public WeaverDeathMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModSounds.SOUNDS.register(modBus);
        Net.init();
        modBus.addListener(SoundPack::onAddPackFinders);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);

        MinecraftForge.EVENT_BUS.register(ServerEvents.class);
    }
}
