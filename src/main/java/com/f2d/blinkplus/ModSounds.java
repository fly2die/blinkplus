package com.f2d.blinkplus;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {

    public static final SoundEvent BLINK_DAGGER = register("blink_dagger");
    public static final SoundEvent ARCANE_BLINK = register("arcane_blink");
    public static final SoundEvent SWIFT_BLINK = register("swift_blink");
    public static final SoundEvent OVERWHELMING_BLINK = register("overwhelming_blink");


    private static SoundEvent register(String name) {
        return Registry.register(
                BuiltInRegistries.SOUND_EVENT,
                Blink.id(name),
                SoundEvent.createVariableRangeEvent(Blink.id(name))
        );
    }

    public static void initialize() {
    }
}