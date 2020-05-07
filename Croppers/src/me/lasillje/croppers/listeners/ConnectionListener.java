package me.lasillje.croppers.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.lasillje.croppers.Croppers;

public class ConnectionListener implements Listener {

	private Croppers plugin;
	
	public ConnectionListener(Croppers plugin) {
		this.plugin = plugin;
	}
	
	/*
	 * Check player data on join
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		plugin.getCropperData().setupUserdata(e.getPlayer());
	}
	
	/*
	 * Save player data on disconnect
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		plugin.getCropperData().loadUserdata(e.getPlayer());
		plugin.getCropperData().saveUserdata();
	}
	
}
