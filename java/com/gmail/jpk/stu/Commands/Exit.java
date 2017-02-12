package com.gmail.jpk.stu.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.jpk.stu.Arena.Arena;
import com.gmail.jpk.stu.Arena.GlobalArena;

public class Exit extends BasicCommand{

	public Exit(Arena plugin) {
		super(plugin);
	}

	@Override
	public boolean performCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Only players can exit the Arena.");
			return true;
		}
		else {
			Player p = (Player) sender;
			if((GlobalArena.GetQueuePlayer(p) != null)) {
				if(GlobalArena.getHasStarted() || GlobalArena.getRound() != 0) {
					p.sendMessage("Only a shitter would abandon his team.");
					return true;
				}
				GlobalArena.GetQueuePlayer(p).resetScore();
				GlobalArena.RemoveQueuePlayer(p);
				p.setFoodLevel(20);
				p.setScoreboard(GlobalArena.getScoreboardManager().getNewScoreboard()); // Reset the scoreboard for the player
				p.sendMessage("You have left the Arena!");
				GlobalArena.toQueuePlayers(p.getDisplayName() + " has left! Currently at" + GlobalArena.getPlayersInQueue().size() + " player(s).");
				return true;
			}
			else if(GlobalArena.GetSpectatorPlayer(p) != null) {
				GlobalArena.RemoveSpectatorPlayer(p);
				p.sendMessage("You have left the spectators!");
				return true;
			}
			else {
				p.sendMessage("You aren't apart of the Arena!");
			}
		}
		return true;
	}

}
