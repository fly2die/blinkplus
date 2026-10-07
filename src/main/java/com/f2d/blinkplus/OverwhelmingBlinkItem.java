package com.f2d.blinkplus;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class OverwhelmingBlinkItem extends BlinkDaggerItem {

    private static final double DAMAGE_RADIUS = 2.0;
    private static final float DAMAGE_AMOUNT = 8.0f; // 4 сердечка

    public OverwhelmingBlinkItem(Properties properties) {
        super(properties);
    }

    @Override
    public SoundEvent getTeleportSound() {
        return ModSounds.OVERWHELMING_BLINK;
    }

    @Override
    public void onTeleport(Player player) {
        Level level = player.level();

        // Создаём область вокруг игрока (радиус 2 блока)
        AABB area = player.getBoundingBox().inflate(DAMAGE_RADIUS);

        // Наносим урон всем живым существам в области
        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class,
                area,
                entity -> entity != player
        );

        for (LivingEntity entity : entities) {
            entity.hurt(level.damageSources().playerAttack(player), DAMAGE_AMOUNT);
        }

        // Огненные частицы в радиусе
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.FLAME,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    50,                    // количество частиц
                    DAMAGE_RADIUS,          // разброс по X
                    DAMAGE_RADIUS,          // разброс по Y
                    DAMAGE_RADIUS,          // разброс по Z
                    0.05                   // скорость
            );
        }
    }
}