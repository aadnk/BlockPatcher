package com.comphenix.blockpatcher.lookup;

import com.google.common.base.Objects;

/**
 * Represents a lookup that uses a read-only lookup as a basis for all of its operations.
 * <p>
 * When a caller attempt to modify the data, the underlying lookup table is cloned. Subsequent
 * modifications are performed on this cloned lookup table. 
 * 
 * @author Kristian
 */
public class LazyCopyLookup implements ConversionLookup {

	/**
	 * The underlying lookup table. Will be replaced by a clone after the first modification.
	 */
	protected ConversionLookup delegate;
	
	/**
	 * Whether or not the lookup table has been changed since the lazy lookup was created. 
	 */
	protected boolean modified;
	
	/**
	 * Construct a lazy copy from a given lookup.
	 * @param lookupTable - the lookup to copy.
	 */
	public LazyCopyLookup(ConversionLookup lookupTable) {
		setLookupTable(lookupTable);
		this.modified = false;
	}
	
	/**
	 * Change the underlying lookup table.
	 * <p>
	 * The given lookup table will never be modified by other consumers. It will 
	 * automatically be cloned if necessary.
	 * 
	 * @param defaultLookupTable - new underlying lookup table.
	 */
	public void setLookupTable(ConversionLookup lookupTable) {
		// Handle delegated lookup tables
		if (lookupTable instanceof LazyCopyLookup) {
			this.delegate = ((LazyCopyLookup) lookupTable).delegate;
			return;
		} 
		// Avoid circular lookup tables
		if (lookupTable == this) 
			throw new IllegalArgumentException("Cannot set lookup table to itself. Circular references are prohibited.");
		if (lookupTable == null)
			throw new IllegalArgumentException("The lookup table cannot be NULL. Use an identity table instead.");
		
		this.delegate = lookupTable;
	}
	
	@Override
	public byte[] getBlockLookup() {
		return delegate.getBlockLookup();
	}
	
	@Override
	public byte[] getDataLookup() {
		return delegate.getDataLookup();
	}
	
	@Override
	public int getBlockLookup(int blockID) {
		return delegate.getBlockLookup(blockID);
	}

	@Override
	public void setBlockLookup(int blockID, int newBlockID) {
		checkModifications();
		delegate.setBlockLookup(blockID, newBlockID);
	}
	
	@Override
	public int getDataLookup(int blockID, int dataValue) {
		return delegate.getDataLookup(blockID, dataValue);
	}
	
	@Override
	public void setDataLookup(int blockID, int originalDataValue, int newDataValue) {
		checkModifications();
		delegate.setDataLookup(blockID, originalDataValue, newDataValue);
	}

	@Override
	public boolean equals(Object other) {
	    if(other == this) return true;
	    if(other == null) return false;
	    
	    // Compare the delegate instead of the actual lazy instance
	    if (other instanceof LazyCopyLookup) {
	    	final LazyCopyLookup lazyCopy = (LazyCopyLookup) other;
	    	return Objects.equal(delegate, lazyCopy.delegate);
	    } else {
	    	// Compare against the delegate instead
	    	return Objects.equal(delegate, other);
	    }
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(delegate);
	}
	
	/**
	 * Whether or not the lookup table has been modified since it was last initialized.
	 * @return TRUE if it has, FALSE otherwise.
	 */
	public boolean isModified() {
		return modified;
	}
	
	/**
	 * Called before any data is modified.
	 */
	protected void checkModifications() {
		if (!modified) {
			modified = true;
			delegate = delegate.deepClone();
		}
	}

	@Override
	public ConversionLookup deepClone() {
		return new LazyCopyLookup(this);
	}
}
