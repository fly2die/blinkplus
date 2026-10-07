package com.f2d.blinkplus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Blink implements ModInitializer {
	public static final String MOD_ID = "blinkplus";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ModItems.initialize();
		ModParticles.initialize();
		ModSounds.initialize();

		// Регистрируем пакет для частиц
		PayloadTypeRegistry.clientboundPlay().register(BlinkPayload.ID, BlinkPayload.CODEC);

		// Регистрируем событие урона (для блокировки блинка)
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
			if (entity instanceof Player player) {
				BlinkDaggerItem.onPlayerDamaged(player);
			}
		});

		// Регистрируем тикер сервера (для превью частиц)
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				ItemStack mainHand = player.getMainHandItem();

				// Проверяем, что в руке именно Блинк (любой из четырёх)
				if (mainHand.getItem() instanceof BlinkDaggerItem blinkItem) {

					// Проверяем, что предмет НЕ на кулдауне
					if (player.getCooldowns().isOnCooldown(mainHand)) {
						continue;
					}

					// Вызываем метод через экземпляр (нестатический)
					BlockPos safePos = blinkItem.findSafeTeleportPos(player, player.level());

					// Отправляем пакет с двумя одинаковыми точками (для превью)
					if (safePos != null) {
						ServerPlayNetworking.send(player, new BlinkPayload(safePos, safePos));
					}
				}
			}
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}