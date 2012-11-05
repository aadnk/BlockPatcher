package com.comphenix.blockpatcher.lookup;

/**
 * Represents an object that maps block IDs and associated data values to some corresponding values.
 * 
 * @author Kristian
 */
public interface ConversionLookup {

	/**
	 * Retrieve a lookup table for blocks. 
	 * <p>
	 * Note that the resulting table is only intended for fast read-access. 
	 * It should never be modified.
	 * @return Lookup table for blocks.
	 */
	public abstract byte[] getDataLookup();

	/**
	 * Retrieve a lookup table for the data field. 
	 * <p>
	 * Every block have 16 entries, one for each possible data value.
	 * <p>
	 * Note that the resulting table is only intended for fast read-access. 
	 * It should never be modified.
	 * @return Lookup table.
	 */
	public abstract byte[] getBlockLookup();
	
	/**
	 * Convert the given block type into another block type.
	 * @param blockID - the given block type.
	 * @param newBlockID - the new block type.
	 */
	public abstract void setBlockLookup(int blockID, int newBlockID);
	
	/**
	 * Retrieve the block ID the given block ID will be converted to.
	 * @param blockID - the old block ID.
	 * @return The new block ID after chunk processing.
	 */
	public abstract int getBlockLookup(int blockID);
	
	/**
	 * Converts data values of the given value into the new value.
	 * @param blockID - block to match.
	 * @param originalDataValue - data value to match.
	 * @param newDataValue - replaced data value.
	 */
	public abstract void setDataLookup(int blockID, int originalDataValue, int newDataValue);

	/**
	 * Retrieve the converted data value given the old block ID and data value. 
	 * @param blockID - the old block ID.
	 * @param dataValue - the old data value.
	 * @return The new data value after chunk processing.
	 */
	public abstract int getDataLookup(int blockID, int dataValue);
	
	/**
	 * Create a deep clone of the current conversion lookup table.
	 * @return A deep copy of the current lookup table.
	 */
	public ConversionLookup deepClone();
}