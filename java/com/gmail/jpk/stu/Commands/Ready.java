package com.gmail.jpk.stu.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.jpk.stu.Arena.Arena;
import com.gmail.jpk.stu.Arena.GlobalArena;
import com.gmail.jpk.stu.Entities.GamePlayer;

public class Ready extends BasicCommand {

	public Ready(Arena plugin) {
		super(plugin);
	}

	@Override
	public boolean performCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You have to be a player to take part in the Arena!");
			return true;
		}
		Player p = (Player) sender;
		if(GlobalArena.GetQueuePlayer(p) != null) {
			if(GlobalArena.getHasStarted()) {
				p.sendMessage("The Arena has already started!");
				return true;
			}
			GlobalArena.GetQueuePlayer(p).setIsReady(!GlobalArena.GetQueuePlayer(p).getIsReady());
			if(GlobalArena.GetQueuePlayer(p).getIsReady()) {
				int total = GlobalArena.getPlayersInQueue().size();
				int votes = 0;
				for(GamePlayer gp : GlobalArena.getPlayersInQueue()) {
					if(gp.getIsReady()) {
						votes++;
					}
				}
				if(votes == total) {
					GlobalArena.QueuePlayersTele(GlobalArena.getSpawn(3));
					GlobalArena.toQueuePlayers("The Arena has begun!");
					GlobalArena.setHasStarted(true);
					new GlobalArena.DelayRoundStartTask().runTaskLater(getPlugin(), 60);
				}
				else {
					GlobalArena.toQueuePlayers(p.getDisplayName() + " has readied! Currently at: " + votes + "/" + total + ".");
				}
			}
			else {
				int total = GlobalArena.getPlayersInQueue().size();
				int votes = 0;
				for(GamePlayer gp : GlobalArena.getPlayersInQueue()) {
					if(gp.getIsReady()) {
						votes++;
					}
				}
				GlobalArena.toQueuePlayers(p.getDisplayName() + " has unreadied! Currently at: " + votes + "/" + total + ".");
			}
		}
		else {
			p.sendMessage("You must be apart of the Arena to ready up!");
		}
		return true;
	}

}
