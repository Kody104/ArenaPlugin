package com.gmail.jpk.stu.Entities;

import org.bukkit.entity.LivingEntity;

public class SlaveEntity {
	
	public enum SlaveTargetReason {
		PLAYERCOMMAND, PLAYERATTACKED;
	}
	
	private LivingEntity mEntity;
	private LivingEntity currentTarget;
	private SlaveTargetReason targetReason;
	
	public SlaveEntity(LivingEntity entity) {
		mEntity = entity;
	}
	
	public LivingEntity getMinecraftEntity() {
		return mEntity;
	}
	
	public void setCurrentTarget(LivingEntity le, SlaveTargetReason targetReason) {
		currentTarget = le;
		this.targetReason = targetReason;
	}
	
	public LivingEntity getCurrentTarget() {
		return currentTarget;
	}
	
	public SlaveTargetReason getTargetReason() {
		return targetReason;
	}
}
