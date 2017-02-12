package com.gmail.jpk.stu.Entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.jpk.stu.Abilities.Ability;
import com.gmail.jpk.stu.Arena.GlobalArena;
import com.gmail.jpk.stu.Arena.StatItem;
import com.gmail.jpk.stu.Entities.StatusEffect.StatusEffects;

public class GamePlayer {
	
	private Player mPlayer;
	private Scoreboard currentScoreboard;
	private ClassRole role;
	private boolean isReady;
	private int score;
	private Location spawn;
	private Inventory pInventory;
	private List<SlaveEntity> allSummons = new ArrayList<SlaveEntity>();
	private List<Ability> allAbilities = new ArrayList<Ability>();
	private List<StatusEffect> allStatusEffects = new ArrayList<StatusEffect>();
	private List<Block> placedBlocks = new ArrayList<Block>();
	private List<StatItem> allStatItems = new ArrayList<StatItem>();
	private boolean canForgeNow;
	
	private double maxHp;
	private double hp;
	private double atk;
	private double magic;
	private double defense;
	private double resistance;
	private double dodge;
	private double critical;

	public GamePlayer(Player p) {
		this.mPlayer = p;
		setCurrentScoreboard(0);
		setCurrentScoreboard(2);
		setCurrentScoreboard(1);
		isReady = false;
		score = 0;
		spawn = p.getLocation();
		pInventory = p.getInventory();
		canForgeNow = false;
		
		maxHp = mPlayer.getMaxHealth();
		hp = maxHp;
		atk = 3.5d;
		magic = 1.0d;
		defense = 0.0d;
		resistance = 0.0d;
		dodge = 0.5d;
		critical = 0.0d;
	}
	
	public Player getMinecraftPlayer() {
		return mPlayer;
	}
	
	public double getTakenDamage(double dmg, AttackType attack) {
		double multi;
		if(attack == AttackType.PHYSICAL) {
			multi = (1.0d - (getDefense(false) / 50.0d));
		}
		else {
			multi = (1.0d - (getResistance(false) / 50.0d));
		}
		dmg *= multi;
		return dmg;
	}
	
	public void setMaxHP(double maxHp) {
		this.maxHp = maxHp;
		mPlayer.setMaxHealth(this.maxHp);
	}
	
	public double getMaxHp() {
		double toReturn = maxHp;
		return toReturn;
	}
	
	public void setHp(double hp) {
		this.hp = hp;
		mPlayer.setHealth(this.hp);
	}
	
	public double getHp() {
		return hp;
	}
	
	public void setAtk(double atk) {
		this.atk = atk;
	}
	
	public double getAtk(boolean isTrue) {
		double toReturn = atk;
		if(mPlayer.getInventory().getItemInMainHand() != null) {
			if(mPlayer.getInventory().getItemInMainHand().hasItemMeta()) {
				if(hasStatItem(mPlayer.getInventory().getItemInMainHand())) {
					StatItem s = getStatItem(mPlayer.getInventory().getItemInMainHand());
					if(s.getReqLevel() <= mPlayer.getLevel()) {
						toReturn += s.getAtkAdd();
					}
				}
			}
		}
		if(isTrue) {
			return atk;
		}
		return toReturn;
	}
	
	public void setMagic(double magic) {
		this.magic = magic;
	}
	
	public double getMagic(boolean isTrue) {
		double toReturn = magic;
		if(mPlayer.getInventory().getItemInMainHand() != null) {
			if(mPlayer.getInventory().getItemInMainHand().hasItemMeta()) {
				if(hasStatItem(mPlayer.getInventory().getItemInMainHand())) {
					StatItem s = getStatItem(mPlayer.getInventory().getItemInMainHand());
					if(s.getReqLevel() <= mPlayer.getLevel()) {
						toReturn += s.getMagicAdd();
					}
				}
			}
		}
		if(isTrue) {
			return magic;
		}
		return toReturn;
	}
	
	public void setDefense(double defense) {
		this.defense = defense;
	}
	
	public double getDefense(boolean isTrue) {
		
		double toReturn = defense;
		if(mPlayer.getInventory().getItemInMainHand() != null) {
			if(mPlayer.getInventory().getItemInMainHand().hasItemMeta()) {
				if(hasStatItem(mPlayer.getInventory().getItemInMainHand())) {
					StatItem s = getStatItem(mPlayer.getInventory().getItemInMainHand());
					if(s.getReqLevel() <= mPlayer.getLevel()) {
						toReturn += s.getDefenseAdd();
					}
				}
			}
		}
		if(isTrue) {
			return defense;
		}
		if(toReturn > 45.0d) {
			return 45.0d;
		}
		return toReturn;
	}
	
	public void setResistance(double resistance) {
		this.resistance = resistance;
	}
	
	public double getResistance(boolean isTrue) {
		double toReturn = resistance;
		if(mPlayer.getInventory().getItemInMainHand() != null) {
			if(mPlayer.getInventory().getItemInMainHand().hasItemMeta()) {
				if(hasStatItem(mPlayer.getInventory().getItemInMainHand())) {
					StatItem s = getStatItem(mPlayer.getInventory().getItemInMainHand());
					if(s.getReqLevel() <= mPlayer.getLevel()) {
						toReturn += s.getResistanceAdd();
					}
				}
			}
		}
		if(isTrue) {
			return resistance;
		}
		if(toReturn > 45.0d) {
			return 45.0d;
		}
		return toReturn;
	}
	
	public void setDodge(double dodge) {
		this.dodge = dodge;
	}
	
	public double getDodge(boolean isTrue) {
		double toReturn = dodge;
		if(mPlayer.getInventory().getItemInMainHand() != null) {
			if(mPlayer.getInventory().getItemInMainHand().hasItemMeta()) {
				if(hasStatItem(mPlayer.getInventory().getItemInMainHand())) {
					StatItem s = getStatItem(mPlayer.getInventory().getItemInMainHand());
					if(s.getReqLevel() <= mPlayer.getLevel()) {
						toReturn += s.getDodgeAdd();
					}
				}
			}
		}
		if(isTrue) {
			return dodge;
		}
		if(toReturn > 45.0d) {
			return 45.0d;
		}
		return toReturn;
	}
	
	public double getCritical(boolean isTrue) {
		double toReturn = critical;
		if(mPlayer.getInventory().getItemInMainHand() != null) {
			if(mPlayer.getInventory().getItemInMainHand().hasItemMeta()) {
				if(hasStatItem(mPlayer.getInventory().getItemInMainHand())) {
					StatItem s = getStatItem(mPlayer.getInventory().getItemInMainHand());
					if(s.getReqLevel() <= mPlayer.getLevel()) {
						toReturn += s.getCritAdd();
					}
				}
			}
		}
		if(isTrue) {
			return critical;
		}
		return toReturn;
	}

	public void setCritical(double critical) {
		this.critical = critical;
	}
	
	public int getCriticalChance() {
		int crit = (int) (Math.floor((1.0 - (getCritical(false) / 100.0d)) * 100.0d));
		return crit;
	}
	
	public int getDodgeChance() {
		int dodge = (int) (Math.floor((1.0 - (getDodge(false) / 50.0d)) * 100.0d));
		return dodge;
	}
	
	public Scoreboard getCurrentScoreboard() {
		return currentScoreboard;
	}
	
	public void setCurrentScoreboard(int select) {
		currentScoreboard = GlobalArena.getScoreboard(select);
		mPlayer.setScoreboard(currentScoreboard);
		if(select == 0) {
			mPlayer.setLevel(mPlayer.getLevel());
		}
		else if(select == 1) {
			currentScoreboard.getObjective("showscore").getScore(mPlayer.getName()).setScore(score);
		}
		else if(select == 2) {
			mPlayer.setHealth(mPlayer.getHealth());
		}
	}
	
	public void setRole(ClassRole role) {
		this.role = role;
		allAbilities.clear();
		for(Ability a : role.getAllAbilities()) {
			if(a.isStackable()) {
				allAbilities.add(new Ability(a.getName(), a.getReqLvl(), a.getPow(), a.getCooldown(), a.getColor(), a.getMaxStacks()));
			}
			else if(a.hasCustomColor()) {
				allAbilities.add(new Ability(a.getName(), a.getReqLvl(), a.getPow(), a.getCooldown(), a.getColor()));
			}
			else {
				allAbilities.add(new Ability(a.getName(), a.getReqLvl(), a.getPow(), a.getCooldown()));
			}
		}
		switch(role) {
			case ARCHER:
				setMaxHP(20.0d);
				setAtk(1.5d); // Multiplied by 4 per arrow. 6.0 damage
				setMagic(0.0d);
				setDefense(1.5d);
				setResistance(1.5d);
				setDodge(1.5d);
				break;
			case FIGHTER:
				setMaxHP(20.0d);
				setAtk(3.0d);
				setMagic(0.0d);
				setDefense(2.0d);
				setResistance(0.0d);
				setDodge(1.0d);
				break;
			case MAGE:
				setMaxHP(20.0d);
				setAtk(1.5d);
				setMagic(4.5d);
				setDefense(0.0d);
				setResistance(0.0d);
				setDodge(0.0d);
				break;
			case MYSTIC:
				setMaxHP(20.0d);
				setAtk(2.0d);
				setMagic(3.5d);
				setDefense(0.0d);
				setResistance(0.5d);
				setDodge(0.0d);
				break;
			case NECROMANCER:
				setMaxHP(20.0d);
				setAtk(3.0d);
				setMagic(2.0d);
				setDefense(0.0d);
				setResistance(1.0d);
				setDodge(0.0d);
				break;
			case PALADIN:
				setMaxHP(20.0d);
				setAtk(2.5d);
				setMagic(1.0d);
				setDefense(1.5d);
				setResistance(1.0d);
				setDodge(0.0d);
				break;
			case WARRIOR:
				setMaxHP(20.0d);
				setAtk(3.0d);
				setMagic(0.0d);
				setDefense(1.5d);
				setResistance(1.5d);
				setDodge(0.0d);
				break;
			default:
				setMaxHP(20.0d);
				setAtk(4.0d);
				setMagic(2.0d);
				setDefense(0.0d);
				setResistance(0.0d);
				setDodge(0.0d);
				break;
		}
	}
	
	public ClassRole getRole() {
		return role;
	}
	
	public void setIsReady(boolean isReady) {
		this.isReady = isReady;
	}
	
	public boolean getIsReady() {
		return isReady;
	}
	
	public int getScore() {
		return score;
	}
	
	public void addScore(int add) {
		score += add;
		if(GlobalArena.getScoreboard(1) == currentScoreboard) {
			currentScoreboard.getObjective("showscore").getScore(mPlayer.getName()).setScore(score);
		}
	}
	
	public void resetScore() {
		score = 0;
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public Inventory getInventory() {
		return pInventory;
	}
	
	public boolean canForgeNow() {
		return canForgeNow;
	}

	public void setCanForgeNow(boolean canForgeNow) {
		this.canForgeNow = canForgeNow;
	}

	public void addSummon(LivingEntity le) {
		for(int i = 0; i < allSummons.size(); i++) {
			if(allSummons.get(i).getMinecraftEntity().getType() == le.getType()) {
				return;
			}
		}
		allSummons.add(new SlaveEntity(le));
	}
	
	public void removeSummon(LivingEntity le) {
		for(int i = 0; i < allSummons.size(); i++) {
			if(allSummons.get(i).getMinecraftEntity().getType() == le.getType()) {
				allSummons.remove(i);
				return;
			}
		}
	}
	
	public SlaveEntity getEntityInSummon(EntityType e) {
		for(int i = 0; i < allSummons.size(); i++) {
			if(allSummons.get(i).getMinecraftEntity().getType() == e) {
				return allSummons.get(i);
			}
		}
		return null;
	}
	
	public boolean hasEntityInSummon(LivingEntity le) {
		for(int i = 0; i < allSummons.size(); i++) {
			if(allSummons.get(i).getMinecraftEntity().getType() == le.getType()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasEntityInSummon(EntityType e) {
		for(int i = 0; i < allSummons.size(); i++) {
			if(allSummons.get(i).getMinecraftEntity().getType() == e) {
				return true;
			}
		}
		return false;
	}
	
	public List<SlaveEntity> getAllSummons() {
		return allSummons;
	}
	
	public Ability getAbility(int index) {
		return allAbilities.get(index);
	}
	
	public List<Ability> getAllAbilities() {
		return allAbilities;
	}
	
	public Ability getAbility(String Name) {
		for(Ability a : allAbilities) {
			if(a.getName().equalsIgnoreCase(Name)) {
				return a;
			}
		}
		return null;
	}
	
	public void addBlock(Block b) {
		placedBlocks.add(b);
	}
	
	public boolean checkRemoveBlock(Block b) {
		for(int i = 0; i < placedBlocks.size(); i++) {
			if(placedBlocks.get(i).getX() == b.getX() && placedBlocks.get(i).getY() == b.getY() && placedBlocks.get(i).getZ() == b.getZ()) {
				placedBlocks.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public List<Block> getPlacedBlocks() {
		return placedBlocks;
	}
	
	public boolean addStatItem(StatItem i) {
		for(StatItem s : allStatItems) {
			if(s == i) {
				return false;
			}
		}
		allStatItems.add(i);
		return true;
	}
	
	public List<StatItem> getAllStatItems() {
		return allStatItems;
	}
	
	public List<StatusEffect> getAllStatusEffects() {
		return allStatusEffects;
	}
	
	public boolean hasStatItem(ItemStack item) {
		boolean hasItem = false;
		
		for(int i = 0; i < allStatItems.size(); i++) {
			StatItem s = allStatItems.get(i);
			if(s.getItemMeta().getLore().size() == item.getItemMeta().getLore().size()) {
				for(int x = 0; x < item.getItemMeta().getLore().size(); x++) {
					String statLore = s.getItemMeta().getLore().get(x);
					String itemLore = item.getItemMeta().getLore().get(x);
					if(!statLore.equals(itemLore)) {
						break;
					}
					
					if(x == item.getItemMeta().getLore().size() - 1) {
						return true;
					}
				}
			}
		}
		return hasItem;
	}
	
	public StatItem getStatItem(ItemStack item) {
		for(int i = 0; i < allStatItems.size(); i++) {
			StatItem s = allStatItems.get(i);
			if(s.getItemMeta().getLore().size() == item.getItemMeta().getLore().size()) {
				for(int x = 0; x < item.getItemMeta().getLore().size(); x++) {
					String statLore = s.getItemMeta().getLore().get(x);
					String itemLore = item.getItemMeta().getLore().get(x);
					if(!statLore.equals(itemLore)) {
						break;
					}
					if(x == item.getItemMeta().getLore().size() - 1) {
						return s;
					}
				}
			}
		}
		return null;
	}
	
	public StatusEffect getStatusEffect(StatusEffects effect) {
		if(hasStatusEffect(effect)) {
			for(int i = 0; i < allStatusEffects.size(); i++) {
				if(allStatusEffects.get(i).getEffect() == effect) {
					return allStatusEffects.get(i);
				}
			}
		}
		return null;
	}
	
	public void removeStatusEffect(StatusEffects effect) {
		for(int i = 0; i < allStatusEffects.size(); i++) {
			if(allStatusEffects.get(i).getEffect() == effect) {
				allStatusEffects.remove(i);
				i--;
			}
		}
	}
	
	public boolean hasStatusEffect(StatusEffects effect) {
		for(int i = 0; i < allStatusEffects.size(); i++) {
			if(allStatusEffects.get(i).getEffect() == effect) {
				return true;
			}
		}
		return false;
	}
	
	public void addStatusEffect(StatusEffect effect) {
		if(hasStatusEffect(effect.getEffect())) {
			for(int i = 0; i < allStatusEffects.size(); i++) {
				if(allStatusEffects.get(i).getEffect() == effect.getEffect()) {
					int duration = allStatusEffects.get(i).getDuration() + effect.getDuration();
					removeStatusEffect(effect.getEffect());
					allStatusEffects.add(new StatusEffect(effect.getEffect(), duration, effect.getCaster()));
					return;
				}
			}
		}
		else {
			allStatusEffects.add(effect);
		}
	}
	
	public Collection<PotionEffect> getHarmfulPotionEffects() {
		Collection<PotionEffect> c = new ArrayList<PotionEffect>();
		for(PotionEffect p : mPlayer.getActivePotionEffects()) {
			if(p.getType() == PotionEffectType.BLINDNESS || p.getType() == PotionEffectType.CONFUSION || 
					p.getType() == PotionEffectType.HARM || p.getType() == PotionEffectType.HUNGER || 
					p.getType() == PotionEffectType.POISON || p.getType() == PotionEffectType.SLOW || 
					p.getType() == PotionEffectType.SLOW_DIGGING || p.getType() == PotionEffectType.UNLUCK || 
					p.getType() == PotionEffectType.WEAKNESS || p.getType() == PotionEffectType.WITHER) {
				c.add(p);
			}
		}
		return c;
	}
	
	public boolean hasPotionEffect(PotionEffectType type) {
		for(PotionEffect p : mPlayer.getActivePotionEffects()) {
			if(p.getType().getName().equalsIgnoreCase(type.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public void addPotionEffect(PotionEffect p) {
		for(int i = 0; i < mPlayer.getActivePotionEffects().size(); i++) {
			PotionEffect effect = (PotionEffect) mPlayer.getActivePotionEffects().toArray()[i];
			if(effect.getType().getName().equalsIgnoreCase(p.getType().getName())) {
				if(effect.getAmplifier() < p.getAmplifier()) {
					mPlayer.removePotionEffect(effect.getType());
					mPlayer.addPotionEffect(p);
					return;
				}
				else if(effect.getAmplifier() == p.getAmplifier()) {
					int duration = effect.getDuration() + p.getDuration();
					PotionEffectType type = effect.getType();
					int amplifier = effect.getAmplifier();
					mPlayer.removePotionEffect(effect.getType());
					mPlayer.addPotionEffect(new PotionEffect(type, duration, amplifier));
					return;
				}
				else {
					int diff = (effect.getAmplifier() - p.getAmplifier()) + 1;
					int duration = effect.getDuration() + (p.getDuration() / diff);
					PotionEffectType type = effect.getType();
					int amplifier = effect.getAmplifier();
					mPlayer.removePotionEffect(effect.getType());
					mPlayer.addPotionEffect(new PotionEffect(type, duration, amplifier));
					return;
				}
			}
		}
		mPlayer.addPotionEffect(p);
	}
	
	public void removePotionEffect(PotionEffect p) {
		for(int i = 0; i < mPlayer.getActivePotionEffects().size(); i++) {
			PotionEffect effect = (PotionEffect) mPlayer.getActivePotionEffects().toArray()[i];
			if(effect.getType().getName().equalsIgnoreCase(p.getType().getName())) {
				mPlayer.removePotionEffect(p.getType());
				return;
			}
		}
	}
}
