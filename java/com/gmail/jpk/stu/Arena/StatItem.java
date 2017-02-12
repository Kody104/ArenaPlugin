package com.gmail.jpk.stu.Arena;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.jpk.stu.Entities.ClassRole;
import com.gmail.jpk.stu.Entities.GamePlayer;

public class StatItem extends ItemStack {

	private String tier;
	private ClassRole reqRole;
	private int reqLevel;

	private double atkAdd;
	private double magicAdd;
	private double defenseAdd;
	private double resistanceAdd;
	private double dodgeAdd;
	private double critAdd;
	
	public StatItem(Material material, GamePlayer player) {
		super(material);
		int roll = new Random().nextInt(100) + 1;
		if(roll < 4) {
			rollStats(player, "Legendary");
		}
		else if(roll < 8) {
			rollStats(player, "Epic");
		}
		else if(roll < 15) {
			rollStats(player, "Rare");
		}
		else if(roll < 31) {
			rollStats(player, "Uncommon");
		}
		else {
			rollStats(player, "Common");
		}
		createLore();
	}
	
	public StatItem(ItemStack item) {
		super(item.getType());
		atkAdd = 0.0d;
		magicAdd = 0.0d;
		defenseAdd = 0.0d;
		resistanceAdd = 0.0d;
		dodgeAdd = 0.0d;
		critAdd = 0.0d;
		
		ItemMeta meta = this.getItemMeta();
		List<String> itemLore = new ArrayList<String>();
		for(String s : item.getItemMeta().getLore()) {
			itemLore.add(s);
			if(s.equalsIgnoreCase("stat item")) {
				continue;
			}
			else if(s.toLowerCase().contains("lvl:")) {
				String number = s.substring(9);
				reqLevel = Integer.parseInt(number);
			}
			else if(s.toLowerCase().contains("role:")) {
				String role = s.substring(10);
				for(ClassRole c : ClassRole.values()) {
					if(c.toString().toLowerCase().equals(role.toLowerCase())) {
						reqRole = c;
						break;
					}
				}
				if(reqRole == null) {
					reqRole = ClassRole.ARCHER;
				}
			}
			else if(s.toLowerCase().contains("atk:")) {
				String number = s.substring(4);
				atkAdd  = Double.parseDouble(number);	
			}
			else if(s.toLowerCase().contains("magic:")) {
				String number = s.substring(6);
				magicAdd  = Double.parseDouble(number);	
			}
			else if(s.toLowerCase().contains("defense:")) {
				String number = s.substring(8);
				defenseAdd  = Double.parseDouble(number);	
			}
			else if(s.toLowerCase().contains("resistance:")) {
				String number = s.substring(11);
				resistanceAdd  = Double.parseDouble(number);	
			}
			else if(s.toLowerCase().contains("dodge:")) {
				String number = s.substring(6);
				dodgeAdd  = Double.parseDouble(number);	
			}
			else if(s.toLowerCase().contains("crit:")) {
				String number = s.substring(5);
				critAdd = Double.parseDouble(number);
			}
			else {
				if(tier == null) {
					tier = s;
				}
			}
		}
		meta.setLore(itemLore);
		setItemMeta(meta);
	}
	
	public void createLore() {
		ItemMeta meta = this.getItemMeta();
		if(meta.hasLore()) {
			meta.getLore().clear();
		}
		List<String> itemLore = new ArrayList<String>();
		itemLore.add("Stat Item");
		itemLore.add(tier);
		itemLore.add("Req Role: " + reqRole.toString());
		itemLore.add("Req Lvl: " + reqLevel);
		if(atkAdd > 0.0d) {
			itemLore.add("Atk: " + atkAdd);
		}
		if(magicAdd > 0.0d) {
			itemLore.add("Magic: " + magicAdd);
		}
		if(defenseAdd > 0.0d) {
			itemLore.add("Defense: " + defenseAdd);
		}
		if(resistanceAdd > 0.0d) {
			itemLore.add("Resistance: " + resistanceAdd);
		}
		if(dodgeAdd > 0.0d) {
			itemLore.add("Dodge: " + dodgeAdd);
		}
		if(critAdd > 0.0d) {
			itemLore.add("Crit: " + critAdd);
		}
		meta.setLore(itemLore);
		setItemMeta(meta);
	}
		
	public void rollStats(GamePlayer player, String tier) {
		int level;
		Random r = new Random();
		if(player.getMinecraftPlayer().getLevel() < 1) {
			level = 1;
		}
		else {
			level = player.getMinecraftPlayer().getLevel();
		}
		
		reqRole = player.getRole();
		reqLevel = level;
		this.tier = tier;
		
		atkAdd = 0.0d;
		magicAdd = 0.0d;
		defenseAdd = 0.0d;
		resistanceAdd = 0.0d;
		dodgeAdd = 0.0d;
		critAdd = 0.0d;
		
		double atkToAdd = getMinStatToAdd("atk", player) + (getMaxStatToAdd("atk", player) - getMinStatToAdd("atk", player)) * r.nextDouble();
		double magicToAdd = getMinStatToAdd("magic", player) + (getMaxStatToAdd("magic", player) - getMinStatToAdd("magic", player)) * r.nextDouble();
		double defenseToAdd = getMinStatToAdd("defense", player) + (getMaxStatToAdd("defense", player) - getMinStatToAdd("defense", player)) * r.nextDouble();
		double resistanceToAdd = getMinStatToAdd("resistance", player) + (getMaxStatToAdd("resistance", player) - getMinStatToAdd("resistance", player)) * r.nextDouble();
		double dodgeToAdd = getMinStatToAdd("dodge", player) + (getMaxStatToAdd("dodge", player) - getMinStatToAdd("dodge", player)) * r.nextDouble();
		double critToAdd = getMinStatToAdd("crit", player) + (getMaxStatToAdd("crit", player) - getMinStatToAdd("crit", player)) * r.nextDouble();
		
		double bonusAtk = 0.0d;
		double bonusMagic = 0.0d;
		double bonusDefense = 0.0d;
		double bonusResistance = 0.0d;
		double bonusDodge = 0.0d;
		double bonusCrit = 0.0d;
		
		double multi;
		
		switch(tier.toLowerCase()) {
			case "common":
				break;
			case "uncommon":
				multi = level * 1.25d;
				bonusAtk = multi * 0.0825d;
				bonusMagic = multi * 0.0825d;
				bonusDefense = multi * 0.0825d;
				bonusResistance = multi * 0.0825d;
				bonusDodge = multi * 0.0825d;
				bonusCrit = multi * 0.0825d;
				break;
			case "rare":
				multi = level * 1.5d;
				bonusAtk = multi * 0.12d;
				bonusMagic = multi * 0.12d;
				bonusDefense = multi * 0.12d;
				bonusResistance = multi * 0.12d;
				bonusDodge = multi * 0.12d;
				bonusCrit = multi * 0.12d;
				break;
			case "epic":
				multi = level * 1.75d;
				bonusAtk = multi * 0.17d;
				bonusMagic = multi * 0.17d;
				bonusDefense = multi * 0.17d;
				bonusResistance = multi * 0.17d;
				bonusDodge = multi * 0.17d;
				bonusCrit = multi * 0.17d;
				break;
			case "legendary":
				multi = level * 2.0d;
				bonusAtk = multi * 0.2325d;
				bonusMagic = multi * 0.2325d;
				bonusDefense = multi * 0.2325d;
				bonusResistance = multi * 0.2325d;
				bonusDodge = multi * 0.2325d;
				bonusCrit = multi * 0.2325d;
				break;
			default:
				break;
		}
		
		if(reqRole == ClassRole.ARCHER) { // Balance the archer
			bonusAtk /= 4.0d;
		}
		
		BigDecimal b;
		
		if(atkToAdd != 0.0d) {
			b = new BigDecimal(atkToAdd + bonusAtk).setScale(2, RoundingMode.HALF_EVEN);
			atkAdd += b.doubleValue();
		}
		if(magicToAdd != 0.0d) {
			b = new BigDecimal(magicToAdd + bonusMagic).setScale(2, RoundingMode.HALF_EVEN);
			magicAdd += b.doubleValue();
		}
		if(defenseToAdd != 0.0d) {
			b = new BigDecimal(defenseToAdd + bonusDefense).setScale(2, RoundingMode.HALF_EVEN);
			defenseAdd += b.doubleValue();
		}
		if(resistanceToAdd != 0.0d) {
			b = new BigDecimal(resistanceToAdd + bonusResistance).setScale(2, RoundingMode.HALF_EVEN);
			resistanceAdd += b.doubleValue();
		}
		if(dodgeToAdd != 0.0d) {
			b = new BigDecimal(dodgeToAdd + bonusDodge).setScale(2, RoundingMode.HALF_EVEN);
			dodgeAdd += b.doubleValue();
		}
		if(critToAdd != 0.0d) {
			b = new BigDecimal(critToAdd + bonusCrit).setScale(2, RoundingMode.HALF_EVEN);
			critAdd += b.doubleValue();
		}
		
		if(atkAdd < 0.06d) {
			atkAdd = 0.0d;
		}
		else if(atkAdd < 0.125d) {
			atkAdd = 0.125d;
		}
		if(magicAdd < 0.06d) {
			magicAdd = 0.0d;
		}
		else if(magicAdd < 0.125d) {
			magicAdd = 0.125d;
		}
		if(defenseAdd < 0.06d) {
			defenseAdd = 0.0d;
		}
		else if(defenseAdd < 0.125d) {
			defenseAdd = 0.125d;
		}
		if(resistanceAdd < 0.06d) {
			resistanceAdd = 0.0d;
		}
		else if(resistanceAdd < 0.125d) {
			resistanceAdd = 0.125d;
		}
		if(dodgeAdd < 0.06d) {
			dodgeAdd = 0.0d;
		}
		else if(dodgeAdd < 0.125d) {
			dodgeAdd = 0.125d;
		}
		if(critAdd < 0.06d) {
			critAdd = 0.0d;
		}
		else if(critAdd < 0.125d) {
			critAdd = 0.125d;
		}
		
		createLore();
	}
	
	public double getMaxStatToAdd(String statName, GamePlayer player) {
		double toReturn = 0.0d;
		switch(statName.toLowerCase()) {
			case "atk":
				if(player.getRole() == ClassRole.WARRIOR) {
					toReturn = (0.25d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.FIGHTER) {
					toReturn = (0.5d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MYSTIC) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.PALADIN) {
					toReturn = (0.25d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.ARCHER) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MAGE) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.NECROMANCER) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				break;
			case "magic":
				if(player.getRole() == ClassRole.WARRIOR) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.FIGHTER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MYSTIC) {
					toReturn = (0.5d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.PALADIN) {
					toReturn = (0.25d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.ARCHER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MAGE) {
					toReturn = (0.5d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.NECROMANCER) {
					toReturn = (0.25d * player.getMinecraftPlayer().getLevel());
				}
				else {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				break;
			case "defense":
				if(player.getRole() == ClassRole.WARRIOR) {
					toReturn = (0.5d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.FIGHTER) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MYSTIC) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.PALADIN) {
					toReturn = (0.25d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.ARCHER) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MAGE) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.NECROMANCER) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				break;
			case "resistance":
				if(player.getRole() == ClassRole.WARRIOR) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.FIGHTER) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MYSTIC) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.PALADIN) {
					toReturn = (0.25d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.ARCHER) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MAGE) {
					toReturn = (0.25d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.NECROMANCER) {
					toReturn = (0.25d * player.getMinecraftPlayer().getLevel());
				}
				else {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				break;
			case "dodge":
				if(player.getRole() == ClassRole.WARRIOR) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.FIGHTER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MYSTIC) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.PALADIN) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.ARCHER) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MAGE) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.NECROMANCER) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				break;
			case "crit":
				toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				break;
			default:
				break;
		}
		return toReturn;
	}
	
	public double getMinStatToAdd(String statName, GamePlayer player) {
		double toReturn = 0.0d;
		switch(statName.toLowerCase()) {
			case "atk":
				if(player.getRole() == ClassRole.WARRIOR) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.FIGHTER) {
					toReturn = (0.25d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MYSTIC) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.PALADIN) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.ARCHER) {
					toReturn = (0.0625d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MAGE) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.NECROMANCER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				break;
			case "magic":
				if(player.getRole() == ClassRole.WARRIOR) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.FIGHTER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MYSTIC) {
					toReturn = (0.25d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.PALADIN) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.ARCHER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MAGE) {
					toReturn = (0.25d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.NECROMANCER) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				break;
			case "defense":
				if(player.getRole() == ClassRole.WARRIOR) {
					toReturn = (0.25d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.FIGHTER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MYSTIC) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.PALADIN) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.ARCHER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MAGE) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.NECROMANCER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				break;
			case "resistance":
				if(player.getRole() == ClassRole.WARRIOR) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.FIGHTER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MYSTIC) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.PALADIN) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.ARCHER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MAGE) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.NECROMANCER) {
					toReturn = (0.125d * player.getMinecraftPlayer().getLevel());
				}
				else {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				break;
			case "dodge":
				if(player.getRole() == ClassRole.WARRIOR) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.FIGHTER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MYSTIC) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.PALADIN) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.ARCHER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.MAGE) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else if(player.getRole() == ClassRole.NECROMANCER) {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				else {
					toReturn = (0.0d * player.getMinecraftPlayer().getLevel());
				}
				break;
			case "crit":
				toReturn = (0.375d * player.getMinecraftPlayer().getLevel());
				break;
			default:
				break;
		}
		return toReturn;
	}
	
	public ClassRole getReqRole() {
		return reqRole;
	}
	
	public void setReqRole(ClassRole reqRole) {
		this.reqRole = reqRole;
	}
	
	public int getReqLevel() {
		return reqLevel;
	}

	public void setReqLevel(int reqLevel) {
		this.reqLevel = reqLevel;
	}
	
	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public double getAtkAdd() {
		return atkAdd;
	}

	public void setAtkAdd(double atkAdd) {
		this.atkAdd = atkAdd;
	}

	public double getMagicAdd() {
		return magicAdd;
	}

	public void setMagicAdd(double magicAdd) {
		this.magicAdd = magicAdd;
	}

	public double getDefenseAdd() {
		return defenseAdd;
	}

	public void setDefenseAdd(double defenseAdd) {
		this.defenseAdd = defenseAdd;
	}

	public double getResistanceAdd() {
		return resistanceAdd;
	}

	public void setResistanceAdd(double resistanceAdd) {
		this.resistanceAdd = resistanceAdd;
	}

	public double getDodgeAdd() {
		return dodgeAdd;
	}

	public void setDodgeAdd(double dodgeAdd) {
		this.dodgeAdd = dodgeAdd;
	}
	
	public double getCritAdd() {
		return critAdd;
	}
	
	public void setCritadd(double critAdd) {
		this.critAdd = critAdd;
	}
}
