package me.lasillje.croppers;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.lasillje.croppers.commands.CommandCropper;
import me.lasillje.croppers.data.CropperData;
import me.lasillje.croppers.listeners.ConnectionListener;
import me.lasillje.croppers.listeners.CropItemListener;
import me.lasillje.croppers.listeners.CropperListener;

public class Croppers extends JavaPlugin {
	
	private CropperData cropperData;
	private CommandCropper commandCropper;
	
	@Override
	public void onEnable() {
		
		CONFIG.loadConfig(this);
		
		cropperData = new CropperData(this);
		commandCropper = new CommandCropper(this);
		
		registerCommand("cropper",commandCropper);
		
		registerListener(new ConnectionListener(this));
		registerListener(new CropperListener(this));
		registerListener(new CropItemListener(this));
		
		cropperData.checkUserdataFolder();
		cropperData.loadLiveCroppers();
	}
	
	public void registerCommand(String name, CommandExecutor executor) {
		PluginCommand command = getCommand(name);
		if(command != null) {
			command.setExecutor(executor);
			if(executor instanceof TabCompleter) {
				command.setTabCompleter((TabCompleter) executor);
			}
		} else {
			getLogger().log(Level.SEVERE, "Couldn't register command: /" + name);
		}
	}
	
	public void registerListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener,this);
	}
	
	public CropperData getCropperData() {
		return cropperData;
	}

}
