package com.gmail.jpk.stu.Commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.jpk.stu.Arena.Arena;
import com.gmail.jpk.stu.Arena.GlobalArena;
import com.gmail.jpk.stu.Arena.LoreItem;
import com.gmail.jpk.stu.Entities.ClassRole;

public class Role extends BasicCommand{

	public Role(Arena plugin) {
		super(plugin);
	}

	@Override
	public boolean performCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You need to be a player to set your role!");
			return true;
		}
		if(args.length != 1) {
			sender.sendMessage("Usage: /role <class>");
			return true;
		}
		Player p = (Player) sender;
		if(GlobalArena.GetQueuePlayer(p) != null) {
			if(GlobalArena.getHasStarted() || GlobalArena.getRound() != 0) {
				p.sendMessage("You can't change your role after the Arena has started!");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("warrior") || args[0].equalsIgnoreCase("tank")) {
				GlobalArena.ResetPlayerBlocks(p);
				p.getInventory().setArmorContents(null);
				p.getInventory().clear();
				GlobalArena.GetQueuePlayer(p).setRole(ClassRole.WARRIOR);
				p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
				p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
				p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
				p.getInventory().addItem(new ItemStack(Material.WOOD_SWORD));
				p.getInventory().addItem(new ItemStack(Material.BREAD, 4));
			}
			else if (args[0].equalsIgnoreCase("fighter") || args[0].equalsIgnoreCase("beserker")) {
				GlobalArena.ResetPlayerBlocks(p);
				p.getInventory().setArmorContents(null);
				p.getInventory().clear();
				GlobalArena.GetQueuePlayer(p).setRole(ClassRole.FIGHTER);
				p.getInventory().addItem(new ItemStack(Material.STONE_AXE));
			}
			else if (args[0].equalsIgnoreCase("support") || args[0].equalsIgnoreCase("healer")) {
				p.sendMessage("The support class has been divided into 2 classes. (A) Mystic (B) Paladin");
			}
			else if(args[0].equalsIgnoreCase("mystic")) {
				GlobalArena.ResetPlayerBlocks(p);
				p.getInventory().setArmorContents(null);
				p.getInventory().clear();
				GlobalArena.GetQueuePlayer(p).setRole(ClassRole.MYSTIC);
				p.getInventory().addItem(new ItemStack(Material.WOOD_SWORD));
				p.getInventory().addItem(new ItemStack(Material.SANDSTONE, 16));
			}
			else if(args[0].equalsIgnoreCase("paladin")) {
				GlobalArena.ResetPlayerBlocks(p);
				p.getInventory().setArmorContents(null);
				p.getInventory().clear();
				GlobalArena.GetQueuePlayer(p).setRole(ClassRole.PALADIN);
				p.getInventory().addItem(new ItemStack(Material.WOOD_SWORD));
			}
			else if (args[0].equalsIgnoreCase("archer") || args[0].equalsIgnoreCase("thief")) {
				GlobalArena.ResetPlayerBlocks(p);
				p.getInventory().setArmorContents(null);
				p.getInventory().clear();
				GlobalArena.GetQueuePlayer(p).setRole(ClassRole.ARCHER);
				p.getInventory().addItem(new ItemStack(Material.BOW));
				p.getInventory().addItem(new ItemStack(Material.ARROW, 48));
			}
			else if(args[0].equalsIgnoreCase("mage") || args[0].equalsIgnoreCase("wizard")) {
				GlobalArena.ResetPlayerBlocks(p);
				p.getInventory().setArmorContents(null);
				p.getInventory().clear();
				GlobalArena.GetQueuePlayer(p).setRole(ClassRole.MAGE);
				LoreItem i = new LoreItem(Material.STICK, "Spell: Fire Charge");
				p.getInventory().addItem(i);
			}
			else if(args[0].equalsIgnoreCase("necromancer") || args[0].equalsIgnoreCase("conjurer")) {
				GlobalArena.ResetPlayerBlocks(p);
				p.getInventory().setArmorContents(null);
				p.getInventory().clear();
				GlobalArena.GetQueuePlayer(p).setRole(ClassRole.NECROMANCER);
				p.getInventory().addItem(new ItemStack(Material.WOOD_SWORD));
			}
			else {
				p.sendMessage("That's not a valid role!");
			}
		}
		else {
			p.sendMessage("You aren't apart of the Arena!");
			return true;
		}
		
		return true;
	}

}
