package com.gmail.jpk.stu.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.jpk.stu.Arena.Arena;
import com.gmail.jpk.stu.Arena.GlobalArena;

public class Enter extends BasicCommand{

	public Enter(Arena plugin) {
		super(plugin);
	}

	@Override
	public boolean performCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to enter the Arena!");
			return true;
		}
		Player p = (Player) sender;
		if(GlobalArena.GetQueuePlayer(p) == null) {
			if(GlobalArena.getHasStarted() || GlobalArena.getRound() != 0) {
				GlobalArena.AddNewSpectatorPlayer(p);
				p.teleport(GlobalArena.getSpawn(1));
				p.getInventory().clear();
				p.sendMessage("The Arena is currently running. Placing you in the spectators.");
				return true;
			}
			if(GlobalArena.getPlayersInQueue().size() < GlobalArena.getMaxSize()) {
				GlobalArena.AddNewQueuePlayer(p);
				p.teleport(GlobalArena.getSpawn(2));
				p.getInventory().clear();
				p.setFoodLevel(10);
				p.sendMessage("You have entered the Arena!");
				GlobalArena.toQueuePlayers(p.getDisplayName() + " has joined! Queue currently at: " + GlobalArena.getPlayersInQueue().size() + " player(s).");
			}
			else{
				p.sendMessage("Too many players currently inside the Arena!");
				if(GlobalArena.GetQueuePlayer(p) == null) {
					GlobalArena.AddNewSpectatorPlayer(p);
					p.teleport(GlobalArena.getSpawn(1));
					p.getInventory().clear();
					p.sendMessage("Placing you in the spectators.");
				}
			}
		}
		else {
			p.sendMessage("You are already apart of the Arena!");
		}
		return true;
	}

}
