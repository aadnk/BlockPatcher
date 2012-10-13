package com.comphenix.blocktest;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.blockpatcher.PatcherAPI;
import com.comphenix.blockpatcher.PatcherMod;
import com.comphenix.blockpatcher.events.ItemConvertingEvent;

public class BlockTestMod extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		PatcherAPI api = PatcherMod.getAPI();
		
		// Testing API
		api.setBlockLookup(Material.SAND.getId(), Material.GLASS.getId());
		api.setBlockLookup(Material.GRASS.getId(), Material.WOOL.getId());
		
		for (int i = 0; i < 16; i++)
			api.setDataLookup(Material.GRASS.getId(), i, 5);
		
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onItemConverting(ItemConvertingEvent event) {
		for (ItemStack stack : event.getItemStacks()) {
			if (stack != null && stack.getTypeId() == Material.SAND.getId()) {
				stack.setTypeId(Material.GLASS.getId());
			}
		}
	}
}
