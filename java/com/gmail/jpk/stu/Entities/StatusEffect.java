package com.gmail.jpk.stu.Entities;

import org.bukkit.entity.LivingEntity;

public class StatusEffect {
	
	public enum StatusEffects {
		TAUNTED, CONFUSED, SOUL_LINK, INVULNERABLE, HEROIC_LINK, AURIA_POSSESSED, WOODMAN_MARK, FLIGHT,
		ARCANE;
	}
	
	private StatusEffects effect;
	private LivingEntity caster;
	private int duration;
	
	public StatusEffect(StatusEffects effect, int duration, LivingEntity caster) {
		this.effect = effect;
		this.caster = caster;
		this.duration = duration;
	}
	
	public StatusEffects getEffect() {
		return effect;
	}
	
	public LivingEntity getCaster() {
		return caster;
	}
	
	public int getDuration() {
		return duration;
	}
}
