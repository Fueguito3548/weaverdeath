package com.weaverdeath;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WeaverDeathMod.MODID);

    public static final RegistryObject<SoundEvent> RISE = reg("rise");
    public static final RegistryObject<SoundEvent> ORB_APPEAR = reg("orb_appear");
    public static final RegistryObject<SoundEvent> DARK_BURST = reg("dark_burst");
    public static final RegistryObject<SoundEvent> MASK_APPEAR = reg("mask_appear");
    public static final RegistryObject<SoundEvent> MOUTH_OPEN = reg("mouth_open");
    public static final RegistryObject<SoundEvent> TREMBLE = reg("tremble");
    public static final RegistryObject<SoundEvent> ORB_ENTER = reg("orb_enter");
    public static final RegistryObject<SoundEvent> EYES = reg("eyes");
    public static final RegistryObject<SoundEvent> LIGHT_GROW = reg("light_grow");
    public static final RegistryObject<SoundEvent> SNAP = reg("snap");
    public static final RegistryObject<SoundEvent> WAIL = reg("wail");
    public static final RegistryObject<SoundEvent> SMOKE = reg("smoke");
    public static final RegistryObject<SoundEvent> VANISH = reg("vanish");

    private static RegistryObject<SoundEvent> reg(String name) {
        return SOUNDS.register(name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(WeaverDeathMod.MODID, name)));
    }

    private ModSounds() {
    }
}
