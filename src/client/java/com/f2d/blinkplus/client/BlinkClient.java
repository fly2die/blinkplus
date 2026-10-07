package com.f2d.blinkplus.client;

import com.f2d.blinkplus.BlinkPayload;
import com.f2d.blinkplus.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.core.BlockPos;

public class BlinkClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// Превью (рамка)
		ParticleProviderRegistry.getInstance().register(
				ModParticles.BLINK_PART_TP,
				FlameParticle.Provider::new
		);

		// Частица телепорта — своя текстура
		ParticleProviderRegistry.getInstance().register(
				ModParticles.BLINK_PART_BLINK,
				FlameParticle.Provider::new
		);

		ClientPlayNetworking.registerGlobalReceiver(BlinkPayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				if (context.client().level == null) return;

				if (payload.from().equals(payload.to())) {
					drawPreviewFrame(context, payload.to());
				} else {
					spawnBlinkBurst(context, payload.from());
					spawnBlinkBurst(context, payload.to());
				}
			});
		});
	}

	private static void drawPreviewFrame(
			net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context context,
			BlockPos pos) {
		double centerX = pos.getX() + 0.5;
		double centerY = pos.getY() + 0.1;
		double centerZ = pos.getZ() + 0.5;

		double[][][] edges = {
				{{-0.5, -0.5}, {0.5, -0.5}},
				{{0.5, -0.5}, {0.5, 0.5}},
				{{0.5, 0.5}, {-0.5, 0.5}},
				{{-0.5, 0.5}, {-0.5, -0.5}}
		};

		int steps = 5;

		for (double[][] edge : edges) {
			double startX = edge[0][0];
			double startZ = edge[0][1];
			double endX = edge[1][0];
			double endZ = edge[1][1];

			for (int i = 0; i <= steps; i++) {
				double t = (double) i / steps;
				double offsetX = startX + (endX - startX) * t;
				double offsetZ = startZ + (endZ - startZ) * t;

				context.client().level.addParticle(
						ModParticles.BLINK_PART_TP,
						centerX + offsetX,
						centerY,
						centerZ + offsetZ,
						0.0, 0.0, 0.0
				);
			}
		}
	}

	private static void spawnBlinkBurst(
			net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context context,
			BlockPos pos) {
		double centerX = pos.getX() + 0.5;
		double centerY = pos.getY() + 0.5;
		double centerZ = pos.getZ() + 0.5;

		for (int i = 0; i < 25; i++) {
			context.client().level.addParticle(
					ModParticles.BLINK_PART_BLINK,
					centerX + (Math.random() - 0.5) * 1.2,
					centerY + (Math.random() - 0.5) * 1.2,
					centerZ + (Math.random() - 0.5) * 1.2,
					0.0, 0.0, 0.0
			);
		}
	}
}