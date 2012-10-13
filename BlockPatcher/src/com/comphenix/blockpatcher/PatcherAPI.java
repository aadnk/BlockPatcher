/*
 *  BlockPatcher - Safely convert one block ID to another for the client only 
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package com.comphenix.blockpatcher;

import com.google.common.base.Preconditions;

/**
 * Able to automatically translate every block and item on the server to a different type. 
 * <p>
 * This conversion is only client side and will never affect the actual world files.
 * <p>
 * To convert items, subscribe to the event ItemConvertingEvent.
 * @author Kristian
 */
public class PatcherAPI {
	/**
	 * The translation table we'll be using.
	 */
	private byte[] blockLookup = getIdentityTranslation(256);
	
	/**
	 * The data value translation table we'll use.
	 */
	private byte[] dataLookup = getDataIdentity(256, 16);
	
	/**
	 * Retrieve the lookup table for blocks.
	 * @return Lookup table for blocks.
	 */
	public byte[] getBlockLookup() {
		return blockLookup;
	}

	/**
	 * Retrieve the lookup table for the data field.
	 * <p>
	 * Every block have 16 entries, one for each possible data value.
	 * @return Lookup table.
	 */
	public byte[] getDataLookup() {
		return dataLookup;
	}
	
	/**
	 * Convert the given block type into another block type.
	 * @param blockID - the given block type.
	 * @param newBlockID - the new block type.
	 */
	public void setBlockLookup(int blockID, int newBlockID) {
		Preconditions.checkPositionIndex(blockID, 256, "Block ID must be in the range 0 - 256");
		Preconditions.checkPositionIndex(newBlockID, 256, "New block ID must be in the range 0 - 256");

		blockLookup[blockID] = (byte) newBlockID;
	}
	
	/**
	 * Converts data values of the given value into the new value.
	 * @param blockID - block to match.
 	 * @param originalDataValue - data value to match.
	 * @param newDataValue - replaced data value.
	 */
	public void setDataLookup(int blockID, int originalDataValue, int newDataValue) {
		Preconditions.checkPositionIndex(blockID, 256, "Block ID must be in the range 0 - 256");
		Preconditions.checkPositionIndex(originalDataValue, 16, "Original data value must be in the range 0 - 15");
		Preconditions.checkPositionIndex(newDataValue, 16, "New data value must be in the range 0 - 15");
		
		dataLookup[(blockID << 4) + originalDataValue] = (byte) newDataValue;
	}
		
	/**
	 * Generate a translation table that doesn't change any value.
	 * @param max - the maximum number of entries in the table.
	 * @return The identity translation table.
	 */
	public byte[] getIdentityTranslation(int max) {
		byte[] values = new byte[max];
		
		for (int i = 0; i < values.length; i++) {
			values[i] = (byte) i;
		}
		return values;
	}
	
	/**
	 * Generate an identity translation table that doesn't change anything.
	 * @param blocks - number of blocks.
	 * @param entries - number of data values per block.
	 * @return The translation table.
	 */
	private byte[] getDataIdentity(int blocks, int entries) {
		byte[] values = new byte[blocks * entries];

		for (int i = 0; i < values.length; i++) {
			values[i] = (byte) (i % entries);
		}
		return values;
	}
}
