package com.gmail.jpk.stu.Timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.jpk.stu.Arena.GlobalArena;
import com.gmail.jpk.stu.Entities.GamePlayer;

public class StatChangeTimer extends BukkitRunnable {

	private Player sender;
	private String stat;
	private double statChange;
	
	public StatChangeTimer(Player sender, String stat, double statChange) {
		this.sender = sender;
		this.stat = stat;
		this.statChange = statChange;
	}
	
	@Override
	public void run() {
		if(GlobalArena.GetQueuePlayer(sender) != null) {
			GamePlayer gp = GlobalArena.GetQueuePlayer(sender);
			switch(stat.toLowerCase()) {
				case "atk":
					gp.setAtk(gp.getAtk(true) + statChange);
					break;
				case "magic":
					gp.setMagic(gp.getMagic(true) + statChange);
					break;
				case "defense":
					gp.setDefense(gp.getDefense(true) + statChange);
					break;
				case "resistance":
					gp.setResistance(gp.getResistance(true) + statChange);
					break;
				case "dodge":
					gp.setDodge(gp.getDodge(true) + statChange);
					break;
				case "critical":
					gp.setCritical(gp.getCritical(true) + statChange);
					break;
				default:
					sender.sendMessage(stat + " isn't an actual stat!");
					break;
			}
		}
	}
	
	public Player getPlayerSender()
	{
		return sender;
	}
	
	public String getStat()
	{
		return stat;
	}
	
	public double getStatChange()
	{
		return statChange;
	}

}
