package com.f2d.blinkplus;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class ModItems {

    public static final Item BLINK_DAGGER =
            register("blink_dagger", BlinkDaggerItem::new);

    public static final Item SWIFT_BLINK =
            register("swift_blink", SwiftBlinkItem::new);

    public static final Item OVERWHELMING_BLINK =
            register("overwhelming_blink", OverwhelmingBlinkItem::new);

    public static final Item ARCANE_BLINK =
            register("arcane_blink", ArcaneBlinkItem::new);

    private static Item register(String name, Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Blink.id(name));
        Item.Properties properties = new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .setId(key);
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties));
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> {
            output.insertAfter(Items.ELYTRA, BLINK_DAGGER);
            output.insertAfter(BLINK_DAGGER, SWIFT_BLINK);
            output.insertAfter(SWIFT_BLINK, OVERWHELMING_BLINK);
            output.insertAfter(OVERWHELMING_BLINK, ARCANE_BLINK);
        });
    }
}
