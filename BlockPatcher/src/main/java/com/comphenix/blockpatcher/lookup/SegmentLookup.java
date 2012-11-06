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
	 * @param chunkY - the y position of the specified segment.
	 * @return The lookup table.
	 */
	public ConversionLookup getSegmentView(int chunkY);

	/**
	 * Convert the given block type into another block type.
	 * @param blockID - the given block type.
	 * @param newBlockID - the new block type.
	 * @param chunkY - the y position of the specified segment.
	 */
	public abstract void setBlockLookup(int blockID, int newBlockID, int chunkY);
	
	/**
	 * Retrieve the block ID the given block ID will be converted to.
	 * @param blockID - the old block ID.
	 * @return The new block ID after chunk processing.
	 * @param chunkY - the y position of the specified segment.
	 */
	public abstract int getBlockLookup(int blockID, int chunkY);
	
	/**
	 * Converts data values of the given value into the new value.
	 * @param blockID - block to match.
	 * @param originalDataValue - data value to match.
	 * @param newDataValue - replaced data value.
	 * @param chunkY - the y position of the specified segment.
	 */
	public abstract void setDataLookup(int blockID, int originalDataValue, int newDataValue, int chunkY);

	/**
	 * Retrieve the converted data value given the old block ID and data value. 
	 * @param blockID - the old block ID.
	 * @param dataValue - the old data value.
	 * @param chunkY - the y position of the specified segment.
	 * @return The new data value after chunk processing.
	 */
	public abstract int getDataLookup(int blockID, int dataValue, int chunkY);
	
	/**
	 * Retrieve the number of segments in this lookup.
	 * @return Number of segments.
	 */
	public int getSegmentCount();
}
