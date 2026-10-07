package com.f2d.blinkplus;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlinkDaggerItem extends Item {

    private static final int MAX_LIFT = 5;
    private static final int COOLDOWN_TICKS = 160;
    private static final int DAMAGE_LOCK_TICKS = 60;
    private static final int MAX_TELEPORT_Y = 310;

    private static final Map<UUID, Long> LAST_DAMAGE_TIME = new HashMap<>();
    private static final Map<UUID, Long> LAST_GLOBAL_USE = new HashMap<>();

    public BlinkDaggerItem(Properties properties) {
        super(properties);
    }

    public double getMaxDistance() {
        return 8.0;
    }

    public SoundEvent getTeleportSound() {
        return ModSounds.BLINK_DAGGER;
    }

    public void onTeleport(Player player) {
    }

    public static void onPlayerDamaged(Player player) {
        LAST_DAMAGE_TIME.put(player.getUUID(), player.level().getGameTime());
    }

    private static boolean isDamageLocked(Player player) {
        Long lastDamage = LAST_DAMAGE_TIME.get(player.getUUID());
        if (lastDamage == null) return false;
        long timeSinceDamage = player.level().getGameTime() - lastDamage;
        return timeSinceDamage < DAMAGE_LOCK_TICKS;
    }

    private static boolean isGlobalCooldown(Player player) {
        Long lastUse = LAST_GLOBAL_USE.get(player.getUUID());
        if (lastUse == null) return false;
        long timeSinceUse = player.level().getGameTime() - lastUse;
        return timeSinceUse < COOLDOWN_TICKS;
    }

    public BlockPos findSafeTeleportPos(Player player, Level level) {
        Vec3 look = player.getLookAngle();
        double maxDistance = getMaxDistance();

        for (double distance = maxDistance; distance >= 1.0; distance -= 1.0) {
            double targetX = player.getX() + look.x * distance;
            double targetY = player.getY() + look.y * distance;
            double targetZ = player.getZ() + look.z * distance;

            if (targetY > MAX_TELEPORT_Y) continue;

            double safeY = targetY;

            for (int i = 0; i < MAX_LIFT; i++) {
                if (safeY > MAX_TELEPORT_Y) break;

                BlockPos checkPos = BlockPos.containing(targetX, safeY, targetZ);

                boolean feetFree = level.getBlockState(checkPos)
                        .getCollisionShape(level, checkPos).isEmpty();

                boolean headFree = level.getBlockState(checkPos.above())
                        .getCollisionShape(level, checkPos.above()).isEmpty();

                boolean isFree = feetFree && headFree;

                boolean hasGround = !level.getBlockState(checkPos.below())
                        .getCollisionShape(level, checkPos.below()).isEmpty();

                if (isFree && hasGround) return checkPos;

                safeY += 1.0;
            }
        }

        return null;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(hand);

        // Общий кулдаун — нельзя использовать любой блинк
        if (isGlobalCooldown(player)) {
            player.getCooldowns().addCooldown(stack, 20);
            return InteractionResult.FAIL;
        }

        // Блокировка после урона
        if (isDamageLocked(player)) {
            player.getCooldowns().addCooldown(stack, 20);
            return InteractionResult.FAIL;
        }

        BlockPos fromPos = BlockPos.containing(player.getX(), player.getY(), player.getZ());

        BlockPos safePos = findSafeTeleportPos(player, level);
        if (safePos == null) return InteractionResult.PASS;

        player.teleportTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);

        level.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                getTeleportSound(),
                SoundSource.PLAYERS,
                1.0f,
                0.8f + player.getRandom().nextFloat() * 0.4f
        );

        onTeleport(player);

        if (player instanceof ServerPlayer serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, new BlinkPayload(fromPos, safePos));
        }

        // Записываем в общий трекер
        LAST_GLOBAL_USE.put(player.getUUID(), player.level().getGameTime());

        // Визуальный кулдаун на стак
        player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);

        return InteractionResult.SUCCESS;
    }
}