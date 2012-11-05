package com.comphenix.blockpatcher.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Invoked before an item is sent to a client.
 * 
 * @author Kristian
 */
public class ItemConvertingEvent extends Event {
	
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private ItemStack[] itemStacks;
    private boolean fromInventory;

	public ItemConvertingEvent(ItemStack[] itemStacks, Player player, boolean fromInventory) {
		super();
		this.player = player;
		this.itemStacks = itemStacks;
		this.fromInventory = fromInventory;
	}

	/**
	 * The player that will recieve this conversion.
	 * @return The involved player.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * The item stacks to be converted.
	 * <p>
	 * Note that they're not always derived from CraftItemStack.
	 * @return Item stacks to convert.
	 */
	public ItemStack[] getItemStacks() {
		return itemStacks;
	}

	/**
	 * Whether or not this event originated from an inventory.
	 * @return TRUE if this event was invoked due to an inventory action, FALSE otherwise.
	 */
	public boolean isFromInventory() {
		return fromInventory;
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
        return ItemConvertingEvent.handlers;
    }
}
