package me.lasillje.croppers.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import me.lasillje.croppers.CONFIG;
import me.lasillje.croppers.CropperUtils;
import me.lasillje.croppers.Croppers;
import me.lasillje.croppers.builder.CropperBuilder;

public class CommandCropper implements CommandExecutor, TabCompleter {
	
	private Croppers plugin;
	
	public CommandCropper(Croppers plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) {
			sender.sendMessage(CropperUtils.color(CONFIG.COMMAND_DENIED_PLAYER));
			return true;
		}
		
		if(args.length == 0) {
			if(sender.hasPermission("croppers.help")) {
				sender.sendMessage(CropperUtils.color(CONFIG.COMMAND_HOPPER_HELP));
				sender.sendMessage(CropperUtils.color(CONFIG.COMMAND_HOPPER_TYPES));
				sender.sendMessage(CropperUtils.color(CONFIG.COMMAND_HOPPER_SETMAX));
				return true;
			} else {
				sender.sendMessage(CropperUtils.color(CONFIG.COMMAND_PERM_DENIED));
			}
		} else
			
		if(args.length == 1) {
			
			if(sender.hasPermission("croppers.give")) {
				giveCropper(args[0],(Player) sender);
				return true;
			} else {
				sender.sendMessage(CropperUtils.color(CONFIG.COMMAND_PERM_DENIED));
			}
			
		} else
			
		if(args.length == 2) {
			
			if(sender.hasPermission("croppers.give")) {
				
				Player p = Bukkit.getServer().getPlayer(args[1]);
			
				if(p == null) {
					sender.sendMessage(CropperUtils.color(CONFIG.INVALID_PLAYER));
					return true;
				}
			
				giveCropper(args[0],p);
				return true;
			} else {
				sender.sendMessage(CropperUtils.color(CONFIG.COMMAND_PERM_DENIED));
			}
		} else
			
		if(args.length == 3) {
			
			if(args[0].equalsIgnoreCase("setmax")) {
				return setPlayerCropperLimit(sender,args);
			}
		}
		
		return true;
	}
	
	/*
	 * Function to give player a cropper
	 * @param type The cropper type
	 * @param p Player to which it is given to
	 */
	private void giveCropper(String type, Player p) 
	{
		ItemStack cropper = CropperBuilder.build(type);
		
		if(cropper == null) {
			p.sendMessage(CropperUtils.color(CONFIG.INVALID_TYPE));
			return;
		}
		
		p.sendMessage(CropperUtils.formatColor(CONFIG.GAVE_HOPPER, p, type.toUpperCase()));
		p.getInventory().addItem(cropper);
	}
	
	/*
	 * Function to change player's max amount of croppers
	 * @param sender Command sender
	 * @param args Command arguments
	 */
	private boolean setPlayerCropperLimit(CommandSender sender, String[] args) {
		
		if(!sender.hasPermission("croppers.setmax")) {
			sender.sendMessage(CropperUtils.color(CONFIG.COMMAND_PERM_DENIED));
			return true;
		}
		
		if(Bukkit.getServer().getPlayer(args[1]) != null) {
			if(StringUtils.isNumeric(args[2])) {
				
				Player p = Bukkit.getServer().getPlayer(args[1]);
				int limit = Integer.valueOf(args[2]);
				
				sender.sendMessage(CropperUtils.formatColor(CONFIG.CROPPERS_NEW_MAX, p, limit));
				plugin.getCropperData().setPlayerLimit(p, limit);
				return true;
				
			} else {
				sender.sendMessage(CropperUtils.color(CONFIG.CROPPERS_NAN));
				return true;
			}
			
		} else {
			sender.sendMessage(CropperUtils.color(CONFIG.INVALID_PLAYER));
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		List<String> completions = new ArrayList<>();
		List<String> commands = new ArrayList<>();
		
		if(args.length == 1) {
			
			if(sender.hasPermission("croppers.give")) {
				commands.add("cactus");
				commands.add("sugarcane");
				commands.add("melon");
				commands.add("pumpkin");
				commands.add("setmax");
			}
			StringUtil.copyPartialMatches(args[0], commands, completions);
		}
		
		Collections.sort(completions);
		return completions;
	}
}
