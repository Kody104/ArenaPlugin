package com.gmail.jpk.stu.Commands;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.jpk.stu.Arena.Arena;
import com.gmail.jpk.stu.Arena.Data;
import com.gmail.jpk.stu.Arena.GlobalArena;
import com.gmail.jpk.stu.Arena.StatItem;
import com.gmail.jpk.stu.Entities.GamePlayer;

public class Forge extends BasicCommand{

	public Forge(Arena plugin) {
		super(plugin);
	}

	@Override
	public boolean performCommand(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(!Data.HasPlayer(p)) {
			p.sendMessage("The data hashmap doesn't contain this player. Contact an admin.");
			return true;
		}
		
		if(!Data.HasPlayerProperty(p, "vip")) {
			p.sendMessage("You aren't allowed to use this command unless you're a VIP player.");
			return true;
		}
		
		if(args.length < 1 || args.length > 2) {
			sender.sendMessage("Usage: /forge reforge");
			sender.sendMessage("or Usage: /forge <refine> [stat]");
			return true;
		}
		
		if(args.length == 1 && !args[0].equalsIgnoreCase("reforge")) {
			p.sendMessage("Didn't recognize command.");
			return true;
		}
		
		if(args.length == 2) {
			if(!args[0].equalsIgnoreCase("refine")) {
				p.sendMessage("Didn't recognize command.");
				return true;
			}
			switch(args[1].toLowerCase()) {
				case "atk":
					break;
				case "attack":
					break;
				case "magic":
					break;
				case "defense":
					break;
				case "resistance":
					break;
				case "dodge":
					break;
				case "critical":
					break;
				case "crit":
					break;
				default:
					p.sendMessage("Didn't recognize stat.");
					return true;
			}
		}
		
		if(GlobalArena.GetQueuePlayer(p) == null) {
			p.sendMessage("You must be in the arena to use this command.");
			return true;
		}
		
		GamePlayer gp = GlobalArena.GetQueuePlayer(p);
		
		if(!GlobalArena.isShopRound()) {
			p.sendMessage("You can only use this command in the shop.");
			return true;
		}
		
		if(!gp.canForgeNow()) {
			p.sendMessage("You've already used your forge for this shop.");
			return true;
		}
		
		if(p.getInventory().getItemInMainHand() != null) {
			if(p.getInventory().getItemInMainHand().hasItemMeta()) {
				if(gp.hasStatItem(p.getInventory().getItemInMainHand())) {
					StatItem s = gp.getStatItem(p.getInventory().getItemInMainHand());
					if(args.length == 1) {
						s.rollStats(gp, s.getTier());
						gp.addStatItem(s);
						gp.setCanForgeNow(false);
						p.getInventory().setItemInMainHand(s);
						p.sendMessage("Your item has been reforged!");
						return true;
					}
					else if(args.length == 2) {
						if(args[1].equalsIgnoreCase("atk") || args[1].equalsIgnoreCase("attack")) {
							s.setAtkAdd(s.getAtkAdd() + 1.8d);
						}
						else if(args[1].equalsIgnoreCase("magic")) {
							s.setMagicAdd(s.getMagicAdd() + 1.8d);
						}
						else if(args[1].equalsIgnoreCase("defense")) {
							s.setDefenseAdd(s.getDefenseAdd() + 1.8d);
						}
						else if(args[1].equalsIgnoreCase("resistance")) {
							s.setResistanceAdd(s.getResistanceAdd() + 1.8d);
						}
						else if(args[1].equalsIgnoreCase("dodge")) {
							s.setDodgeAdd(s.getDodgeAdd() + 1.8d);
						}
						else if(args[1].equalsIgnoreCase("critical") || args[1].equalsIgnoreCase("crit")) {
							s.setCritadd(s.getCritAdd() + 1.8d);
						}
						BigDecimal b = new BigDecimal(s.getAtkAdd() - 0.30d).setScale(2, RoundingMode.HALF_EVEN);
						s.setAtkAdd(b.doubleValue());
						b = new BigDecimal(s.getMagicAdd() - 0.30d).setScale(2, RoundingMode.HALF_EVEN);
						s.setMagicAdd(b.doubleValue());
						b = new BigDecimal(s.getDefenseAdd() - 0.30d).setScale(2, RoundingMode.HALF_EVEN);
						s.setDefenseAdd(b.doubleValue());
						b = new BigDecimal(s.getResistanceAdd() - 0.30d).setScale(2, RoundingMode.HALF_EVEN);
						s.setResistanceAdd(b.doubleValue());
						b = new BigDecimal(s.getDodgeAdd() - 0.30d).setScale(2, RoundingMode.HALF_EVEN);
						s.setDodgeAdd(b.doubleValue());
						b = new BigDecimal(s.getCritAdd() - 0.30d).setScale(2, RoundingMode.HALF_EVEN);
						s.setCritadd(b.doubleValue());
						s.createLore();
						gp.addStatItem(s);
						gp.setCanForgeNow(false);
						p.getInventory().setItemInMainHand(s);
						p.sendMessage("Your item has been refined! " + args[1] + " stat has been increased while all others have been decreased!");
						return true;
					}
					else {
						p.sendMessage("Argument length error. Contact an admin.");
						return true;
					}
				}
				else {
					p.sendMessage("If this is a stat item, contact an admin. Otherwise, this isn't a stat item.");
					return true;
				}
			}
			else {
				p.sendMessage("This isn't a stat item.");
				return true;
			}
		}
		
		return true;
	}

}
