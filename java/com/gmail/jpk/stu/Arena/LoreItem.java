package com.gmail.jpk.stu.Arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoreItem extends ItemStack {
	
	public LoreItem(Material material, String lore) {
		super(material);
		ItemMeta meta = this.getItemMeta();
		List<String> itemLore = new ArrayList<String>();
		itemLore.add(lore);
		meta.setLore(itemLore);
		setItemMeta(meta);
	}
}
