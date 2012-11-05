package com.comphenix.blockpatcher.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.comphenix.blockpatcher.lookup.SegmentLookup;

/**
 * Invoked right before a chunk is sent to a player.
 * <p>
 * The resulting ConversionLookup will be used for all future block changes within this chunk
 * until the next chunk processing event.
 * 
 * @author Kristian
 */
public class ChunkPostProcessingEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private int chunkX;
    private int chunkZ;
    private SegmentLookup lookup;
    
    public ChunkPostProcessingEvent(Player player, int chunkX, int chunkZ, SegmentLookup lookup) {
		this.player = player;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		
		// This deep copy uses lazy copies underneath, so it's fairly quick
		this.lookup = (SegmentLookup) lookup.deepClone();
	}

    /**
     * Retrieve the player that will receive this chunk.
     * @return The receiver.
     */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Retrieve the X position of the transmitted chunk.
	 * @return The transmitted chunk.
	 */
    public int getChunkX() {
		return chunkX;
	}
    
	/**
	 * Retrieve the Z position of the transmitted chunk.
	 * @return The transmitted chunk.
	 */
    public int getChunkZ() {
		return chunkZ;
	}

    /**
     * Retrieve the lookup table that will be used to modify the current chunk.
     * <p>
	 * All future block changes will use this conversion until the same chunk is transmitted again.
     * @return The lookup table used.
     */
    public SegmentLookup getLookup() {
		return lookup;
	}

    /**
     * Set the lookup table that will be used to modify the current chunk.
     * <p>
     * Note: Most callers should modify the existing lookup table, allowing multiple plugins to work together.
     * @param lookup - the new lookup table.
     */
    public void setLookup(SegmentLookup lookup) {
		this.lookup = lookup;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	/**
	* This is a Bukkit method.
	* @return Registered handlers to Bukkit
	*/
    public static HandlerList getHandlerList() {
        return ChunkPostProcessingEvent.handlers;
    }
}
