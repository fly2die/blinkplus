package com.f2d.blinkplus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Blink implements ModInitializer {
	public static final String MOD_ID = "blinkplus";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Как часто обновляется превью цели, в тиках (20 тиков = 1 секунда). */
	private static final int PREVIEW_INTERVAL_TICKS = 4;

	@Override
	public void onInitialize() {
		LOGGER.info("Blink+ loaded");

		ModItems.initialize();
		ModParticles.initialize();
		ModSounds.initialize();

		// Пакет для отрисовки частиц блинка на клиенте
		PayloadTypeRegistry.clientboundPlay().register(BlinkPayload.ID, BlinkPayload.CODEC);

		// Блокировка блинка после полученного урона
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
			if (entity instanceof Player player) {
				BlinkDaggerItem.onPlayerDamaged(player);
			}
		});

		// Иначе статические карты копят записи об игроках, которые давно вышли
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
				BlinkDaggerItem.clearPlayer(handler.getPlayer().getUUID()));

		ServerTickEvents.END_SERVER_TICK.register(Blink::tickPreview);
	}

	/**
	 * Показывает игроку, куда его перенесёт блинк в руке.
	 * Отправляем не каждый тик, иначе на каждого игрока уходит 20 пакетов в секунду.
	 */
	private static void tickPreview(MinecraftServer server) {
		if (server.getTickCount() % PREVIEW_INTERVAL_TICKS != 0) return;

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			ItemStack mainHand = player.getMainHandItem();

			if (!(mainHand.getItem() instanceof BlinkDaggerItem blinkItem)) continue;
			if (player.getCooldowns().isOnCooldown(mainHand)) continue;

			BlockPos target = blinkItem.findSafeTeleportPos(player, player.level());
			if (target != null) {
				// Две одинаковые точки = превью цели, а не сам телепорт
				ServerPlayNetworking.send(player, new BlinkPayload(target, target));
			}
		}
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
