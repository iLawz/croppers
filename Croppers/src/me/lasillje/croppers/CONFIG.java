package me.lasillje.croppers;

import org.bukkit.configuration.file.FileConfiguration;

public class CONFIG {

	/*
	 * Meta strings
	 */
	public static String PREFIX; //Prefix in front of messages, e.g. [Croppers]
	public static String COMMAND_DENIED_PLAYER; //Sender is not a player
	public static String COMMAND_PERM_DENIED; //No permission
	public static String CHUNK_OCCUPIED; //Chunk already contains a cropper
	public static String CROPPERS_MAX; //Player is at their limit
	public static String CROPPERS_NEW_MAX; //Message when player gets new max limit
	public static String CROPPERS_NAN;

	/*
	 * Commands
	 */
	public static String COMMAND_HOPPER_HELP; //Help command string
	public static String COMMAND_HOPPER_TYPES; //Shows use of /cropper command
	public static String COMMAND_HOPPER_SETMAX; //Shows use of /cropper setmax command
	public static String GAVE_HOPPER; //When using /cropper <type>
	public static String INVALID_TYPE; //When using /cropper <type> incorrectly
	public static String INVALID_PLAYER; //Player not found
	
	/*
	 * Lore
	 */
	public static String HOPPER_LORE; //Lore of hopper
	
	public static void loadConfig(Croppers plugin) {
		
		FileConfiguration config = plugin.getConfig();

		PREFIX = config.getString("prefix");
		COMMAND_DENIED_PLAYER = PREFIX  + config.getString("sender_not_player");
		COMMAND_PERM_DENIED = PREFIX + config.getString("permission_denied");
		CHUNK_OCCUPIED = PREFIX + config.getString("chunk_occupied");
		CROPPERS_MAX = PREFIX + config.getString("croppers_max");
		CROPPERS_NEW_MAX = PREFIX + config.getString("croppers_new_max");
		CROPPERS_NAN = PREFIX + config.getString("croppers_nan");
		
		COMMAND_HOPPER_HELP = PREFIX + config.getString("croppers_help_1");
		COMMAND_HOPPER_TYPES = PREFIX + config.getString("croppers_help_2");
		COMMAND_HOPPER_SETMAX = PREFIX + config.getString("croppers_help_3");
		GAVE_HOPPER = PREFIX + config.getString("croppers_gave");
		INVALID_TYPE = PREFIX + config.getString("croppers_invalid_type");
		INVALID_PLAYER = PREFIX + config.getString("croppers_invalid_player");
		HOPPER_LORE = config.getString("hopper_lore");
		
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveDefaultConfig();
	}
}
