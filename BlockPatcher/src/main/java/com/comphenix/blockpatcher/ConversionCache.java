package com.comphenix.blockpatcher;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.bukkit.entity.Player;

import com.comphenix.blockpatcher.lookup.ConversionLookup;
import com.comphenix.blockpatcher.lookup.SegmentLookup;
import com.comphenix.blockpatcher.lookup.SphericalBuffer;
import com.google.common.collect.MapMaker;

public class ConversionCache {

	/**
	 * The radius of cached chunk conversions around a player.
	 * <p>
	 * This will only break in case someone uses more than "far" render distance in
	 * multiplayer, which is currently not possible.
	 */
	private int BUFFER_RADIUS = 16;
	
	// Cached conversions by player and chunk. Note that we don't actually store the real chunk location.
	private ConcurrentMap<Player, SphericalBuffer<SegmentLookup>> playerConversions;
	
	// Prevent duplicate conversion lookups from being added
	private Map<SegmentLookup, WeakReference<SegmentLookup>> conversionCache;
	
	// Reading and writing to the conversion cache
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
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
		
		// Duplicate cache
		this.conversionCache = new WeakHashMap<SegmentLookup, WeakReference<SegmentLookup>>();
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
		
		if (lookupTable != null) {
			lookupTable = getCachedConversion(lookupTable);
		}
		
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
	
	private SegmentLookup getCachedConversion(SegmentLookup lookup) {
		WeakReference<SegmentLookup> previous = null;
		
		// Prevent duplicates
		try {
			lock.readLock().lock();
			previous = conversionCache.get(lookup);
		} finally {
			lock.readLock().unlock();
		}
		
		// Use the previous value, instead of adding a new
		if (previous != null && previous.get() != null) {
			return previous.get();
		} else {
			try {
				lock.writeLock().lock();
				conversionCache.put(lookup, new WeakReference<SegmentLookup>(lookup));
			} finally {
				lock.writeLock().unlock();
			}
			
			// Return the same conversion table
			return lookup;
		}
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
	
	/**
	 * Retrieve the conversion lookup table at a given chunk for a player, or the default table if not found.
	 * @param player - the player.
	 * @param chunkX - chunk x position.
	 * @param chunkY - chunk y position.
	 * @param chunkZ - chunk y position.
	 * @return A lookup table, or the default lookup table if not found.
	 */
	public ConversionLookup loadCacheOrDefault(Player player, int chunkX, int chunkY, int chunkZ) {
		return loadCacheOrDefault(player, chunkX, chunkZ).getSegmentView(chunkY);
	}
}
