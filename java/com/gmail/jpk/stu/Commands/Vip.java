package com.gmail.jpk.stu.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.jpk.stu.Arena.Arena;
import com.gmail.jpk.stu.Arena.Data;

public class Vip extends BasicCommand {

	public Vip(Arena plugin) {
		super(plugin);
	}

	@Override
	public boolean performCommand(CommandSender sender, String[] args) {
		if(args.length != 2) {
			sender.sendMessage("Usage: /vip <player> [0/1]");
			return true;
		}
		int bool;
		try{
			bool = Integer.parseInt(args[1]);
		} catch(NumberFormatException e) {
			sender.sendMessage("Usage: /vip <player> [0/1]");
			return true;
		}
		if(bool > 1 || bool < 0) {
			sender.sendMessage("Usage: /vip <player> [0/1]");
			return true;
		}
		if(sender.getServer().getPlayer(args[0]) != null) {
			Player p = sender.getServer().getPlayer(args[0]);
			if(Data.HasPlayer(p)) {
				if(bool == 0) {
					if(!Data.HasPlayerProperty(p, "vip")) {
						sender.sendMessage(p.getName() + " isn't a vip player.");
						return true;
					}
					Data.AddPropertyToPlayer(p, "vip", false);
					sender.sendMessage(p.getName() + " is no longer a vip player!");
					return true;
				}
				else if(bool == 1) {
					if(Data.HasPlayerProperty(p, "vip")) {
						sender.sendMessage(p.getName() + " is a vip player.");
						return true;
					}
					Data.AddPropertyToPlayer(p, "vip", true);
					sender.sendMessage(p.getName() + " is now a vip player!");
					return true;
				}
				else {
					sender.sendMessage("Unexpected boolean number. Contact an admin.");
					return true;
				}
			}
			else {
				sender.sendMessage("The data hashmap doesn't contain this player. Contact an admin.");
				return true;
			}
		}
		else {
			sender.sendMessage(args[0] + " isn't online right now.");
			return true;
		}
	}

}
