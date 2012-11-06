package com.comphenix.blocktest;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;

import com.comphenix.blockpatcher.PatcherAPI;
import com.comphenix.blockpatcher.PatcherMod;
import com.comphenix.blockpatcher.events.ChunkPostProcessingEvent;
import com.comphenix.blockpatcher.events.ItemConvertingEvent;
import com.comphenix.blockpatcher.lookup.ConversionLookup;

public class BlockTestMod extends JavaPlugin implements Listener {

	// We only use the X and Z coordinate for now
	private Map<Player, BlockVector> glassChunk = new WeakHashMap<Player, BlockVector>();
	
	// API reference
	private PatcherAPI api;
	
	@Override
	public void onEnable() {
		api = PatcherMod.getAPI();
		
		// Testing API
		//api.setBlockLookup(Material.SAND.getId(), Material.GLASS.getId());
		//api.setBlockLookup(Material.GRASS.getId(), Material.WOOL.getId());
		
		//for (int i = 0; i < 16; i++)
		//	api.setDataLookup(Material.GRASS.getId(), i, 5);
		
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		// See if the player has moved outside the chunk
		updateChunk(event.getPlayer());
	}
	
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		updateChunk(event.getPlayer());
	}
	
	private void updateChunk(Player player) {
		// See if the chunk coordinate changed in the X or Z direction
		BlockVector coord = getChunkCoordinate(player);
		BlockVector last = glassChunk.get(player);
		
		if (last == null || coord.getBlockX() != last.getBlockX() || coord.getBlockZ() != last.getBlockZ()) {
			// Update!
			api.resendChunk(player, coord.getBlockX(), coord.getBlockZ());
			
			if (last != null)
				api.resendChunk(player, last.getBlockX(), last.getBlockZ());
			glassChunk.put(player, coord);
		}
	}
	
	private BlockVector getChunkCoordinate(Player player) {
		Location loc = player.getLocation();
		return new BlockVector(loc.getBlockX() >> 4, loc.getBlockY() >> 4, loc.getBlockZ() >> 4);
	}
	
	@EventHandler
	public void onChunk(ChunkPostProcessingEvent event) {
		BlockVector last = glassChunk.get(event.getPlayer());
		
		// Convert to glass
		if (last != null && (last.getBlockX() == event.getChunkX() && last.getBlockZ() == event.getChunkZ())) {
			ConversionLookup lookup = event.getLookup();
			
			int glass = Material.GLASS.getId();
			
			// To glass
			lookup.setBlockLookup(Material.BEDROCK.getId(), glass);
			lookup.setBlockLookup(Material.BRICK.getId(), glass);
			lookup.setBlockLookup(Material.CLAY.getId(), glass);
			lookup.setBlockLookup(Material.DIRT.getId(), glass);
			lookup.setBlockLookup(Material.GRASS.getId(), glass);
			lookup.setBlockLookup(Material.GRAVEL.getId(), glass);
			lookup.setBlockLookup(Material.ICE.getId(), glass);
			lookup.setBlockLookup(Material.OBSIDIAN.getId(), glass);
			lookup.setBlockLookup(Material.NETHER_BRICK.getId(), glass);
			lookup.setBlockLookup(Material.NETHERRACK.getId(), glass);
			lookup.setBlockLookup(Material.SAND.getId(), glass);
			
			lookup.setBlockLookup(Material.SOUL_SAND.getId(), glass);
			lookup.setBlockLookup(Material.STONE.getId(), glass);
			
			// To air
			lookup.setBlockLookup(Material.SNOW.getId(), 0);
			lookup.setBlockLookup(Material.WATER.getId(), 0);
			
			lookup.setBlockLookup(Material.LAPIS_BLOCK.getId(), glass);
			lookup.setBlockLookup(Material.IRON_BLOCK.getId(), glass);
			
			System.out.println("GLASS at " + event.getChunkX() + " " + event.getChunkZ());
		}
	}
	
	@EventHandler
	public void onItemConverting(ItemConvertingEvent event) {
		//for (ItemStack stack : event.getItemStacks()) {
		//	if (stack != null && stack.getTypeId() == Material.SAND.getId()) {
		//		stack.setTypeId(Material.GLASS.getId());
		//	}
		//}
	}
}
