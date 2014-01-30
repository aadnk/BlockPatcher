/*
 *  BlockAPI - Safely convert one block ID to another for the client only 
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

import org.bukkit.plugin.java.JavaPlugin;

public class PatcherMod extends JavaPlugin {

	private EventScheduler scheduler;
	private Calculations calculations;
	private PacketListeners listeners;
	
	private static ConversionCache cache;
	private static PatcherAPI api;
	
	private BlockMismatchFixer fixer;
	
	/**
	 * Retrieve an instance of the API.
	 * @return Patcher API.
	 */
	public static PatcherAPI getAPI() {
		return api;
	}

	/**
	 * Retrieve the current cache of lookup tables by chunks and players.
	 * @return Every cached lookup table.
	 */
	public static ConversionCache getCache() {
		return cache;
	}
	
	@Override
	public void onLoad() {
		api = new PatcherAPI();
		cache = new ConversionCache(api);
	}
	
	@Override
	public void onEnable() {
		scheduler = new EventScheduler(getServer().getPluginManager());
		calculations = new Calculations(cache, scheduler);
		listeners = new PacketListeners(this, scheduler);
		listeners.registerEvents(calculations);
		fixer = new BlockMismatchFixer(this, cache);
	}
	
	@Override
	public void onDisable() {
		fixer.close();
	}
}
