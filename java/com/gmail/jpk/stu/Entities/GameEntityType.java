package com.gmail.jpk.stu.Entities;

public enum GameEntityType {
	
	REVERSAL, DEFENDER, RESISTOR, BEEFY, SHARPENED, HEIGHTENED,
	GUNKER, BLINDER, CONFUSER, POISONOUS, WITHEROUS,
	FEEBLER, HINDERER, DULLER, DIMINISHER;
	
	public String getName() {
		String toReturn = this.toString();
		String lower = this.toString().toLowerCase();
		toReturn = toReturn.substring(0, 1);
		toReturn += lower.substring(1);
		return toReturn;
	}
}
