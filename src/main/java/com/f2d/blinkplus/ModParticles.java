package com.f2d.blinkplus;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModParticles {

    public static final SimpleParticleType BLINK_PART_TP = FabricParticleTypes.simple();
    public static final SimpleParticleType BLINK_PART_BLINK = FabricParticleTypes.simple();

    public static void initialize() {
        Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                Blink.id("blink_part_tp"),
                BLINK_PART_TP
        );
        Registry.register(
                BuiltInRegistries.PARTICLE_TYPE,
                Blink.id("blink_part_blink"),
                BLINK_PART_BLINK
        );
    }
}