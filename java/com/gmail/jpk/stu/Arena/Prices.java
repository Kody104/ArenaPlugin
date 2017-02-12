package com.gmail.jpk.stu.Arena;

public enum Prices {
	WOOD_SWORD(1), STONE_SWORD(3), IRON_SWORD(6), GOLD_SWORD(10), DIAMOND_SWORD(15),
	WOOD_AXE(1), STONE_AXE(3), IRON_AXE(6), GOLD_AXE(10), DIAMOND_AXE(15),
	BREAD(3), ARROW(3), SANDSTONE(3);
	
	private int price;
	
	Prices(int price) {
		this.price = price;
	}
	
	public int getPrice(){
		return price;
	}
}