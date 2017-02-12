package com.gmail.jpk.stu.Timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.jpk.stu.Abilities.Ability;
import com.gmail.jpk.stu.Entities.ClassRole;

public class AbilityTimer extends BukkitRunnable {
	
	private Player sender;
	private Ability owner;
	private ClassRole role;
	
	public AbilityTimer(Player sender, Ability owner, ClassRole role) {
		this.sender = sender;
		this.owner = owner;
		this.role = role;
	}
	
	public void run() {
		owner.setIsActive(false);
		if(role == ClassRole.ARCHER && !(owner.getName().equalsIgnoreCase("scavanger"))) {
			sender.sendMessage("You're " + owner.getDisplayName() + " arrow is ready.");
		}
		else if(role == ClassRole.ARCHER && owner.getName().equalsIgnoreCase("scavanger")) {
			sender.sendMessage("You've used your " + owner.getDisplayName() + " ability to get some arrows!");
		}
		else {
			sender.sendMessage("You're " + owner.getDisplayName() + " ability is ready.");
		}
	}
	
	public Player getPlayerSender() {
		return sender;
	}
	
	public Ability getAbility() {
		return owner;
	}
	
	public ClassRole getPlayerClassRole() {
		return role;
	}
}
