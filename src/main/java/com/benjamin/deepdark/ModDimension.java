package com.benjamin.deepdark;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ModDimension {
	// Registry keys voor onze dimensie
	public static final RegistryKey<World> DEEP_DARK_WORLD = RegistryKey.of(
			RegistryKeys.WORLD,
			new Identifier(DeepDarkMod.MOD_ID, "deep_dark")
	);
	
	public static final RegistryKey<DimensionType> DEEP_DARK_TYPE = RegistryKey.of(
			RegistryKeys.DIMENSION_TYPE,
			new Identifier(DeepDarkMod.MOD_ID, "deep_dark_type")
	);

	public static void register() {
		DeepDarkMod.LOGGER.info("Registering dimension for " + DeepDarkMod.MOD_ID);
		// Dimensie wordt via datapack JSON bestanden geregistreerd
	}
}
