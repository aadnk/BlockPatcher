package com.comphenix.blockpatcher;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;

class PacketListeners {

	private Plugin plugin;
	private EventScheduler scheduler;
	
	public PacketListeners(Plugin plugin, EventScheduler scheduler) {
		super();
		this.plugin = plugin;
		this.scheduler = scheduler;
	}

	public void registerEvents(final Calculations calculations) {
		
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();		
		
		// Modify chunk packets asynchronously
		manager.getAsynchronousManager().registerAsyncHandler(
			new PacketAdapter(plugin, ConnectionSide.SERVER_SIDE, ListenerPriority.HIGHEST, 
					Packets.Server.MAP_CHUNK, Packets.Server.MAP_CHUNK_BULK) {
				@Override
				public void onPacketSending(PacketEvent event) {
					try {
						switch (event.getPacketID()) {
						case Packets.Server.MAP_CHUNK:
							calculations.translateMapChunk(event.getPacket(), event.getPlayer());
							break;
						case Packets.Server.MAP_CHUNK_BULK:
							calculations.translateMapChunkBulk(event.getPacket(), event.getPlayer());
							break;
						}

					} catch (FieldAccessException e) {
						plugin.getLogger().log(Level.SEVERE, "Cannot access chunk data", e);
					}
				}
			}).start(2);
		
		// These are small enough to be run on the main thread
		manager.addPacketListener(
			new PacketAdapter(plugin, ConnectionSide.SERVER_SIDE, ListenerPriority.HIGHEST,
					Packets.Server.MAP_CHUNK,
					Packets.Server.MAP_CHUNK_BULK,
					Packets.Server.BLOCK_CHANGE, 
					Packets.Server.MULTI_BLOCK_CHANGE, 
					Packets.Server.VEHICLE_SPAWN,
					Packets.Server.PICKUP_SPAWN,
					Packets.Server.SET_SLOT,
					Packets.Server.WINDOW_ITEMS) {
				public void onPacketSending(PacketEvent event) {
					try {
						PacketContainer packet = event.getPacket();
						Player player = event.getPlayer();
						ItemStack[] stacks;
						
						switch (event.getPacketID()) {
						case Packets.Server.MAP_CHUNK:
							if (calculations.isImportantChunk(packet, player)) 
								event.getAsyncMarker().setNewSendingIndex(0);
							break;
						case Packets.Server.MAP_CHUNK_BULK:
							if (calculations.isImportantChunkBulk(packet, player)) 
								event.getAsyncMarker().setNewSendingIndex(0);
							break;
						case Packets.Server.BLOCK_CHANGE:	
							calculations.translateBlockChange(packet, player);
							break;
						case Packets.Server.MULTI_BLOCK_CHANGE:
							calculations.translateMultiBlockChange(packet, player);
							break;
						case Packets.Server.VEHICLE_SPAWN:
							calculations.translateFallingObject(packet, player);
							break;
						case Packets.Server.PICKUP_SPAWN:
							calculations.translateDroppedItem(packet, player, scheduler);
							break;
						case Packets.Server.SET_SLOT:
							stacks = new ItemStack[] { packet.getItemModifier().read(0) };
					    	scheduler.computeItemConversion(stacks, player, true);
							break;
						case Packets.Server.WINDOW_ITEMS:
							stacks = packet.getItemArrayModifier().read(0);
							scheduler.computeItemConversion(stacks, player, true);
							break;
						}

					} catch (FieldAccessException e) {
						plugin.getLogger().log(Level.SEVERE, "Cannot access chunk data", e);
					}
				};
			});
	}
}
