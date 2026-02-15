package com.benjamin.deepdark;

import com.benjamin.deepdark.block.DeepDarkPortalBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
	// Deep Dark Portal block
	public static final Block DEEP_DARK_PORTAL = registerBlock("deep_dark_portal",
		new DeepDarkPortalBlock(FabricBlockSettings.of(Material.PORTAL, MapColor.CYAN)
			.noCollision()
			.strength(-1.0F)
			.sounds(BlockSoundGroup.GLASS)
			.luminance(state -> 11)));

	private static Block registerBlock(String name, Block block) {
		return Registry.register(Registries.BLOCK, new Identifier(DeepDarkMod.MOD_ID, name), block);
	}

	public static void register() {
		DeepDarkMod.LOGGER.info("Registering blocks for " + DeepDarkMod.MOD_ID);
	}
}
