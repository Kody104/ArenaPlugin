package com.gmail.jpk.stu.Timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.jpk.stu.Abilities.Ability;
import com.gmail.jpk.stu.Arena.GlobalArena;

public class StackTimer extends BukkitRunnable {

	private Player sender;
	private Ability ability;
	
	public StackTimer(Player sender, Ability ability) {
		this.sender = sender;
		this.ability = ability;
	}
	
	@Override
	public void run() {
		if(GlobalArena.GetQueuePlayer(sender) != null) {
			if(ability.getCurrentStacks() > 0) {
				ability.setCurrentStacks(ability.getCurrentStacks() - 1);
			}
		}
	}

	public Player getSender() {
		return sender;
	}

	public void setSender(Player sender) {
		this.sender = sender;
	}

	public Ability getAbility() {
		return ability;
	}

	public void setAbility(Ability ability) {
		this.ability = ability;
	}
	
	
}
