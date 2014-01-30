package com.comphenix.blockpatcher;

import java.util.logging.Logger;

import org.bukkit.Material;

import com.google.common.collect.ImmutableMap;

class BlockHeight {
	private final ImmutableMap<Material, Double> lookup;
	private static final int NONSTANDARD_BLOCKS = 21;
	
	public BlockHeight(Logger logger) {
		ImmutableMap.Builder<Material, Double> builder = ImmutableMap.builder();
		
		// Ugly, but it allows us to stay backwards compatible
		for (int i = 0; i < NONSTANDARD_BLOCKS; i++) {
			try {
				addBlock(builder, i);
			} catch (Throwable e) {
				// Print a minimal version of the errro
				logger.severe(e.getMessage());
			}
		}
		this.lookup = builder.build();
	}

	// On Error Resume Next
	private void addBlock(ImmutableMap.Builder<Material, Double> builder, int index) {
		switch (index) {
			case  0: builder.put(Material.FENCE,                   1.5); break;
			case  1: builder.put(Material.COBBLE_WALL,             1.5); break;
			case  2: builder.put(Material.SOUL_SAND,               7 / 8.0); break;
			case  3: builder.put(Material.CHEST,                   7 / 8.0); break;
			case  4: builder.put(Material.BREWING_STAND,           7 / 8.0); break;
			case  5: builder.put(Material.ENDER_PORTAL_FRAME,      13 / 16.0); break;
			case  6: builder.put(Material.ENCHANTMENT_TABLE,       3 / 4.0); break;
			case  7: builder.put(Material.COCOA,                   3 / 4.0); break;
			case  8: builder.put(Material.BED,                     9 / 16.0); break;
			case  9: builder.put(Material.STEP,                    1 / 2.0); break;
			case 10: builder.put(Material.WOOD_STEP,               1 / 2.0); break;
			case 11: builder.put(Material.CAKE_BLOCK,              7 / 16.0); break;
			case 12: builder.put(Material.DAYLIGHT_DETECTOR,       3 / 8.0); break;
			case 13: builder.put(Material.FLOWER_POT,              3 / 8.0); break;
			case 14: builder.put(Material.REDSTONE_COMPARATOR,     1 / 8.0); break;
			case 15: builder.put(Material.REDSTONE_COMPARATOR_OFF, 1 / 8.0); break;
			case 16: builder.put(Material.REDSTONE_COMPARATOR_ON,  1 / 8.0); break;
			case 17: builder.put(Material.DIODE_BLOCK_OFF,         1 / 8.0); break;
			case 18: builder.put(Material.DIODE_BLOCK_ON,          1 / 8.0); break;
			case 19: builder.put(Material.TRAP_DOOR,               1 / 8.0); break;
			case 20: builder.put(Material.SNOW,                    1 / 8.0); break;
		}
	}
	
	/**
	 * Retrieve the height of a specific material.
	 * @param material - the material.
	 * @return The height of this block.
	 */
	public double getHeight(Material material) {
		Double value = lookup.get(material);
		return value != null ? value : 1.0;
	}
}
