package com.gmail.jpk.stu.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.jpk.stu.Arena.Arena;
import com.gmail.jpk.stu.Arena.GlobalArena;
import com.gmail.jpk.stu.Entities.GamePlayer;

public class Sidebar extends BasicCommand{

	public Sidebar(Arena plugin) {
		super(plugin);
	}

	@Override
	public boolean performCommand(CommandSender sender, String[] args) {
		if(sender instanceof Player) {
			if(args.length != 1) {
				sender.sendMessage("Usage: /sidebar <level | score>");
				return true;
			}
			if(!args[0].toLowerCase().equals("level") && !args[0].toLowerCase().equals("score") && !args[0].toLowerCase().equals("health")) {
				sender.sendMessage("Not a valid sidebar!");
				return true;
			}
			Player p = (Player) sender;
			if(GlobalArena.GetQueuePlayer(p) != null) {
				int select = -1;
				if(args[0].toLowerCase().equals("level") || args[0].toLowerCase().equals("lvl")) {
					select = 0;
				}
				else if(args[0].toLowerCase().equals("health") || args[0].toLowerCase().equals("hp")) {
					select = 2;
				}
				else {
					select = 1;
				}
				GamePlayer gp = GlobalArena.GetQueuePlayer(p);
				gp.setCurrentScoreboard(select);
				p.sendMessage("You have changed your sidebar!");
				return true;
			}
		}
		return true;
	}

}
