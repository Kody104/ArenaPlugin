package com.gmail.jpk.stu.Arena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Data {
	
	private static Map<UUID, HashMap<String, Boolean>> PlayerDatabase = new HashMap<UUID, HashMap<String, Boolean>>();
	
	public static boolean HasPlayerProperty(Player p, String property) {
		if(PlayerDatabase.containsKey(p.getUniqueId())) {
			if(PlayerDatabase.get(p.getUniqueId()).containsKey(property.toLowerCase())) {
				return PlayerDatabase.get(p.getUniqueId()).get(property.toLowerCase());
			}
		}
		return false;
	}
	
	public static boolean HasPlayer(Player p) {
		return PlayerDatabase.containsKey(p.getUniqueId());
	}
	
	public static Map<UUID, HashMap<String, Boolean>> GetPlayerDatabase() {
		return PlayerDatabase;
	}
	
	public static void AddPropertyToPlayer(Player p, String property, boolean defaultValue) {
		if(PlayerDatabase.containsKey(p.getUniqueId())) {
			PlayerDatabase.get(p.getUniqueId()).put(property.toLowerCase(), defaultValue);
			return;
		}
		p.getServer().getLogger().info("Couldn't set " + property + " property to player " + p.getName() + " because they aren't in the database!");
	}
	
	public static void SaveDatabase(Plugin plugin) {
		try {
			File dir = new File("plugins/Arena");
			File file;
			
			FileOutputStream fos;
			ObjectOutputStream out;
			
			plugin.getLogger().info("Creating the directory.");
			dir.mkdirs();
			
			plugin.getLogger().info("Opening FileOutputStream and ObjectOutputStream.");
			file = new File(dir, "players.dat");
			fos = new FileOutputStream(file, false);
			out = new ObjectOutputStream(fos);
			
			plugin.getLogger().info("Saving the hashmap. PlayerDatabase: " + PlayerDatabase);
			out.writeObject(PlayerDatabase);
			
			plugin.getLogger().info("Closing FileOutputStream and ObjectOutputStream.");
			fos.close();
			out.close();
			
			plugin.getLogger().info("Player Databse saved!");
			
		} catch(IOException e) {
			plugin.getLogger().info("An error occured while trying to save players.dat");
			plugin.getLogger().info("Reason: " + e.getCause());
			e.printStackTrace();
		}
	}
	
	public static void LoadDatabase(Plugin plugin) {
		plugin.getLogger().info("Attempting to load Player Data...");

		File file = new File("plugins/Arena/players.dat");
		PlayerDatabase = new HashMap<UUID, HashMap<String, Boolean>>();
		FileInputStream fis;
		ObjectInputStream ois;
		
		if (!file.exists()) {  
			plugin.getLogger().info("\"players.dat\" is missing or corrupted.");
			plugin.getLogger().info("Program will not attempt to load data");
			return;
		} 
		else {
			plugin.getLogger().info("\"players.dat\" found! Loading the hashmap data...");
		}
		
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			
			PlayerDatabase = (HashMap<UUID, HashMap<String, Boolean>>) ois.readObject();
			plugin.getLogger().info("PlayerDatabase was successful loaded!");
			
			fis.close();
			ois.close();
		} catch(IOException io) {
			plugin.getLogger().info("Any error has occured while trying to load player.dat");
			plugin.getLogger().info("Reason: " + io.getCause());
			io.printStackTrace();
		} catch(ClassNotFoundException cnfe) {
			plugin.getLogger().info("Any error has occured while trying to load player.dat");
			plugin.getLogger().info("Reason: " + cnfe.getCause());
			cnfe.printStackTrace();
		}
	}
}
