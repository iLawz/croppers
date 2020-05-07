package me.lasillje.croppers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CropperUtils {
	
	public enum TYPECOLOR {
		
		CACTUS(ChatColor.DARK_GREEN),
		SUGARCANE(ChatColor.GREEN),
		MELON(ChatColor.RED),
		PUMPKIN(ChatColor.GOLD);
		
		private ChatColor color;
			
		TYPECOLOR(ChatColor color) {
			this.color = color;
		}
		
		public ChatColor getColor() {
			return color;
		}
	}

	/*
	 * Easier to use than typing ChatColor... everytime
	 */
	public static String color(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	/*
	 * Used for config file inputs
	 * Player && cropper type
	 */
	public static String formatColor(String string, Player player, String type) {
		
		if(string.contains("%typecolor%")) {
			string = string.replace("%typecolor%", "" + TYPECOLOR.valueOf(type.toUpperCase()).getColor());
		}
		
		string = string.replace("%player%", player.getName());
		string = string.replace("%type%", type);
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	/*
	 * Cropper type
	 */
	public static String formatColor(String string, String type) {
		if(string.contains("%typecolor%")) {
			string = string.replace("%typecolor%", "" + TYPECOLOR.valueOf(type.toUpperCase()).getColor());
		}
		string = string.replace("%type%", type);
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	/*
	 * Number && player 
	 */
	public static String formatColor(String string, Player player, int max) {
		string = string.replace("%player%", player.getName());
		string = string.replace("%number%", Integer.toString(max));
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
}
