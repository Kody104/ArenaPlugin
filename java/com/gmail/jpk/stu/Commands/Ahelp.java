package com.gmail.jpk.stu.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.jpk.stu.Arena.Arena;

public class Ahelp extends BasicCommand {

	public Ahelp(Arena plugin) {
		super(plugin);
	}

	@Override
	public boolean performCommand(CommandSender sender, String[] args) {
		if(args.length != 1) {
			sender.sendMessage("Usage: /ahelp <command>");
			return true;
		}
		if(args[0].equalsIgnoreCase("ahelp")) {
			sender.sendMessage(ChatColor.YELLOW + "This command tries to help you understand another command purpose and usage." + ChatColor.WHITE);
		}
		else if(args[0].equalsIgnoreCase("enter")) {
			sender.sendMessage(ChatColor.YELLOW + "Allows a non-queued player to enter the arena." + ChatColor.WHITE + "\nUsage: /enter");
		}
		else if(args[0].equalsIgnoreCase("exit")) {
			sender.sendMessage(ChatColor.YELLOW + "Allows a queued arena player to exit the arena and queue." + ChatColor.WHITE + "\nUsage: /exit");
		}
		else if(args[0].equalsIgnoreCase("force")) {
			sender.sendMessage(ChatColor.YELLOW + "Forces the next round to start while the arena is active. OP use only." + ChatColor.WHITE + "\nUsage: /force");
		}
		else if(args[0].equalsIgnoreCase("ready")) {
			sender.sendMessage(ChatColor.YELLOW + "Allows a queued arena player to show that they are ready for the round to start." + ChatColor.WHITE + "\nUsage: /ready");
		}
		else if(args[0].equalsIgnoreCase("role")) {
			sender.sendMessage(ChatColor.YELLOW + "Allows a queued arena player to select his class for the arena." + ChatColor.WHITE + "\nUsage: /role <class>");
		}
		else if (args[0].equalsIgnoreCase("class") || args[0].equalsIgnoreCase("classes") || args[0].equalsIgnoreCase("roles")) {
			sender.sendMessage(ChatColor.YELLOW + "Warrior - Tank role. Designed to absorb damage and help tank aggro.\nFighter - Bruiser role. Designed to do consistent damage based on risk vs reward system.\n"
					+ "Priest - Support role. Designed to help buff allies and debuff enemies.\nPaladin - Healer role. Designed to heal damage and mitigate damage to allies.\n"
					+ "Archer - DPS role. Designed to do constant damage from afar.\nMage - Burst role. Designed to burst enemies quickly and efficiently.\nNecromancer - Pet role. Designed to summon creatures to fight for them.");
		}
		return true;
	}
	
	
}
