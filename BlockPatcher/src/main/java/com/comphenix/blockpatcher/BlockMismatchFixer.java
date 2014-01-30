package com.comphenix.blockpatcher;

import static com.comphenix.protocol.PacketType.Play.Client.FLYING;
import static com.comphenix.protocol.PacketType.Play.Client.POSITION;
import static com.comphenix.protocol.PacketType.Play.Client.POSITION_LOOK;

import java.util.concurrent.ConcurrentMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import com.comphenix.blockpatcher.lookup.SegmentLookup;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.google.common.collect.MapMaker;

class BlockMismatchFixer {
	private Plugin plugin;
	
	// Last known block type
	private ConcurrentMap<Player, Double> deltaY = new MapMaker().weakKeys().makeMap();

	private PacketAdapter packetListener;
	private Listener bukkitListener;

	// For retrieving the cached conversion
	private ConversionCache cache;
	
	// Looking up block height
	private final BlockHeight heightLookup;

	public BlockMismatchFixer(Plugin plugin, ConversionCache cache) {
		this.plugin = plugin;
		this.cache = cache;
		this.heightLookup = new BlockHeight(plugin.getLogger());
		registerBukkitListener();
		registerPacketListener();
	}
	
	private void registerPacketListener() {
		// This is executed asynchronously
		ProtocolLibrary.getProtocolManager().addPacketListener(
		  packetListener = new PacketAdapter(plugin, FLYING, POSITION, POSITION_LOOK) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Double delta = deltaY.get(event.getPlayer());

				// Soul sand fix
				if (delta != null && delta != 0) {
					incrementDouble(event, 1, delta);
					incrementDouble(event, 3, delta);
				}
			}

			private void incrementDouble(PacketEvent event, int index, Double delta) {
				StructureModifier<Double> doubles = event.getPacket().getDoubles();
				doubles.write(index, doubles.read(index) + delta);
			}
		});
	}
	
	private void registerBukkitListener() {
		plugin.getServer().getPluginManager().registerEvents(
		  bukkitListener = new Listener() {
			@EventHandler
			public void onPlayerMove(PlayerMoveEvent e) {
				updateLookupDelta(e.getTo(), e.getPlayer());
			}
			
			@EventHandler
			public void onPlayerLogin(PlayerLoginEvent e) {
				updateLookupDelta(e.getPlayer().getLocation(), e.getPlayer());
			}
		}, plugin);
	}
	
	private void updateLookupDelta(Location loc, Player player) {
		final Block underneath = getStandingBlock(loc);
		final SegmentLookup lookup = cache.loadCacheOrDefault(
			player, underneath.getX() >> 4, underneath.getY() >> 4);
		
		final Material serverMaterial = underneath.getType();
		@SuppressWarnings("deprecation")
		final Material clientMaterial = Material.getMaterial(lookup.getBlockLookup(serverMaterial.getId()));
		
		if (serverMaterial != clientMaterial) {
			deltaY.put(player, heightLookup.getHeight(serverMaterial) - heightLookup.getHeight(clientMaterial));
		} else {
			deltaY.remove(player);
		}
	}
	
	private Block getStandingBlock(Location loc) {
		Block foot = loc.getBlock();
		
		// Usually because the block is shorter in height
		if (foot.getType().isSolid())
			return foot;
		return foot.getRelative(BlockFace.DOWN);
	}

	/**
	 * Close this resource.
	 */
	public void close() {
		if (plugin != null) {
			ProtocolLibrary.getProtocolManager().
				removePacketListener(packetListener);
			PlayerMoveEvent.getHandlerList().unregister(bukkitListener);
			plugin = null;
		}
	}
}
