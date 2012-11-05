package com.comphenix.blockpatcher.lookup;

/**
 * Represents a set of indexed lookup tables for a specific chunk.
 * 
 * @author Kristian
 */
public interface SegmentLookup extends ConversionLookup {
	
	/**
	 * Default number of segments in Minecraft.
	 */
	public static final int MINECRAFT_SEGMENT_COUNT = 16;
	
	/**
	 * Retrieve the lookup table for a specific segment.
	 * @param chunkY - y position of the specified segment.
	 * @return The lookup table.
	 */
	public ConversionLookup getSegmentView(int chunkY);
	
	/**
	 * Retrieve the number of segments in this lookup.
	 * @return Number of segments.
	 */
	public int getSegmentCount();
}
