package com.f2d.blinkplus;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

public class ArcaneBlinkItem extends BlinkDaggerItem {

    public ArcaneBlinkItem(Properties properties) {
        super(properties);
    }

    @Override
    public double getMaxDistance() {
        return 12.0;
    }

    @Override
    public SoundEvent getTeleportSound() {
        return ModSounds.ARCANE_BLINK;
    }

    @Override
    public void onTeleport(Player player) {
        player.heal(2.0f); // восстанавливаем одно сердечко
    }
}