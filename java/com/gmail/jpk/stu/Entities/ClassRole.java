package com.gmail.jpk.stu.Entities;

import org.bukkit.ChatColor;

import com.gmail.jpk.stu.Abilities.Ability;

public enum ClassRole {
	WARRIOR(new Ability("Bountiful Feast", 0, 0.0d, 2400, ChatColor.AQUA), new Ability("Taunt", 2, 0.0d, 150), new Ability("Defensive Paradigm", 4, 15.0d, 300, ChatColor.AQUA), new Ability("Victory Smash", 6, 1.25d, 350), new Ability("Vigour", 8, 37.5d, 500, ChatColor.AQUA), new Ability("Heroic Tank", 10, 0.0d, 800)),
	FIGHTER(new Ability("True Damager", 0, 0.05d, 0), new Ability("Rage", 2, 0.0d, 40), new Ability("Blood Rush", 4, 0.0d, 400, ChatColor.YELLOW, 4), new Ability("Lifesteal", 6, 0.5d, 100), new Ability("Fervor", 8, 0.0d, 250, ChatColor.AQUA), new Ability("Death Axe", 10, 0.0d, 150)),
	MYSTIC(new Ability("Summon Snowman", 0, 0.0d, 600), new Ability("Sanic's Blessing", 2, 1.0d, 300), new Ability("Mass Defense", 4, 1.0d, 350), new Ability("Light Burst", 6, 1.0d, 500), new Ability("Auria's Shielding", 8, 0.0d, 600), new Ability("Mystic Insight", 10, 0.0d, 1500)),
	PALADIN(new Ability("Mend", 0, 0.0d, 30), new Ability("Knight's Blessing", 2, 0.0d, 300), new Ability("Auria's Linking", 4, 0.0d, 99999), new Ability("Holy Shield", 6, 0.0d, 500), new Ability("Remedy", 8, 0.0d, 350), new Ability("Auria's Possession", 10, 0.0d, 650)),
	ARCHER(new Ability("Scavanger", 0, 0.0d, 900, ChatColor.AQUA), new Ability("Slow", 2, 1.0d, 100), new Ability("Wither", 4, 1.0d, 150), new Ability("Strike Weakness", 6, 1.5d, 350), new Ability("Woodman's Mark", 8, 0.5d, 300),  new Ability("Triple Shot", 10, 0.0d, 550)),
	MAGE(new Ability("Fire Charge", 0, 6.5d, 120), new Ability("Hex", 2, 5.0d, 340), new Ability("Mage's Exchange", 4, 0.0d, 300), new Ability("Nuke", 6, 10.0d, 400), new Ability("Firestorm", 8, 6.5d, 400), new Ability("Arcane Form", 10, 0.0d, 1200)),
	NECROMANCER(new Ability("Death's Grasp", 0, 0.0d, 99999, ChatColor.AQUA), new Ability("Summon Zombie", 4, 0.0d, 1200), new Ability("Summon Skeleton", 8, 0.0d, 1600), new Ability("Summon Witch", 12, 0.0d, 2000));
	
	private Ability[] ClassAbilities;
	
	ClassRole(Ability... abilities) {
		ClassAbilities = new Ability[abilities.length];
		for(int i = 0; i < abilities.length; i++) {
			ClassAbilities[i] = abilities[i];
		}
	}
	
	public Ability getAbility(int index) {
		return ClassAbilities[index];
	}
	
	public Ability getAbility(String name) {
		for(Ability a : ClassAbilities) {
			if(a.getName().toLowerCase().equals(name.toLowerCase())) {
				return a;
			}
		}
		return null;
	}
	
	public Ability[] getAllAbilities() {
		return ClassAbilities;
	}
}
