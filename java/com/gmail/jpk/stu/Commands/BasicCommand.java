package com.gmail.jpk.stu.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.jpk.stu.Arena.Arena;

public abstract class BasicCommand implements CommandExecutor {

	private Arena plugin;
	
	public BasicCommand(Arena plugin) {
		this.plugin = plugin;
	}
	
	public abstract boolean performCommand(CommandSender sender, String args[]);
	
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String args[]) {
		return performCommand(sender, args);
	}
	
	public Arena getPlugin() {
		return plugin;
	}

}
