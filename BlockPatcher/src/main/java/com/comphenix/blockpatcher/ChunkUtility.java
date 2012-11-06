package com.comphenix.blockpatcher;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;

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
		EntityPlayer entity = ((CraftPlayer) player).getHandle();
		entity.chunkCoordIntPairQueue.add(new ChunkCoordIntPair(chunkX, chunkZ));
	}
}
