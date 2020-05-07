package me.lasillje.croppers.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import me.lasillje.croppers.Croppers;

public class CropItemListener implements Listener {

	private Croppers plugin;
	
	private Material[] hoppableCrops = {Material.CACTUS, Material.SUGAR_CANE, Material.MELON_SLICE, Material.PUMPKIN};

	public CropItemListener(Croppers plugin) {
		this.plugin = plugin;
	}
	
	/*
	 * Collects matching items to a cropper if one is found
	 */
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e) {
		
		ItemStack item =  e.getEntity().getItemStack();
		
		if(!Arrays.asList(hoppableCrops).contains(item.getType())) {
			return;
		}

		Chunk itemChunk = e.getEntity().getLocation().getChunk();
		
		Location cropperLocation = null;
		String type = null;
		
		for(Entry<Location, String> loc : plugin.getCropperData().getLiveCroppers().entrySet()) {
			if(loc.getKey().getChunk() == itemChunk) {
				cropperLocation = loc.getKey();
				type = loc.getValue();
				break;
			}
		}
		
		if(cropperLocation == null) {
			return;
		}
		
		/*
		 * Cropper exists
		 * Check for matching type
		 * Check for matching entity types on ground currently
		 */
		
		if(matchTypes(type,item)) {
			Hopper hopper = (Hopper) cropperLocation.getBlock().getState();
			collectRemaining(hopper,item);
			hopper.getInventory().addItem(item);
			e.setCancelled(true);
		}
	}
	
	/*
	 * Collects remaining items on ground matching the specified item's type
	 */
	private void collectRemaining(Hopper hopper, ItemStack item) {
	
		Chunk chunk = hopper.getLocation().getChunk();
		List<Entity> items = Arrays.stream(chunk.getEntities()).
				filter(e -> e instanceof Item).
				filter(i -> ((Item) i).getItemStack().getType() == item.getType()).
				collect(Collectors.toList());
	
		if(items.isEmpty()) {
			return;
		}
		
		for(Entity matchingItem : items) {
			hopper.getInventory().addItem(((Item) matchingItem).getItemStack());
			matchingItem.remove();
		}
	}
	
	/*
	 * Match item types to hopper types
	 * Special case for melon and sugarcane as its item does not correspond in its type name in contrary to the other hoppable items
	 */
	private boolean matchTypes(String type, ItemStack item) {
		if(type.equalsIgnoreCase("melon")) {
			if(item.getType() == Material.MELON_SLICE) {
				return true;
			}
		} else if(type.equalsIgnoreCase("sugarcane")) {
			if(item.getType() == Material.SUGAR_CANE) {
				return true;
			}
		} else if(Material.getMaterial(type.toUpperCase()) == item.getType()) {
			return true;
		}

		return false;
		
	}
}
