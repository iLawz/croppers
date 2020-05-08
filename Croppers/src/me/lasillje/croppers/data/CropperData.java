package me.lasillje.croppers.data;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.lasillje.croppers.Croppers;

public class CropperData {
	
	private Croppers plugin;
	
	private FileConfiguration fileConfig;
	
	private File userFile;
	private File folder;
	private File dataFolder; 
	
	private HashMap<Location, String> liveCroppers;
	private HashMap<Location, UUID> playerCroppers; //Could have been better to merge the type and UUID and then split when needed
	
	public CropperData(Croppers plugin) {
		this.plugin = plugin;
		
		dataFolder = plugin.getDataFolder();
		folder = new File(dataFolder, "userdata" + File.separator);
		
		liveCroppers = new HashMap<Location, String>();
		playerCroppers = new HashMap<Location, UUID>();
	}
	
	/*
	 * Function to load all currently active croppers into a HashMap
	 * Location and type are stored.
	 */
	public void loadLiveCroppers() {
		
		plugin.getLogger().log(Level.INFO, "Loading active croppers...");
		
		if(Arrays.asList(folder.listFiles()).isEmpty()) {
			plugin.getLogger().log(Level.INFO, "All active croppers loaded!");
			return;
		}
		
		for(File file : folder.listFiles()) {
		
			loadUserdataFromFile(file);
						
			UUID pUUID = removeExtension(file);
	
			if(fileConfig.getConfigurationSection("hoppers.placed") == null) {
				break;
			}
			
			for(String key : fileConfig.getConfigurationSection("hoppers.placed").getKeys(false)) {
				Location loc = deserializeLocation(fileConfig.getString("hoppers.placed." + key + ".location"));
				String type = fileConfig.getString("hoppers.placed." + key + ".type");
				
				playerCroppers.put(loc, pUUID);
				liveCroppers.put(loc, type);
			}
		}
		
		plugin.getLogger().log(Level.INFO, "All active croppers loaded!");

		plugin.getLogger().log(Level.INFO, "Loaded croppers:" + liveCroppers.size());
		//plugin.getLogger().log(Level.INFO, "Loaded player croppers:" + playerCroppers.size());
		
	}
	
	/*
	 * Get live croppers in the server
	 */
	public HashMap<Location, String> getLiveCroppers() {
		return liveCroppers;
	}
	
	/*
	 * Checks if userdata folder exists, if not then create it.
	 */
	public void checkUserdataFolder() {
		if(!folder.exists()) {
			folder.mkdir();
		}
	}
	
	/*
	 * Setup userdata for first time
	 * @param p Player to create userdata for
	 */
	public void setupUserdata(Player p) {
		
		userFile = new File(folder, p.getUniqueId() + ".yml");
		
		if(!userFile.exists()) {
			try {
				userFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		fileConfig = YamlConfiguration.loadConfiguration(userFile);
		fileConfig.addDefault("name", p.getName());
		fileConfig.addDefault("max_owned", 2);
		fileConfig.addDefault("hoppers.owned", 0);
		fileConfig.options().copyDefaults(true);
		
		try {
			fileConfig.save(userFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Load userdata for play
	 * @param p Userdata to be loaded
	 */
	public void loadUserdata(Player p) {
		userFile = new File(folder, p.getUniqueId() + ".yml");
		fileConfig = YamlConfiguration.loadConfiguration(userFile);
	}
	
	/*
	 * Load userdata for play
	 * @param p Userdata to be loaded
	 */
	public void loadUserdata(OfflinePlayer p) {
		userFile = new File(folder, p.getUniqueId() + ".yml");
		fileConfig = YamlConfiguration.loadConfiguration(userFile);
	}
	
	/*
	 * Loads userdata directly from file in ./userdata directory
	 * @param file File to be loaded
	 */
	private void loadUserdataFromFile(File file) {
		userFile = file;
		fileConfig = YamlConfiguration.loadConfiguration(userFile);
	}
	
	/*
	 * Save currently loaded userdata
	 */
	public void saveUserdata() {
		try {
			fileConfig.options().copyDefaults(true);
			fileConfig.save(userFile);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Gets this file configuration
	 */
	public FileConfiguration getFileConfig() {
		return fileConfig;
	}
	
	public void reloadUserdata(Player p) {
		loadUserdata(p);
		saveUserdata();
	}
	
	/*
	 * Add a new hopper to a certain user
	 * @param loc Location of placed hopper
	 * @param p Player who placed a hopper
	 * @param type Hopper type in string form
	 */
	public void addHopper(Location loc, Player p, String type) {
		
		int chunkX = loc.getChunk().getX();
		int chunkZ = loc.getChunk().getZ();
	
		loadUserdata(p);
		
		fileConfig.set("hoppers.owned", fileConfig.getInt("hoppers.owned") + 1);
		
		String hopperName = "hopper" + chunkX + "_" + chunkZ;
		fileConfig.set("hoppers.placed." + hopperName + ".type", type.toLowerCase());
		fileConfig.set("hoppers.placed." + hopperName + ".location", serializeLocation(loc));
		liveCroppers.put(loc, type);
		playerCroppers.put(loc, p.getUniqueId());
		
		//liveCroppers.entrySet().stream().forEach(e -> Bukkit.broadcastMessage(e.getKey().toString() + ":" + e.getValue()));
		
		saveUserdata();
	}
	
	/*
	 * Remove a hopper at a certain location
	 * @param loc Location of hopper
	 * @param p Player who broke it
	 */
	public void removeHopper(Location loc, Player p) {
		
		int chunkX = loc.getChunk().getX();
		int chunkZ = loc.getChunk().getZ();
		
		Player owner = Bukkit.getServer().getPlayer(playerCroppers.get(loc));
		
		if(owner == null) {
			OfflinePlayer offlineOwner = Bukkit.getServer().getOfflinePlayer(playerCroppers.get(loc));
			loadUserdata(offlineOwner);
		} else {
			loadUserdata(owner);
		}
		
		String hopperName = "hoppers.placed." + "hopper" + chunkX + "_" + chunkZ;

		if(fileConfig.getConfigurationSection(hopperName) != null) {
			liveCroppers.remove(loc);
			playerCroppers.remove(loc);
			fileConfig.set(hopperName, null);
			fileConfig.set("hoppers.owned", fileConfig.getInt("hoppers.owned") - 1);
			//liveCroppers.entrySet().stream().forEach(e -> Bukkit.broadcastMessage(e.getKey().toString() + ":" + e.getValue()));
		}
		saveUserdata();
	}
	
	/*
	 * Checks if current chunk already contains a cropper
	 * @param loc Location to be checked
	 */
	public boolean chunkContainsCropper(Location loc) {	
		Chunk check = loc.getChunk();	
		return (liveCroppers.entrySet().stream().anyMatch(e -> e.getKey().getChunk() == check));
	}
	
	/*
	 * Get the amount of hoppers someone owns
	 * @param p The player to check
	 */
	public int getOwnedCroppers(Player p) { 
		loadUserdata(p);
		return fileConfig.getInt("hoppers.owned");
	}
	
	/*
	 * Checks if a player is allowed to place an additional cropper
	 */
	public boolean allowedToPlace(Player p) {
		loadUserdata(p);
		if(fileConfig.getInt("hoppers.owned") < fileConfig.getInt("max_owned")) {
			return true;
		}
		return false;
	}
	
	/*
	 * Sets player limit for max amount of owned croppers
	 */
	public void setPlayerLimit(Player p, int limit) {
		loadUserdata(p);
		fileConfig.set("max_owned", limit);
		saveUserdata();
	}
	
	/*
	 * Serializes a location into a string
	 * @param loc Location to be serialized
	 */
	private String serializeLocation(Location loc) {
		return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
	}
	
	/*
	 * Deserializes a string into a location
	 * @param s String to be deserialized
	 */
	private Location deserializeLocation(String s) {
		String[] parts = s.split(",");
        return new Location(Bukkit.getWorld(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]));
	}	
	
	/*
	 * removes file extension and returns the file name
	 * @param file File name to be pruned
	 */
	private UUID removeExtension(File file) {
		String name = file.getName();
		name = name.substring(0, name.length()-4);
		return UUID.fromString(name);
	}
}
