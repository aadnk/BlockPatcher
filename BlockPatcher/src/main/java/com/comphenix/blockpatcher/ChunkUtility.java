package com.comphenix.blockpatcher;

import java.util.List;

import org.bukkit.entity.Player;

import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.utility.MinecraftReflection;

/**
 * Performs simple operations on chunks.
 * 
 * @author Kristian
 */
@SuppressWarnings("unchecked")
class ChunkUtility {
	
	/**
	 * Re-transmit the given chunk to the given player.
	 * @param player - the given player.
	 * @param chunkX - the chunk x coordinate.
	 * @param chunkZ - the chunk z coordinate.
	 */
	public static void resendChunk(Player player, int chunkX, int chunkZ) {
		BukkitUnwrapper unwrapper = new BukkitUnwrapper();
		Object entityPlayer = unwrapper.unwrapItem(player);
		
		Class<?> chunkCoord = MinecraftReflection.getMinecraftClass("ChunkCoordIntPair");
		
		try {
			List<Object> list = (List<Object>) FuzzyReflection.fromObject(entityPlayer).
								getFieldByName("chunkCoordIntPairQueue").get(entityPlayer);
			
			// Add a chunk coord int pair
			list.add(chunkCoord.getConstructor(int.class, int.class).newInstance(chunkX, chunkZ));
			
		} catch (Exception e) {
			throw new RuntimeException("Cannot read chunk coord pair queue.", e);
		}
	}
}
