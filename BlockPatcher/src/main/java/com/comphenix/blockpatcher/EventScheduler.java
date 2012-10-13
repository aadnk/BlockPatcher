package com.comphenix.blockpatcher;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import com.comphenix.blockpatcher.events.ItemConvertingEvent;

class EventScheduler {
	
	private PluginManager manager;
	
	public EventScheduler(PluginManager manager) {
		super();
		this.manager = manager;
	}

	public void computeItemConversion(ItemStack[] stacks, Player player, boolean fromInventory) {
		ItemConvertingEvent event = new ItemConvertingEvent(stacks, player, fromInventory);
		manager.callEvent(event);
	}	
}
