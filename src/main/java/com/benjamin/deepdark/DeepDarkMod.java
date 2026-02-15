package com.benjamin.deepdark;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeepDarkMod implements ModInitializer {
	public static final String MOD_ID = "deepdark";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Deep Dark Dimension wordt geladen... Wardens wakker maken! 👁️");
		
		// Hier registreren we straks alles
		ModItems.register();
		ModBlocks.register();
		ModDimension.register();
	}
}
