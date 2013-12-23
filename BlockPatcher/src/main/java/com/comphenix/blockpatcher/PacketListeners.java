package com.comphenix.blockpatcher;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;

// Import server packets
import static com.comphenix.protocol.PacketType.Play.Server.*;

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
			new PacketAdapter(plugin, ListenerPriority.HIGHEST, 
			  MAP_CHUNK, MAP_CHUNK_BULK, UPDATE_SIGN, TILE_ENTITY_DATA) {
				@Override
				public void onPacketSending(PacketEvent event) {
					try {
						if (event.getPacketType() == MAP_CHUNK) {
							calculations.translateMapChunk(event.getPacket(), event.getPlayer());
						} else if (event.getPacketType() == MAP_CHUNK_BULK) {
							calculations.translateMapChunkBulk(event.getPacket(), event.getPlayer());
						} else {
							// Update sign or tile entity data - these are only enqueued so they 
							// are sent in the correct order
						}

					} catch (FieldAccessException e) {
						plugin.getLogger().log(Level.SEVERE, "Cannot access chunk data", e);
					}
				}
			}).start(2);
	
		// Handling depreciated events
		if (!registerPickupSpawn(manager, calculations)) {
			manager.addPacketListener(
				new PacketAdapter(plugin, ListenerPriority.HIGHEST, ENTITY_METADATA) {
					@Override
					public void onPacketSending(PacketEvent event) {
						calculations.translateDroppedItemMetadata(event.getPacket(), event.getPlayer(), scheduler);
					}
				});
		}
		
		// These are small enough to be run on the main thread
		manager.addPacketListener(
			new PacketAdapter(plugin, ListenerPriority.HIGHEST,
					MAP_CHUNK,
					MAP_CHUNK_BULK,
					BLOCK_CHANGE, 
					MULTI_BLOCK_CHANGE, 
					SPAWN_ENTITY,
					SET_SLOT,
					WINDOW_ITEMS) {
				public void onPacketSending(PacketEvent event) {
					try {
						PacketContainer packet = event.getPacket();
						Player player = event.getPlayer();
						ItemStack[] stacks;
						
						PacketType type = event.getPacketType();
						
						if (type == MAP_CHUNK) {
							if (calculations.isImportantChunk(packet, player)) 
								event.getAsyncMarker().setNewSendingIndex(0);
						} else if (type == MAP_CHUNK_BULK) {
							if (calculations.isImportantChunkBulk(packet, player)) 
								event.getAsyncMarker().setNewSendingIndex(0);
						} else if (type == BLOCK_CHANGE) {
							calculations.translateBlockChange(packet, player);
						} else if (type == MULTI_BLOCK_CHANGE) {
							calculations.translateMultiBlockChange(packet, player);
						} else if (type == SPAWN_ENTITY) {
							calculations.translateFallingObject(packet, player);
						} else if (type == SET_SLOT) {
							stacks = new ItemStack[] { packet.getItemModifier().read(0) };
							scheduler.computeItemConversion(stacks, player, true);
						} else if (type == WINDOW_ITEMS) {
							stacks = packet.getItemArrayModifier().read(0);
							scheduler.computeItemConversion(stacks, player, true);
						}

					} catch (FieldAccessException e) {
						plugin.getLogger().log(Level.SEVERE, "Cannot access chunk data", e);
					}
				};
			});
	}
	
	private boolean registerPickupSpawn(final ProtocolManager manager, final Calculations calculations) {
		// Handle the pickup spawn packet in 1.4.5 and lower
		if (PacketType.Legacy.Server.PICKUP_SPAWN.isSupported()) {
			manager.addPacketListener(
				new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Legacy.Server.PICKUP_SPAWN) {
					@Override
					public void onPacketSending(PacketEvent event) {
						calculations.translateDroppedItem(event.getPacket(), event.getPlayer(), scheduler);
					}
				});
			
			return true;
		}
		
		// We need to register something else
		return false;
	}
}
