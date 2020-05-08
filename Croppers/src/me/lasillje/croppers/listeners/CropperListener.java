package me.lasillje.croppers.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import me.lasillje.croppers.CONFIG;
import me.lasillje.croppers.CropperUtils;
import me.lasillje.croppers.Croppers;
import me.lasillje.croppers.builder.CropperBuilder;

public class CropperListener implements Listener {
	
	private String[] validTypes = {"cactus", "sugarcane", "melon", "pumpkin"};
	
	private Croppers plugin;
	
	public CropperListener(Croppers plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onCropperBreak(BlockBreakEvent e) {
	
		if(e.getBlock().getType() != Material.HOPPER) {
			return;
		}
		
		if(cropperExists(e.getBlock().getLocation())) {
			if(!e.getPlayer().hasPermission("croppers.break")) {
				e.getPlayer().sendMessage(CropperUtils.color(CONFIG.COMMAND_PERM_DENIED));
				e.setCancelled(true);
				return;
			}
			
			String type = plugin.getCropperData().getLiveCroppers().get(e.getBlock().getLocation()).toLowerCase();
			e.setDropItems(false);
			e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), CropperBuilder.build(type));
			plugin.getCropperData().removeHopper(e.getBlock().getLocation(), e.getPlayer());
		}
	}

	@EventHandler
	public void onCropperPlace(BlockPlaceEvent e) {
		
		ItemStack hand = e.getItemInHand();
		
		if(hand.getType() != Material.HOPPER) {
			return;
		}
		
		if(!hand.getItemMeta().getDisplayName().contains(" ")) {
			return;
		}
		
		String type = "";
		String lore[] = ChatColor.stripColor(hand.getItemMeta().getLore().get(0)).split(" ");
		for(String s : lore) {
			if(Arrays.asList(validTypes).contains(s.toLowerCase())) {
				type = s;
				break;
			}
		}
			
		if(type.equalsIgnoreCase("")) {
			return;
		}
		
		/*
		 * Player is holding a cropper
		 */
		
		if(plugin.getCropperData().chunkContainsCropper(e.getBlock().getLocation())) {
			e.getPlayer().sendMessage(CropperUtils.color(CONFIG.CHUNK_OCCUPIED));
			e.setCancelled(true);
			return;
		}
		
		Player p = e.getPlayer();
		
		if(!plugin.getCropperData().allowedToPlace(p)) {
			e.getPlayer().sendMessage(CropperUtils.color(CONFIG.CROPPERS_MAX));
			e.setCancelled(true);
			return;
		}
		
		if(p.hasPermission("croppers.place")) {
			plugin.getCropperData().addHopper(e.getBlock().getLocation(), p, type);
		} else {
			p.sendMessage(CropperUtils.color(CONFIG.COMMAND_PERM_DENIED));
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onCropperExplode(EntityExplodeEvent e) {
		
		List<Block> croppers = getExplodedCroppers(e.blockList());
		
		if(croppers.isEmpty()) {
			return;
		}
		
		List<Location> cropperLocations = plugin.getCropperData().getLiveCroppers().entrySet().stream().
				filter(k -> croppers.contains(k.getKey().getBlock())).map(k -> k.getKey()).collect(Collectors.toList());
		
		if(cropperLocations.isEmpty()) {
			return;
		}
		
		cropperLocations.forEach(k->e.blockList().remove(k.getBlock()));
	}
	
	/*
	 * Checks if there is a cropper on a certain location
	 * @param loc Location to be checked
	 */
	private boolean cropperExists(Location loc) {
		if(plugin.getCropperData().getLiveCroppers().containsKey(loc)) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * In case of an explosion, get croppers if there are any
	 */
	private List<Block> getExplodedCroppers(List<Block> blocks) {
		return blocks.stream().filter(b -> b.getType() == Material.HOPPER).collect(Collectors.toList());
	}
}

