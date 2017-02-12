package com.gmail.jpk.stu.Arena;

import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.jpk.stu.Commands.Ahelp;
import com.gmail.jpk.stu.Commands.Enter;
import com.gmail.jpk.stu.Commands.Exit;
import com.gmail.jpk.stu.Commands.Force;
import com.gmail.jpk.stu.Commands.Forge;
import com.gmail.jpk.stu.Commands.Ready;
import com.gmail.jpk.stu.Commands.Role;
import com.gmail.jpk.stu.Commands.Sidebar;
import com.gmail.jpk.stu.Commands.Vip;
import com.gmail.jpk.stu.Listeners.AllListener;
import com.gmail.jpk.stu.Listeners.DamageListener;

public class Arena extends JavaPlugin {
	private String version = "0.5.0";
	
	@Override
	public void onEnable() {
		Data.LoadDatabase(this);
		this.getCommand("ahelp").setExecutor(new Ahelp(this));
		this.getCommand("enter").setExecutor(new Enter(this));
		this.getCommand("exit").setExecutor(new Exit(this));
		this.getCommand("ready").setExecutor(new Ready(this));
		this.getCommand("role").setExecutor(new Role(this));
		this.getCommand("force").setExecutor(new Force(this));
		this.getCommand("sidebar").setExecutor(new Sidebar(this));
		this.getCommand("vip").setExecutor(new Vip(this));
		this.getCommand("forge").setExecutor(new Forge(this));
		this.getLogger().info("[Arena] v" + version + " has been enabled!");
		new AllListener(this);
		new DamageListener(this);
		GlobalArena.InitWorld(this);
	}
	
	@Override
	public void onDisable() {
		Data.SaveDatabase(this);
		this.getLogger().info("[Arena] v" + version + " has been disabled!");
	}
}
