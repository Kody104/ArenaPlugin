package com.gmail.jpk.stu.Entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.jpk.stu.Entities.StatusEffect.StatusEffects;

public class GameEntity {
	
	private LivingEntity mEntity;
	private List<StatusEffect> allStatusEffects = new ArrayList<StatusEffect>();
	private List<GameEntityType> types = new ArrayList<GameEntityType>();
	
	private int level;
	private double maxHp;
	private double hp;
	private double atk;
	private double magic;
	private double defense;
	private double resistance;
	
	public GameEntity(LivingEntity le, int level, int numOfTypes) {
		this.mEntity = le;
		setLevel(level);
		
		for(int i = 0; i < numOfTypes; i++) {
			GameEntityType get = GameEntityType.values()[new Random().nextInt(GameEntityType.values().length)];
			for(GameEntityType t : types) {
				if(t == get) {
					i--;
					break;
				}
			}
			types.add(get);
		}
		
		switch(le.getType()) {
			case SPIDER:
				setMaxHP(8.0d + (level * 8.0d));
				setHp(maxHp);
				setAtk(1.5d + (level * 1.5d));
				setMagic(1.0d + (level * 0.5d));
				setDefense(0.5d + (0.25d * level));
				setResistance(0.5d + (0.25d * level));
				break;
			case SKELETON:
				setMaxHP(10.0d + (level * 10.0d));
				setHp(maxHp);
				setAtk(2.5d + (level * 2.5d));
				setMagic(0.0d + (level * 0.0d));
				setDefense(0.5d + (level * 0.125d));
				setResistance(0.0d + (level * 0.125d));
				break;
			case ZOMBIE:
				setMaxHP(10.0d + (level * 10.0d));
				setHp(maxHp);
				setAtk(2.0d + (level * 2.0d));
				setMagic(1.0d + (level * 0.5d));
				setDefense(1.0d + (level * 1.0d));
				setResistance(1.0d + (level * 0.5d));
				break;
			case GIANT:
				setMaxHP(50.0d + (level * 50.0d));
				setHp(maxHp);
				setAtk(2.0d + (level * 2.0d));
				setMagic(1.0d + (level * 0.5d));
				setDefense(1.0d + (level * 1.0d));
				setResistance(1.0d + (level * 0.5d));
				break;
			case CAVE_SPIDER:
				setMaxHP(6.0d + (level * 6.0d));
				setHp(maxHp);
				setAtk(1.5d + (level * 1.5d));
				setMagic(1.0d + (level * 1.0d));
				setDefense(1.0d + (0.5d * level));
				setResistance(0.5d + (0.25d * level));
				break;
			case BLAZE:
				setMaxHP(15.0d + (level * 15.0d));
				setHp(maxHp);
				setAtk(4.5d + (level * 4.5d));
				setMagic(2.5d + (level * 2.5d));
				setDefense(2.5d + (1.5d * level));
				setResistance(1.5d + (0.5d * level));
				break;
			case GHAST:
				setMaxHP(10.0d + (level * 10.0d));
				setHp(maxHp);
				setAtk(0.5d + (level * 0.5d));
				setMagic(8.5d + (level * 8.5d));
				setDefense(0.5d + (0.25d * level));
				setResistance(10.0d + (5.0d * level));
				break;
			case CREEPER:
				setMaxHP(10.0d + (level * 10.0d));
				setHp(maxHp);
				setAtk(0.5d + (level * 0.5d));
				setMagic(4.0d + (level * 4.0d));
				setDefense(1.0d + (0.5d * level));
				setResistance(1.0d + (0.5d * level));
				break;
			case SILVERFISH:
				setMaxHP(4.0d + (level * 4.0d));
				setHp(maxHp);
				setAtk(2.5d + (level * 2.5d));
				setMagic(0.0d + (level * 0.0d));
				setDefense(0.0d + (0.125d * level));
				setResistance(0.0d + (0.125d * level));
				break;
			default:
				break;
		}
		if(hasType(GameEntityType.DEFENDER)) {
			setDefense(getDefense() + 17.5d);
		}
		if(hasType(GameEntityType.RESISTOR)) {
			setResistance(getResistance() + 17.5d);
		}
		if(hasType(GameEntityType.BEEFY)) {
			setMaxHP(getMaxHp() * 1.5d);
			setHp(getMaxHp());
		}
		if(hasType(GameEntityType.SHARPENED)) {
			setAtk(getAtk() * 1.5d);
		}
		if(hasType(GameEntityType.HEIGHTENED)) {
			setMagic(getMagic() * 1.5d);
		}
		mEntity.setCustomName("[Lvl " + level + "] " + types.get(0).getName());
		mEntity.setCustomNameVisible(true);
	}
	
	public GameEntity(Entity e) {
		this.mEntity = (LivingEntity) e;
	}
	
	public double getTakenDamage(double dmg, AttackType attack) {
		double multi;
		if(attack == AttackType.PHYSICAL) {
			multi = (1.0d - (getDefense() / 50.0d));
		}
		else {
			multi = (1.0d - (getResistance() / 50.0d));
		}
		dmg *= multi;
		return dmg;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setMaxHP(double maxHp) {
		this.maxHp = maxHp;
		mEntity.setMaxHealth(this.maxHp);
	}
	
	public double getMaxHp() {
		return maxHp;
	}
	
	public void setHp(double hp) {
		this.hp = hp;
		mEntity.setHealth(this.hp);
	}
	
	public double getHp() {
		return hp;
	}
	
	public void setAtk(double atk) {
		this.atk = atk;
	}
	
	public double getAtk() {
		return atk;
	}
	
	public void setMagic(double magic) {
		this.magic = magic;
	}
	
	public double getMagic() {
		return magic;
	}
	
	public void setDefense(double defense) {
		if(defense > 45.0d) {
			defense = 45.0d;
		}
		this.defense = defense;
	}
	
	public double getDefense() {
		return defense;
	}
	
	public void setResistance(double resistance) {
		if(resistance > 45.0d) {
			resistance = 45.0d;
		}
		this.resistance = resistance;
	}
	
	public double getResistance() {
		return resistance;
	}
	
	public List<StatusEffect> getAllStatusEffects() {
		return allStatusEffects;
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
		for(PotionEffect p : mEntity.getActivePotionEffects()) {
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
		for(PotionEffect p : mEntity.getActivePotionEffects()) {
			if(p.getType().getName().equalsIgnoreCase(type.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public void addPotionEffect(PotionEffect p) {
		for(int i = 0; i < mEntity.getActivePotionEffects().size(); i++) {
			PotionEffect effect = (PotionEffect) mEntity.getActivePotionEffects().toArray()[i];
			if(effect.getType().getName().equalsIgnoreCase(p.getType().getName())) {
				if(effect.getAmplifier() < p.getAmplifier()) {
					mEntity.removePotionEffect(effect.getType());
					mEntity.addPotionEffect(p);
					return;
				}
				else if(effect.getAmplifier() == p.getAmplifier()) {
					int duration = effect.getDuration() + p.getDuration();
					PotionEffectType type = effect.getType();
					int amplifier = effect.getAmplifier();
					mEntity.removePotionEffect(effect.getType());
					mEntity.addPotionEffect(new PotionEffect(type, duration, amplifier));
					return;
				}
				else {
					int diff = (effect.getAmplifier() - p.getAmplifier()) + 1;
					int duration = effect.getDuration() + (p.getDuration() / diff);
					PotionEffectType type = effect.getType();
					int amplifier = effect.getAmplifier();
					mEntity.removePotionEffect(effect.getType());
					mEntity.addPotionEffect(new PotionEffect(type, duration, amplifier));
					return;
				}
			}
		}
		mEntity.addPotionEffect(p);
	}
	
	public void removeStatusEffect(StatusEffects effect) {
		for(int i = 0; i < allStatusEffects.size(); i++) {
			if(allStatusEffects.get(i).getEffect() == effect) {
				allStatusEffects.remove(i);
				return;
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
	
	public LivingEntity getMinecraftEntity() {
		return mEntity;
	}
	
	public boolean hasType(GameEntityType type) {
		for(GameEntityType t : types) {
			if(t == type) {
				return true;
			}
		}
		return false;
	}

	public List<GameEntityType> getTypes() {
		return types;
	}

	public void setTypes(List<GameEntityType> types) {
		this.types = types;
	}
	
	
}
