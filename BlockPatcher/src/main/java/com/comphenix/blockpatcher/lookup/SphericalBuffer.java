package com.comphenix.blockpatcher.lookup;

/**
 * The 2D analogy of a circular buffer. 
 * <p>
 * This connects the side of a 2D buffer together.
 * 
 * @author Kristian
 *
 */
@SuppressWarnings("unchecked")
public class SphericalBuffer<TValue> {
	// The rectangular buffer we'll use
	private final Object[][] buffer;
	private final int width;
	private final int height;
	
	/**
	 * Initialize a spherical buffer with a fixed size.
	 * @param width - fixed width.
	 * @param height - fixed height.
	 */
	public SphericalBuffer(int width, int height) {
		this.buffer = new Object[width][height];
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Set the value of a cell in the spherical buffer.
	 * @param x - x position.
	 * @param y - y position.
	 * @param value - new cell value.
	 */
	public void set(int x, int y, TValue value) {
		 buffer[mod(x, width)][mod(y, height)] = value;
	}
	
	/**
	 * Retrieve the value of a cell in the spherical buffer.
	 * @param x - x position.
	 * @param y - y position.
	 * @return Value of the given cell, or NULL if it hasn't been set yet.
	 */
	public TValue get(int x, int y) {
		return (TValue) buffer[mod(x, width)][mod(y, height)];
	}
	
	/**
	 * Retrieve the fixed width of this buffer, after which cells begins to repeat.
	 * @return The fixed width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Retrieve the fixed height of this buffer, after which cells begins to repeat.
	 * @return The fixed height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Calculate the positive result of the given congruence relation.
	 * @param x - the first value
	 * @param y - the second value.
	 * @return The positive result.
	 */
	private int mod(int x, int y) {
	    int result = x % y;
	    
	    // Handle negative numbers
	    if (result < 0) 
	        return result + y;
	    else 
	    	return result;
	}
}
