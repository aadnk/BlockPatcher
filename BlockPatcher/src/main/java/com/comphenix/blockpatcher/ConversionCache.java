package com.comphenix.blockpatcher;

import java.util.concurrent.ConcurrentMap;

import org.bukkit.entity.Player;

import com.comphenix.blockpatcher.lookup.SegmentLookup;
import com.comphenix.blockpatcher.lookup.SphericalBuffer;
import com.google.common.collect.MapMaker;

public class ConversionCache {

	/**
	 * The radius of cached chunk conversions around a player.
	 * <p>
	 * This will only break in case someone uses more than "extreme" render distance in
	 * multiplayer, which is currently not possible.
	 */
	private int BUFFER_RADIUS = 32;
	
	// Cached conversions by player and chunk. Note that we don't actually store the real chunk location.
	private ConcurrentMap<Player, SphericalBuffer<SegmentLookup>> playerConversions;
	
	// Default conversion lookup table
	private SegmentLookup defaultLookupTable;
	
	public ConversionCache(SegmentLookup defaultLookupTable) {
		if (defaultLookupTable == null)
			throw new IllegalArgumentException("Supplied lookup table cannot be NULL.");
		
		// Initialize the cache
		this.defaultLookupTable = defaultLookupTable;
		this.playerConversions = new MapMaker().
				concurrencyLevel(2).
				weakKeys().
				makeMap();
	}
	
	/**
	 * Retrieve the default lookup table. 
	 * <p>
	 * Use this in case the cache is null.
	 * 
	 * @return Default lookup table.
	 */
	public SegmentLookup getDefaultLookupTable() {
		return defaultLookupTable;
	}

	/**
	 * Cache the conversion lookup table used at a given chunk for a given player.
	 * @param player - the player.
	 * @param chunkX - chunk x position.
	 * @param chunkZ - chunk z position.
	 * @param lookupTable - the lookup table used.
	 */
	public void saveCache(Player player, int chunkX, int chunkZ, SegmentLookup lookupTable) {
		SphericalBuffer<SegmentLookup> cache = playerConversions.get(player);
		SphericalBuffer<SegmentLookup> inserted = null;
		
		// Cheap and thread safe
		if (cache == null) {
			cache = new SphericalBuffer<SegmentLookup>(BUFFER_RADIUS * 2, BUFFER_RADIUS * 2);
			inserted = playerConversions.putIfAbsent(player, cache);
			
			if (inserted != null)
				cache = inserted;
		}
		
		// Next, store the chunk conversion
		cache.set(chunkX, chunkZ, lookupTable);
	}
	
	/**
	 * Retrieve the conversion lookup table at a given chunk for a given player.
	 * @param player - the player.
	 * @param chunkX - chunk x position.
	 * @param chunkZ - chunk y position.
	 * @return A lookup table, or NULL if no lookup table was found.
	 */
	public SegmentLookup loadCache(Player player, int chunkX, int chunkZ) {
		SphericalBuffer<SegmentLookup> cache = playerConversions.get(player);
		
		if (cache != null) {
			return cache.get(chunkX, chunkZ);
		} else {
			return null;
		}
	}
	
	/**
	 * Retrieve the conversion lookup table at a given chunk for a player, or the default table if not found.
	 * @param player - the player.
	 * @param chunkX - chunk x position.
	 * @param chunkZ - chunk y position.
	 * @return A lookup table, or the default lookup table if not found.
	 */
	public SegmentLookup loadCacheOrDefault(Player player, int chunkX, int chunkZ) {
		SegmentLookup lookup = loadCache(player, chunkX, chunkZ);
		
		if (lookup != null) {
			return lookup;
		} else {
			return defaultLookupTable;
		}
	}
}
