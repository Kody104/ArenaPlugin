package com.gmail.jpk.stu.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.jpk.stu.Arena.Arena;
import com.gmail.jpk.stu.Arena.GlobalArena;

public class Force extends BasicCommand{

	public Force(Arena plugin) {
		super(plugin);
	}

	@Override
	public boolean performCommand(CommandSender sender, String[] args) {
		if(sender instanceof Player) {
			sender.sendMessage("Only the server can run this command.");
			return true;
		}
		GlobalArena.getCreaturesInArena().clear();
		GlobalArena.toQueuePlayers("The last monster of the round has died!");
		if(!(GlobalArena.getRound() % 5 == 0)) {
			new GlobalArena.DelayRoundStartTask().runTaskLater(getPlugin(), 60);
		}
		else {
			GlobalArena.PlayerShopTele();
			GlobalArena.toQueuePlayers("Shop activiating in 10 seconds...");
			GlobalArena.setHasStarted(false);
		}
		sender.sendMessage("Creature list cleared.");
		return true;
	}

}
