package me.lasillje.croppers.builder;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.lasillje.croppers.CONFIG;
import me.lasillje.croppers.CropperUtils;

public class CropperBuilder {

	/*
	 * Builds a new cropper with the specified type
	 * @param type Type of new cropper
	 */
	public static ItemStack build(String type) {
		
		ItemStack cropper = new ItemStack(Material.HOPPER,1);
		ItemMeta cropperMeta = cropper.getItemMeta();
		
		ChatColor color = ChatColor.BLACK;
		
		switch(type) {
		
		case "cactus": {
			color = ChatColor.DARK_GREEN;
			break;
		}
		
		case "sugarcane": {
			color = ChatColor.GREEN;
			break;
		}
		
		case "melon": {
			color = ChatColor.RED;
			break;
		}
		
		case "pumpkin": {
			color = ChatColor.GOLD;
			break;
		}
		
		default: {
			return null;
		}
		
		}
		
		String lore[] = {CropperUtils.formatColor(CONFIG.HOPPER_LORE, type.toUpperCase())};
		
		cropperMeta.setDisplayName(color + StringUtils.capitalize(type) + ChatColor.GRAY + " Hopper");
		cropperMeta.setLore(Arrays.asList(lore));
		
		cropper.setItemMeta(cropperMeta);
		
		return cropper;
	}
}
