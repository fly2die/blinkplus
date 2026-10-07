package com.f2d.blinkplus;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class SwiftBlinkItem extends BlinkDaggerItem {

    public SwiftBlinkItem(Properties properties) {
        super(properties);
    }

    // Свой звук телепорта
    @Override
    public SoundEvent getTeleportSound() {
        return ModSounds.SWIFT_BLINK;
    }

    // Даём скорость на 3 секунды после телепорта
    @Override
    public void onTeleport(Player player) {
        player.addEffect(new MobEffectInstance(
                MobEffects.SPEED,
                60,    // 3 секунды (20 тиков = 1 сек)
                0,     // уровень Speed I
                false, // ambient
                true,  // showParticles
                true   // showIcon
        ));
    }
}