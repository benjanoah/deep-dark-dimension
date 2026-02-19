package com.benjamin.deepdark;

import com.benjamin.deepdark.item.InvertGlassesItem;
import com.benjamin.deepdark.item.SculkShardItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
	// Sculk Shard - voor het activeren van de portal
	public static final Item SCULK_SHARD = registerItem("sculk_shard",
			new SculkShardItem(new FabricItemSettings().maxCount(64)));

	// Invert Glasses - zet je op als helm om alle kleuren te inverteren
	public static final InvertGlassesItem INVERT_GLASSES = (InvertGlassesItem) registerItem(
			"invert_glasses", new InvertGlassesItem());

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(DeepDarkMod.MOD_ID, name), item);
	}

	public static void register() {
		DeepDarkMod.LOGGER.info("Registering items for " + DeepDarkMod.MOD_ID);
		
		// Voeg sculk shard toe aan creative inventory
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
			content.add(SCULK_SHARD);
		});

		// Voeg invert glasses toe aan creative inventory (combat tab)
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
			content.add(INVERT_GLASSES);
		});
	}
}
