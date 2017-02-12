package com.gmail.jpk.stu.Abilities;

import org.bukkit.ChatColor;


public class Ability {
	
	private String Name;
	private int reqLvl;
	private boolean isActive;
	private double pow;
	private int cooldown;
	private ChatColor color;
	private int maxStacks;
	private int currentStacks;
	
	public Ability(String Name, int reqLvl, double Pow, int Cooldown) {
		this.Name = Name;
		this.reqLvl = reqLvl;
		this.isActive = false;
		this.pow = Pow;
		this.cooldown = Cooldown;
		this.maxStacks = 0;
		this.currentStacks = 0;
	}
	
	public Ability(String Name, int reqLvl, double Pow, int Cooldown, ChatColor color) {
		this.Name = Name;
		this.reqLvl = reqLvl;
		this.isActive = false;
		this.pow = Pow;
		this.cooldown = Cooldown;
		this.color = color;
		this.maxStacks = 0;
		this.currentStacks = 0;
	}
	
	public Ability(String Name, int reqLvl, double Pow, int Cooldown, ChatColor color, int maxStacks) {
		this.Name = Name;
		this.reqLvl = reqLvl;
		this.isActive = false;
		this.pow = Pow;
		this.cooldown = Cooldown;
		this.color = color;
		this.maxStacks = maxStacks;
		this.currentStacks = 0;
	}
	
	public String getName() {
		return Name;
	}
	
	public String getDisplayName() {
		if(color != null) {
			return color + Name + ChatColor.WHITE;
		}
		return ChatColor.DARK_PURPLE + Name + ChatColor.WHITE;
	}
	
	public boolean hasCustomColor() {
		if(color == null) {
			return false;
		}
		return true;
	}
	
	public boolean isStackable() {
		if(maxStacks > 0) {
			return true;
		}
		return false;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public int getReqLvl() {
		return reqLvl;
	}
	
	public boolean getIsActive() {
		return isActive;
	}
	
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public void setPow(double pow) {
		this.pow = pow;
	}
	
	public double getPow() {
		return pow;
	}
	
	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}
	
	public int getCooldown() {
		return cooldown;
	}

	public int getMaxStacks() {
		return maxStacks;
	}

	public void setMaxStacks(int maxStacks) {
		this.maxStacks = maxStacks;
	}

	public int getCurrentStacks() {
		return currentStacks;
	}

	public void setCurrentStacks(int currentStacks) {
		this.currentStacks = currentStacks;
	}
}
