package com.gmail.jpk.stu.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.jpk.stu.Abilities.Ability;
import com.gmail.jpk.stu.Arena.Arena;
import com.gmail.jpk.stu.Arena.Data;
import com.gmail.jpk.stu.Arena.GlobalArena;
import com.gmail.jpk.stu.Arena.LoreItem;
import com.gmail.jpk.stu.Arena.Prices;
import com.gmail.jpk.stu.Arena.StatItem;
import com.gmail.jpk.stu.Entities.GameEntity;
import com.gmail.jpk.stu.Entities.GamePlayer;
import com.gmail.jpk.stu.Entities.ClassRole;
import com.gmail.jpk.stu.Entities.SlaveEntity;
import com.gmail.jpk.stu.Entities.StatusEffect.StatusEffects;

public class AllListener implements Listener {
	private Arena plugin;
	
	public AllListener(Arena plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		
		if(!Data.HasPlayer(p)) {
			Data.GetPlayerDatabase().put(p.getUniqueId(), new HashMap<String, Boolean>());
			Data.AddPropertyToPlayer(p, "vip", false);
		}
	}
	
	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent e) {
		if(!(e.getSpawnReason() == SpawnReason.CUSTOM || e.getSpawnReason() == SpawnReason.SPAWNER_EGG)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemBreak(PlayerItemBreakEvent e) {
		Player p = e.getPlayer();
		if(GlobalArena.GetQueuePlayer(p) != null) {
			GamePlayer gp = GlobalArena.GetQueuePlayer(p);
			ItemStack newItem = new ItemStack(e.getBrokenItem().getType());
			if(e.getBrokenItem().hasItemMeta()) {
				if(e.getBrokenItem().getItemMeta().hasLore()) {
					ItemMeta meta = newItem.getItemMeta();
					List<String> lore = new ArrayList<String>();
					for(String s : e.getBrokenItem().getItemMeta().getLore()) {
						lore.add(s);
					}
					meta.setLore(lore);
					newItem.setItemMeta(meta);
					if(newItem.getItemMeta().getLore().get(0).equalsIgnoreCase("stat item")) {
						StatItem s = new StatItem(newItem);
						if(s.getReqRole() != gp.getRole()) {
							p.sendMessage("This weapon isn't for your role!");
							return;
						}
						gp.addStatItem(s);
					}
				}
			}
			p.getInventory().addItem(newItem);
		}
	}
	
	@EventHandler
	public void onPlayerBlockBreak(BlockBreakEvent e) {
		if(GlobalArena.GetQueuePlayer(e.getPlayer()) != null) {
			GamePlayer gp = GlobalArena.GetQueuePlayer(e.getPlayer());
			if(gp.checkRemoveBlock(e.getBlock())) {
					if(e.getBlock().getType() == Material.SANDSTONE) {
						e.getPlayer().getInventory().addItem(new ItemStack(Material.SANDSTONE));
					}
			}
			else {
				e.setCancelled(true);
			}
		}
		else if(GlobalArena.GetSpectatorPlayer(e.getPlayer()) != null) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerConsumeItem(PlayerItemConsumeEvent e) {
		if(GlobalArena.GetQueuePlayer(e.getPlayer()) != null) {
			if(e.getItem().getType() == Material.BREAD) {
				GamePlayer gp = GlobalArena.GetQueuePlayer(e.getPlayer());
				if(gp.getRole() == ClassRole.WARRIOR) {
					if((e.getPlayer().getLevel() >= gp.getAbility("bountiful feast").getReqLvl()) && !(gp.getAbility("bountiful feast").getIsActive())) { //Sets the bountiful feast ability to active
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("bountiful feast"));
					}
					else {
						gp.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
					}
				}
				else {
					gp.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		if(GlobalArena.GetQueuePlayer(e.getPlayer()) != null) {
			GamePlayer gp = GlobalArena.GetQueuePlayer(e.getPlayer());
			ItemStack item = e.getItem().getItemStack();
			if(item.hasItemMeta()) {
				if(item.getItemMeta().getLore().get(0).equalsIgnoreCase("stat item")) {
					StatItem s = new StatItem(item);
					if(s.getReqRole() != gp.getRole()) {
						e.getPlayer().sendMessage("This weapon isn't for your role!");
						return;
					}
					gp.addStatItem(s);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(GlobalArena.GetQueuePlayer(p) != null) {
			GlobalArena.RemoveQueuePlayer(p);
			p.setMaxHealth(20.0d);
			p.setFoodLevel(20);
			GlobalArena.toQueuePlayers(p.getDisplayName() + " has left the queue!");
			if(GlobalArena.getPlayersInQueue().size() == 0) {
				GlobalArena.setHasStarted(false);
				GlobalArena.ResetArena();
				plugin.getServer().broadcastMessage("The Arena's challengers have failed! It is available to rejoin!");
			}
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		if(e.getEntityType() == EntityType.CREEPER) {
			LivingEntity le = (LivingEntity) e.getEntity();
			if(GlobalArena.GetGameEntity(le) != null) {
				GlobalArena.addExplodedBlocks(e.blockList());
				GameEntity ge = GlobalArena.GetGameEntity(le);
				GlobalArena.getCreaturesInArena().remove(ge);
				if(GlobalArena.getCreaturesInArena().size() <= 0) {
					GlobalArena.toQueuePlayers("The last monster of the round has died!");
					if(!GlobalArena.isShopRound()) {
						new GlobalArena.DelayRoundStartTask().runTaskLater(plugin, 60);
					}
					else {
						GlobalArena.PlayerShopTele();
						GlobalArena.toQueuePlayers("Shop activiating in 10 seconds...");
						GlobalArena.setHasStarted(false);
						for(GamePlayer gp : GlobalArena.getPlayersInQueue()) {
							if(Data.HasPlayer(gp.getMinecraftPlayer())) {
								if(Data.HasPlayerProperty(gp.getMinecraftPlayer(), "vip")) {
									gp.setCanForgeNow(true);
								}
							}
							else {
								plugin.getLogger().info("ERROR: " + gp.getMinecraftPlayer().getName() + " isn't in the hashtable.");
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if(GlobalArena.GetGameEntity(e.getEntity()) != null) {
			GameEntity ge = GlobalArena.GetGameEntity(e.getEntity());
			LivingEntity le = e.getEntity();
			e.getDrops().clear();
			int dropChance = 15;
			switch(le.getType()) {
			case BLAZE:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 4));
				dropChance = 25;
				break;
			case CAVE_SPIDER:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 2));
				break;
			case CREEPER:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 3));
				break;
			case ENDERMAN:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 3));
				break;
			case ENDER_DRAGON:
				e.getDrops().add(new ItemStack(Material.GOLD_INGOT, 3));
				break;
			case GHAST:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 4));
				dropChance = 30;
				break;
			case GIANT:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 7));
				dropChance = 30;
				break;
			case MAGMA_CUBE:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 2));
				break;
			case PIG_ZOMBIE:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 3));
				break;
			case SILVERFISH:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET));
				break;
			case SKELETON:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 4));
				e.setDroppedExp(7);
				break;
			case SLIME:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 3));
				break;
			case SPIDER:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET));
				break;
			case WITCH:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 3));
				break;
			case WITHER:
				e.getDrops().add(new ItemStack(Material.GOLD_INGOT, 1));
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 6));
				break;
			case ZOMBIE:
				e.getDrops().add(new ItemStack(Material.GOLD_NUGGET, 2));
				break;
			default:
				break;
			}
			
			if((new Random().nextInt(100) + 1) < dropChance) { 
				int selection = new Random().nextInt(GlobalArena.getPlayersInQueue().size());
				GamePlayer gp = GlobalArena.getPlayersInQueue().get(selection);
				if(gp.getRole() == ClassRole.ARCHER) {
					e.getDrops().add(new StatItem(Material.BOW, gp));
				}
				else if(gp.getRole() == ClassRole.FIGHTER) {
					StatItem item = new StatItem(Material.WOOD, gp);
					switch(item.getTier().toLowerCase()) {
						case "common":
							item.setType(Material.WOOD_AXE);
							break;
						case "uncommon":
							item.setType(Material.STONE_AXE);
							break;
						case "rare":
							item.setType(Material.IRON_AXE);
							break;
						case "epic":
							item.setType(Material.GOLD_AXE);
							break;
						case "legendary":
							item.setType(Material.DIAMOND_AXE);
							break;
						default:
							item.setType(Material.WOOD);
							break;
					}
					e.getDrops().add(item);
				}
				else {
					StatItem item = new StatItem(Material.WOOD, gp);
					switch(item.getTier().toLowerCase()) {
						case "common":
							item.setType(Material.WOOD_SWORD);
							break;
						case "uncommon":
							item.setType(Material.STONE_SWORD);
							break;
						case "rare":
							item.setType(Material.IRON_SWORD);
							break;
						case "epic":
							item.setType(Material.GOLD_SWORD);
							break;
						case "legendary":
							item.setType(Material.DIAMOND_SWORD);
							break;
						default:
							item.setType(Material.WOOD);
							break;
					}
					e.getDrops().add(item);
				}
			}
			GlobalArena.getCreaturesInArena().remove(ge);
			if(GlobalArena.getCreaturesInArena().size() <= 0) {
				GlobalArena.toQueuePlayers("The last monster of the round has died!");
				if(!(GlobalArena.isShopRound())) {
					new GlobalArena.DelayRoundStartTask().runTaskLater(plugin, 60);
				}
				else {
					GlobalArena.PlayerShopTele();
					GlobalArena.toQueuePlayers("Shop activiating in 10 seconds...");
					GlobalArena.setHasStarted(false);
					for(GamePlayer gp : GlobalArena.getPlayersInQueue()) {
						if(Data.HasPlayer(gp.getMinecraftPlayer())) {
							if(Data.HasPlayerProperty(gp.getMinecraftPlayer(), "vip")) {
								gp.setCanForgeNow(true);
							}
						}
						else {
							plugin.getLogger().info("ERROR: " + gp.getMinecraftPlayer().getName() + " isn't in the hashtable.");
						}
					}
				}
			}
		}
		else {
			for(int i = 0; i < GlobalArena.getPlayersInQueue().size(); i++) {
				for(SlaveEntity se : GlobalArena.getPlayersInQueue().get(i).getAllSummons()) {
					if(e.getEntity() == se.getMinecraftEntity()) {
						GlobalArena.getPlayersInQueue().get(i).removeSummon(e.getEntity());
						return;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if(GlobalArena.GetQueuePlayer(p) != null) {
			GamePlayer gp = GlobalArena.GetQueuePlayer(p);
			if(gp.getRole() == ClassRole.NECROMANCER) {
				if((p.getLevel() >= gp.getAbility("death's grasp").getReqLvl()) && !(gp.getAbility("death's grasp").getIsActive())) {
					GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("death's grasp"));
					return;
				}
			}
			e.setDeathMessage(p.getDisplayName() + " has died! They're out of the Arena!");
			e.getDrops().clear();
			GlobalArena.RemoveQueuePlayer(p);
			p.setMaxHealth(20.0d);
			p.setFoodLevel(20);
			gp.resetScore();
			p.setScoreboard(GlobalArena.getScoreboardManager().getNewScoreboard()); // Resets the scoreboard to nothing
			if(GlobalArena.getPlayersInQueue().size() == 0) {
				GlobalArena.setHasStarted(false);
				GlobalArena.ResetArena();
				plugin.getServer().broadcastMessage("The Arena's challengers have failed! It is available to rejoin!");
			}
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent e) {
		if(e.getEntity() instanceof Creature) {
			LivingEntity le = (LivingEntity) e.getEntity();
			Creature c = (Creature) e.getEntity();
			if(GlobalArena.GetGameEntity(le) != null) {
				GameEntity ge = GlobalArena.GetGameEntity(le);
				if(e.getTarget() instanceof Player) {
					Player p = (Player) e.getTarget();
					if(GlobalArena.GetSpectatorPlayer(p) != null) {
						e.setTarget(null);
						c.setTarget(null);
						return;
					}
				}
				
				if(ge.hasStatusEffect(StatusEffects.CONFUSED)) {
					e.setTarget(null);
					c.setTarget(null);
				}
				else if(ge.hasStatusEffect(StatusEffects.TAUNTED)) {
					e.setTarget(ge.getStatusEffect(StatusEffects.TAUNTED).getCaster());
					c.setTarget(ge.getStatusEffect(StatusEffects.TAUNTED).getCaster());
				}
			}
			else {
				for(int i = 0; i < GlobalArena.getPlayersInQueue().size(); i++) {
					for(SlaveEntity se: GlobalArena.getPlayersInQueue().get(i).getAllSummons()) {
						if(le == se.getMinecraftEntity()) {
							e.setTarget(se.getCurrentTarget());
							c.setTarget(se.getCurrentTarget());
							return;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
		if(e.getEntity() instanceof Creature) {
			LivingEntity le = (LivingEntity) e.getEntity();
			Creature c = (Creature) e.getEntity();
			if(GlobalArena.GetGameEntity(le) != null) {
				GameEntity ge = GlobalArena.GetGameEntity(le);
				if(e.getTarget() instanceof Player) {
					Player p = (Player) e.getTarget();
					if(GlobalArena.GetSpectatorPlayer(p) != null) {
						e.setTarget(null);
						c.setTarget(null);
						return;
					}
				}
				
				if(ge.hasStatusEffect(StatusEffects.CONFUSED)) {
					e.setTarget(null);
					c.setTarget(null);
				}
				else if(ge.hasStatusEffect(StatusEffects.TAUNTED)) {
					e.setTarget(ge.getStatusEffect(StatusEffects.TAUNTED).getCaster());
					c.setTarget(ge.getStatusEffect(StatusEffects.TAUNTED).getCaster());
				}
			}
			else {
				for(int i = 0; i < GlobalArena.getPlayersInQueue().size(); i++) {
					for(SlaveEntity se : GlobalArena.getPlayersInQueue().get(i).getAllSummons()) {
						if(le == se.getMinecraftEntity()) {
							e.setTarget(se.getCurrentTarget());
							c.setTarget(se.getCurrentTarget());
							return;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		
		if(GlobalArena.GetQueuePlayer(p) != null) {
			GamePlayer gp = GlobalArena.GetQueuePlayer(p);
			gp.addBlock(e.getBlock());
			if(gp.getRole() != null) {
				if(gp.getRole() == ClassRole.MYSTIC) {
					gp.addScore(1);
				}
			}
		}
		else if(GlobalArena.GetSpectatorPlayer(p) != null) {
			e.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void onPlayerLaunchArrow(ProjectileLaunchEvent e) {
		if(e.getEntityType() == EntityType.ARROW) {
			Arrow arrow = (Arrow) e.getEntity();
			if(arrow.getShooter() instanceof Player) {
				Player p = (Player) arrow.getShooter();
				if(GlobalArena.GetQueuePlayer(p) != null) {
					GamePlayer gp = GlobalArena.GetQueuePlayer(p);
					if(gp.getRole() == ClassRole.ARCHER) {
						if((p.getLevel() >= gp.getAbility("scavanger").getReqLvl()) && !(gp.getAbility("scavanger").getIsActive())) { //Sets the scavanger ability to active
							GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("scavanger"));
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		if(e.getPlayer() instanceof Player) {
			Player p = (Player) e.getPlayer();
			if(GlobalArena.GetQueuePlayer(p) != null) {
				if(e.getInventory().getHolder() instanceof Chest) {
					Chest c = (Chest) e.getInventory().getHolder();
					ItemStack[] all = new ItemStack[13]; //Total = 13
					
					//Wood Sword
					all[0] = new ItemStack(Material.WOOD_SWORD);
					ItemMeta meta = all[0].getItemMeta();
					List<String> itemLore = new ArrayList<String>();
					itemLore.add(String.format("Price: %d gold bar(s)", Prices.WOOD_SWORD.getPrice()));
					itemLore.add(String.format("Common Tier"));
					meta.setLore(itemLore);
					all[0].setItemMeta(meta);
					itemLore.clear();
					
					//Stone Sword
					all[1] = new ItemStack(Material.STONE_SWORD);
					meta = all[1].getItemMeta();
					itemLore.add(String.format("Price: %d gold bar(s)", Prices.STONE_SWORD.getPrice()));
					itemLore.add(String.format("Uncommon Tier"));
					meta.setLore(itemLore);
					all[1].setItemMeta(meta);
					itemLore.clear();
					
					//Iron Sword
					all[2] = new ItemStack(Material.IRON_SWORD);
					meta = all[2].getItemMeta();
					itemLore.add(String.format("Price: %d gold bar(s)", Prices.IRON_SWORD.getPrice()));
					itemLore.add(String.format("Rare Tier"));
					meta.setLore(itemLore);
					all[2].setItemMeta(meta);
					itemLore.clear();
					
					//Gold Sword
					all[3] = new ItemStack(Material.GOLD_SWORD);
					meta = all[3].getItemMeta();
					itemLore.add(String.format("Price: %d gold bar(s)", Prices.GOLD_SWORD.getPrice()));
					itemLore.add(String.format("Epic Tier"));
					meta.setLore(itemLore);
					all[3].setItemMeta(meta);
					itemLore.clear();
					
					//Diamond Sword
					all[4] = new ItemStack(Material.DIAMOND_SWORD);
					meta = all[4].getItemMeta();
					itemLore.add(String.format("Price: %d gold bar(s)", Prices.DIAMOND_SWORD.getPrice()));
					itemLore.add(String.format("Legendary Tier"));
					meta.setLore(itemLore);
					all[4].setItemMeta(meta);
					itemLore.clear();
					
					//Wood Axe
					all[5] = new ItemStack(Material.WOOD_AXE);
					meta = all[5].getItemMeta();
					itemLore = new ArrayList<String>();
					itemLore.add(String.format("Price: %d gold bar(s)", Prices.WOOD_AXE.getPrice()));
					itemLore.add(String.format("Common Tier"));
					meta.setLore(itemLore);
					all[5].setItemMeta(meta);
					itemLore.clear();
					
					//Stone Axe
					all[6] = new ItemStack(Material.STONE_AXE);
					meta = all[6].getItemMeta();
					itemLore.add(String.format("Price: %d gold bar(s)", Prices.STONE_AXE.getPrice()));
					itemLore.add(String.format("Unommon Tier"));
					meta.setLore(itemLore);
					all[6].setItemMeta(meta);
					itemLore.clear();
					
					//Iron Axe
					all[7] = new ItemStack(Material.IRON_AXE);
					meta = all[7].getItemMeta();
					itemLore.add(String.format("Price: %d gold bar(s)", Prices.IRON_AXE.getPrice()));
					itemLore.add(String.format("Rare Tier"));
					meta.setLore(itemLore);
					all[7].setItemMeta(meta);
					itemLore.clear();
					
					//Gold Axe
					all[8] = new ItemStack(Material.GOLD_AXE);
					meta = all[8].getItemMeta();
					itemLore.add(String.format("Price: %d gold bar(s)", Prices.GOLD_AXE.getPrice()));
					itemLore.add(String.format("Epic Tier"));
					meta.setLore(itemLore);
					all[8].setItemMeta(meta);
					itemLore.clear();
					
					//Diamond Axe
					all[9] = new ItemStack(Material.DIAMOND_AXE);
					meta = all[9].getItemMeta();
					itemLore.add(String.format("Price: %d gold bar(s)", Prices.DIAMOND_AXE.getPrice()));
					itemLore.add(String.format("Legendary Tier"));
					meta.setLore(itemLore);
					all[9].setItemMeta(meta);
					itemLore.clear();
					
					//Bread
					all[10] = new ItemStack(Material.BREAD);
					meta = all[10].getItemMeta();
					itemLore.add(String.format("Price: %d gold nugget(s)", Prices.BREAD.getPrice()));
					meta.setLore(itemLore);
					all[10].setItemMeta(meta);
					itemLore.clear();
					
					//Arrows
					all[11] = new ItemStack(Material.ARROW, 32);
					meta = all[11].getItemMeta();
					itemLore.add(String.format("Price: %d gold nugget(s)", Prices.ARROW.getPrice()));
					meta.setLore(itemLore);
					all[11].setItemMeta(meta);
					itemLore.clear();
					
					//Sandstone Blocks
					all[12] = new ItemStack(Material.SANDSTONE, 8);
					meta = all[12].getItemMeta();
					itemLore.add(String.format("Price: %d gold nugget(s)", Prices.SANDSTONE.getPrice()));
					meta.setLore(itemLore);
					all[12].setItemMeta(meta);
					itemLore.clear();
					
					c.getInventory().setContents(all);
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if(GlobalArena.GetQueuePlayer(p) != null) {
				GamePlayer gp = GlobalArena.GetQueuePlayer(p);
				if(e.getInventory().getHolder() instanceof Chest) {
					Chest c = (Chest) e.getInventory().getHolder();
					ItemStack i = e.getCurrentItem();
					if(i.getType() == Material.WOOD_SWORD) {
						GlobalArena.PlayerBuy(p, new ItemStack(Material.GOLD_INGOT), Prices.WOOD_SWORD.getPrice(), new ItemStack(Material.WOOD_SWORD), "Common");
						e.setCancelled(true);
					}
					else if (i.getType() == Material.STONE_SWORD) {
						GlobalArena.PlayerBuy(p, new ItemStack(Material.GOLD_INGOT), Prices.STONE_SWORD.getPrice(), new ItemStack(Material.STONE_SWORD), "Uncommon");
						e.setCancelled(true);
					}
					else if(i.getType() == Material.IRON_SWORD) {
						GlobalArena.PlayerBuy(p, new ItemStack(Material.GOLD_INGOT), Prices.IRON_SWORD.getPrice(), new ItemStack(Material.IRON_SWORD), "Rare");
						e.setCancelled(true);
					}
					else if(i.getType() == Material.GOLD_SWORD) {
						GlobalArena.PlayerBuy(p, new ItemStack(Material.GOLD_INGOT), Prices.GOLD_SWORD.getPrice(), new ItemStack(Material.GOLD_SWORD), "Epic");
						e.setCancelled(true);
					}
					else if(i.getType() == Material.DIAMOND_SWORD) {
						GlobalArena.PlayerBuy(p, new ItemStack(Material.GOLD_INGOT), Prices.DIAMOND_SWORD.getPrice(), new ItemStack(Material.DIAMOND_SWORD), "Legendary");
						e.setCancelled(true);
					}
					else if(i.getType() == Material.WOOD_AXE) {
						if(gp.getRole() == ClassRole.FIGHTER) {
							GlobalArena.PlayerBuy(p, new ItemStack(Material.GOLD_INGOT), Prices.WOOD_AXE.getPrice(), new ItemStack(Material.WOOD_AXE), "Common");
							e.setCancelled(true);
						}
						else {
							p.sendMessage("Only fighters can buy axes.");
							e.setCancelled(true);
						}
					}
					else if (i.getType() == Material.STONE_AXE) {
						if(gp.getRole() == ClassRole.FIGHTER) {
							GlobalArena.PlayerBuy(p, new ItemStack(Material.GOLD_INGOT), Prices.STONE_AXE.getPrice(), new ItemStack(Material.STONE_AXE), "Uncommon");
							e.setCancelled(true);
						}
						else {
							p.sendMessage("Only fighters can buy axes.");
							e.setCancelled(true);
						}
					}
					else if(i.getType() == Material.IRON_AXE) {
						if(gp.getRole() == ClassRole.FIGHTER) {
							GlobalArena.PlayerBuy(p, new ItemStack(Material.GOLD_INGOT), Prices.IRON_AXE.getPrice(), new ItemStack(Material.IRON_AXE), "Rare");
							e.setCancelled(true);
						}
						else {
							p.sendMessage("Only fighters can buy axes.");
							e.setCancelled(true);
						}
					}
					else if(i.getType() == Material.GOLD_AXE) {
						if(gp.getRole() == ClassRole.FIGHTER) {
							GlobalArena.PlayerBuy(p, new ItemStack(Material.GOLD_INGOT), Prices.GOLD_AXE.getPrice(), new ItemStack(Material.GOLD_AXE), "Epic");
							e.setCancelled(true);
						}
						else {
							p.sendMessage("Only fighters can buy axes.");
							e.setCancelled(true);
						}
					}
					else if(i.getType() == Material.DIAMOND_AXE) {
						if(gp.getRole() == ClassRole.FIGHTER) {
							GlobalArena.PlayerBuy(p, new ItemStack(Material.GOLD_INGOT), Prices.DIAMOND_AXE.getPrice(), new ItemStack(Material.DIAMOND_AXE), "Legendary");
							e.setCancelled(true);
						}
						else {
							p.sendMessage("Only fighters can buy axes.");
							e.setCancelled(true);
						}
					}
					else if(i.getType() == Material.BREAD) {
						GlobalArena.PlayerBuy(p, new ItemStack(Material.GOLD_NUGGET), Prices.BREAD.getPrice(), new ItemStack(Material.BREAD));
						e.setCancelled(true);
					}
					else if(i.getType() == Material.ARROW) {
						if(gp.getRole() == ClassRole.ARCHER) {
							GlobalArena.PlayerBuy(p, new ItemStack(Material.GOLD_NUGGET), Prices.ARROW.getPrice(), new ItemStack(Material.ARROW, 32));
							e.setCancelled(true);
						}
						else {
							p.sendMessage("Only archers can buy arrows.");
							e.setCancelled(true);
						}
					}
					else if(i.getType() == Material.SANDSTONE) {
						if(gp.getRole() == ClassRole.MYSTIC || gp.getRole() == ClassRole.PALADIN) {
							GlobalArena.PlayerBuy(p, new ItemStack(Material.GOLD_NUGGET), Prices.SANDSTONE.getPrice(), new ItemStack(Material.SANDSTONE, 8));
							e.setCancelled(true);
						}
						else {
							p.sendMessage("Only mystics/paladins can buy sandstone.");
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(GlobalArena.GetQueuePlayer(p) == null) {
			if(e.getClickedBlock() != null) {
				if(e.getClickedBlock().getState() != null) {
					if(e.getClickedBlock().getState() instanceof Sign) {
						Sign s = (Sign) e.getClickedBlock().getState();
						if(s.getLine(0).equalsIgnoreCase("enter arena") && s.getLine(1).equalsIgnoreCase("click here")) {
							p.chat("/enter");
						}
					}
				}
			}
		}
		else {
			if(e.getClickedBlock() != null) {
				if(e.getClickedBlock().getState() != null) {
					if(e.getClickedBlock().getState() instanceof Sign) {
						Sign s = (Sign) e.getClickedBlock().getState();
						if(s.getLine(0).equalsIgnoreCase("leave arena") && s.getLine(1).equalsIgnoreCase("click here")) {
							p.chat("/exit");
							return;
						}
						else if(s.getLine(0).equalsIgnoreCase("ready") && s.getLine(1).equalsIgnoreCase("click here")) {
							p.chat("/ready");
							return;
						}
						else if(s.getLine(0).equalsIgnoreCase("warrior")) {
							p.chat("/role warrior");
							return;
						}
						else if(s.getLine(0).equalsIgnoreCase("beserker")) {
							p.chat("/role beserker");
							return;
						}
						else if(s.getLine(0).equalsIgnoreCase("mystic")) {
							p.chat("/role mystic");
							return;
						}
						else if(s.getLine(0).equalsIgnoreCase("paladin")) {
							p.chat("/role paladin");
							return;
						}
						else if(s.getLine(0).equalsIgnoreCase("archer")) {
							p.chat("/role archer");
							return;
						}
						else if(s.getLine(0).equalsIgnoreCase("mage")) {
							p.chat("/role mage");
							return;
						}
						else if(s.getLine(0).equalsIgnoreCase("necromancer")) {
							p.chat("/role necromancer");
							return;
						}
					}
				}
			}
			GamePlayer gp = GlobalArena.GetQueuePlayer(p);
			if(gp.getRole() == ClassRole.MYSTIC) {
				if((p.getLevel() >= gp.getAbility("summon snowman").getReqLvl()) && !(gp.getAbility("summon snowman").getIsActive())) { //Sets the summon snowman ability to active
					if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("summon snowman"));
					}
				}
				if((p.getLevel() >= gp.getAbility("sanic's blessing").getReqLvl()) && !(gp.getAbility("sanic's blessing").getIsActive())) { //Sets the sanic's blessing ability to active
					if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("sanic's blessing"));
					}
				}
				if((p.getLevel() >= gp.getAbility("mass defense").getReqLvl()) && !(gp.getAbility("mass defense").getIsActive())) { //Sets the mass defense ability to active
					if((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("mass defense"));
					}
				}
				if((p.getLevel() >= gp.getAbility("light burst").getReqLvl()) && !(gp.getAbility("light burst").getIsActive())) { //Sets the light burst ability to active
					if(e.getAction() == Action.RIGHT_CLICK_AIR) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("light burst"));
					}
				}
				if((p.getLevel() >= gp.getAbility("auria's shielding").getReqLvl()) && !(gp.getAbility("auria's shielding").getIsActive())) { //Sets the auria's shielding ability to active
					if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("auria's shielding"));
					}
				}
				if((p.getLevel() >= gp.getAbility("mystic insight").getReqLvl()) && !(gp.getAbility("mystic insight").getIsActive())) { //Sets the mystic insight ability to active
					if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("mystic insight"));
					}
				}
			}
			else if(gp.getRole() == ClassRole.PALADIN) {
				if((p.getLevel() >= gp.getAbility("auria's possession").getReqLvl()) && !(gp.getAbility("auria's possession").getIsActive())) { //Sets the auria's possession ability to active
					if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("auria's possession"));
					}
				}
			}
			else if(gp.getRole() == ClassRole.MAGE) {
				if((p.getLevel() >= gp.getAbility("fire charge").getReqLvl()) && !(gp.getAbility("fire charge").getIsActive())) { //Sets the fire charge ability to active
					if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("fire charge"));
					}
				}
				if((p.getLevel() >= gp.getAbility("mage's exchange").getReqLvl()) && !(gp.getAbility("mage's exchange").getIsActive())) { //Sets the mage's exchange ability to active
					if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("mage's exchange"));
					}
				}
				if((p.getLevel() >= gp.getAbility("nuke").getReqLvl()) && !(gp.getAbility("nuke").getIsActive())) { //Sets the nuke ability to active
					if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("nuke"));
					}
				}
				if((p.getLevel() >= gp.getAbility("hex").getReqLvl()) && !(gp.getAbility("hex").getIsActive())) { //Sets the hex ability to active
					if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("hex"));
					}
				}
				if((p.getLevel() >= gp.getAbility("firestorm").getReqLvl()) && !(gp.getAbility("firestorm").getIsActive())) { //Sets the firestorm ability to active
					if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("firestorm"));
					}
				}
				if((p.getLevel() >= gp.getAbility("arcane form").getReqLvl()) && !(gp.getAbility("arcane form").getIsActive())) { //Sets the arcane form ability to active
					if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("arcane form"));
					}
				}
			}
			else if(gp.getRole() == ClassRole.NECROMANCER) {
				if((p.getLevel() >= gp.getAbility("summon zombie").getReqLvl()) && !(gp.getAbility("summon zombie").getIsActive())) { //Sets the summon zombie ability to active
					if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("summon zombie"));
					}
				}
				if((p.getLevel() >= gp.getAbility("summon skeleton").getReqLvl()) && !(gp.getAbility("summon skeleton").getIsActive())) { //Sets the summon skeleton ability to active
					if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("summon skeleton"));
					}
				}
				if((p.getLevel() >= gp.getAbility("summon witch").getReqLvl()) && !(gp.getAbility("summon witch").getIsActive())) { //Sets the summon witch ability to active
					if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("summon witch"));
					}
				}
				if(e.getAction() == Action.RIGHT_CLICK_AIR) { //Orders the pets around
					if(gp.hasEntityInSummon(EntityType.ZOMBIE)) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("summon zombie"));
					}
					if(gp.hasEntityInSummon(EntityType.SKELETON)) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("summon skeleton"));
					}
					if(gp.hasEntityInSummon(EntityType.WITCH)) {
						GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("summon witch"));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		
		if(GlobalArena.GetQueuePlayer(p) != null) {
			GamePlayer gp = GlobalArena.GetQueuePlayer(p);
			if(gp.getRole() == ClassRole.WARRIOR) {
				if((p.getLevel() >= gp.getAbility("taunt").getReqLvl()) && !(gp.getAbility("taunt").getIsActive())) { //Sets the taunt ability to active
					if(e.getRightClicked() instanceof Creature) {
						LivingEntity le = (LivingEntity) e.getRightClicked();
						GlobalArena.PlayerUseAbility(gp, le, gp.getAbility("taunt"));
					}
					else if(e.getRightClicked() instanceof Player) {
						Player target = (Player) e.getRightClicked();
						if((p.getLevel() >= gp.getAbility("heroic tank").getReqLvl()) && !(gp.getAbility("heroic tank").getIsActive())) { //Sets the heroic tank ability to active
							GlobalArena.PlayerUseAbility(gp, target, gp.getAbility("heroic tank"));
						}
					}
				}
			}
			else if(gp.getRole() == ClassRole.PALADIN) {
				if(e.getRightClicked() instanceof Player) {
					Player target = (Player) e.getRightClicked();
					if((p.getLevel() >= gp.getAbility("mend").getReqLvl()) && !(gp.getAbility("mend").getIsActive())) { //Sets the mend ability to active
						GlobalArena.PlayerUseAbility(gp, target, gp.getAbility("mend"));
					}
					if((p.getLevel() >= gp.getAbility("knight's blessing").getReqLvl()) && !(gp.getAbility("knight's blessing").getIsActive())) { //Sets the knight's blessing ability to active
						GlobalArena.PlayerUseAbility(gp, target, gp.getAbility("knight's blessing"));
					}
					if((p.getLevel() >= gp.getAbility("auria's linking").getReqLvl()) && !(gp.getAbility("auria's linking").getIsActive())) { //Sets the auria's linking ability to active
						GlobalArena.PlayerUseAbility(gp, target, gp.getAbility("auria's linking"));
					}
					if((p.getLevel() >= gp.getAbility("holy shield").getReqLvl()) && !(gp.getAbility("holy shield").getIsActive())) { //Sets the holy shield ability to active
						GlobalArena.PlayerUseAbility(gp, target, gp.getAbility("holy shield"));
					}
					if((p.getLevel() >= gp.getAbility("remedy").getReqLvl()) && !(gp.getAbility("remedy").getIsActive())) { //Sets the remedy ability to active
						GlobalArena.PlayerUseAbility(gp, target, gp.getAbility("remedy"));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(GlobalArena.GetQueuePlayer(p) != null) {
				p.setFoodLevel(10);
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerLevelChange(PlayerLevelChangeEvent e) {
		Player p = e.getPlayer();
		
		if(GlobalArena.GetQueuePlayer(p) != null) {
			GamePlayer gp = GlobalArena.GetQueuePlayer(p);
			if(e.getOldLevel() < e.getNewLevel()) {
				gp.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
				switch(gp.getRole()) {
				case ARCHER:
					gp.setMaxHP(20.0d + (0.125d * e.getNewLevel()));
					gp.setAtk(1.5d + (0.125d * e.getNewLevel()));
					gp.setMagic(0.0d + (0.125d * e.getNewLevel()));
					gp.setDefense(1.5d + (0.125d * e.getNewLevel()));
					gp.setResistance(1.5d + (0.125d * e.getNewLevel()));
					gp.setDodge(1.5d + (0.125d * e.getNewLevel()));
					p.sendMessage("You have leveled up!");
					for(Ability a : gp.getAllAbilities()) {
						if(a.getReqLvl() == e.getNewLevel()) {
							p.sendMessage("You have gained " + a.getDisplayName() + " arrows!");
						}
					}
					break;
				case MYSTIC:
					gp.setMaxHP(20.0d + (0.125d * e.getNewLevel()));
					gp.setAtk(2.0d + (0.25d * e.getNewLevel()));
					gp.setMagic(3.5d + (0.5d * e.getNewLevel()));
					gp.setDefense(0.0d + (0.125d * e.getNewLevel()));
					gp.setResistance(0.5d + (0.125d * e.getNewLevel()));
					gp.setDodge(0.0d + (0.25d * e.getNewLevel()));
					p.sendMessage("You have leveled up!");
					for(Ability a : gp.getAllAbilities()) {
						if(a.getReqLvl() == e.getNewLevel()) {
							p.sendMessage("You have gained " + a.getDisplayName() + "!");
							if(a.getName().equalsIgnoreCase("sanic's blessing")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Sanic's Blessing");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("mass defense")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Mass Defense");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("light burst")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Light Burst");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("auria's shielding")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Auria's Shielding");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("mystic insight")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Mystic Insight");
								p.getInventory().addItem(li);
							}
						}
					}
					break;
				case PALADIN:
					gp.setMaxHP(20.0d + (0.25d * e.getNewLevel()));
					gp.setAtk(2.5d + (0.125d * e.getNewLevel()));
					gp.setMagic(1.0d + (0.25d * e.getNewLevel()));
					gp.setDefense(1.5d + (0.25d * e.getNewLevel()));
					gp.setResistance(1.0d + (0.25d * e.getNewLevel()));
					gp.setDodge(0.0d + (0.125d * e.getNewLevel()));
					p.sendMessage("You have leveled up!");
					for(Ability a : gp.getAllAbilities()) {
						if(a.getReqLvl() == e.getNewLevel()) {
							p.sendMessage("You have gained " + a.getDisplayName() + "!");
							if(a.getName().equalsIgnoreCase("knight's blessing")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Knight's Blessing");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("auria's linking")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Auria's Linking");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("holy shield")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Holy Shield");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("remedy")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Remedy");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("auria's possession")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: auria's possession");
								p.getInventory().addItem(li);
							}
						}
					}
					break;
				case FIGHTER:
					gp.setMaxHP(20.0d + (0.25d * e.getNewLevel()));
					gp.setAtk(3.0d + (0.25d * e.getNewLevel()));
					gp.setMagic(0.0d + (0.125d * e.getNewLevel()));
					gp.setDefense(2.0d + (0.125d * e.getNewLevel()));
					gp.setResistance(.0d + (0.125d * e.getNewLevel()));
					gp.setDodge(1.0d + (0.125d * e.getNewLevel()));
					p.sendMessage("You have leveled up!");
					for(Ability a : gp.getAllAbilities()) {
						if(a.getReqLvl() == e.getNewLevel()) {
							p.sendMessage("You have gained " + a.getDisplayName() + "!");
						}
					}
					break;
				case MAGE:
					gp.setMaxHP(20.0d + (0.125d * e.getNewLevel()));
					gp.setAtk(1.5d + (0.125d * e.getNewLevel()));
					gp.setMagic(4.5d + (0.5d * e.getNewLevel()));
					gp.setDefense(0.0d + (0.125d * e.getNewLevel()));
					gp.setResistance(0.0d + (0.25d * e.getNewLevel()));
					gp.setDodge(0.0d + (0.25d * e.getNewLevel()));
					p.sendMessage("You have leveled up!");
					for(Ability a : gp.getAllAbilities()) {
						if(a.getReqLvl() == e.getNewLevel()) {
							p.sendMessage("You have gained " + a.getDisplayName() + "!");
							if(a.getName().equalsIgnoreCase("nuke")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Nuke");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("mage's exchange")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Mage's Exchange");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("hex")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Hex");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("firestorm")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Firestorm");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("arcane form")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Arcane Form");
								p.getInventory().addItem(li);
							}
						}
					}
					break;
				case WARRIOR:
					gp.setMaxHP(20.0d + (0.5d * e.getNewLevel()));
					gp.setAtk(3.0d + (0.25d * e.getNewLevel()));
					gp.setMagic(0.5d + (0.125d * e.getNewLevel()));
					gp.setDefense(1.5d + (0.5d * e.getNewLevel()));
					gp.setResistance(1.5d + (0.125d * e.getNewLevel()));
					gp.setDodge(0.0d + (0.125d * e.getNewLevel()));
					p.sendMessage("You have leveled up!");
					for(Ability a : gp.getAllAbilities()) {
						if(a.getReqLvl() == e.getNewLevel()) {
							p.sendMessage("You have gained " + a.getDisplayName() + "!");
						}
					}
					break;
				case NECROMANCER:
					if(gp.getAbility("death's grasp") != null) {
						if(p.getLevel() >= gp.getAbility("death's grasp").getReqLvl()) {
							if(!gp.getAbility("death's grasp").getIsActive()) {
								gp.setMaxHP(20.0d + (0.125d * e.getNewLevel()));
							}
						}
						else {
							gp.setMaxHP(20.0d + (0.125d * e.getNewLevel()));
						}
					}
					else {
						gp.setMaxHP(20.0d + (0.125d * e.getNewLevel()));
					}
					gp.setAtk(3.0d + (0.25d * e.getNewLevel()));
					gp.setMagic(2.0d + (0.25d * e.getNewLevel()));
					gp.setDefense(0.0d + (0.125d * e.getNewLevel()));
					gp.setResistance(1.0d + (0.25d * e.getNewLevel()));
					gp.setDodge(0.0d + (0.25d * e.getNewLevel()));
					p.sendMessage("You have leveled up!");
					for(Ability a : gp.getAllAbilities()) {
						if(a.getReqLvl() == e.getNewLevel()) {
							p.sendMessage("You have gained " + a.getDisplayName() + "!");
							if(a.getName().equalsIgnoreCase("summon zombie")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Summon Zombie");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("summon skeleton")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Summon Skeleton");
								p.getInventory().addItem(li);
							}
							else if(a.getName().equalsIgnoreCase("summon witch")) {
								LoreItem li = new LoreItem(Material.STICK, "Spell: Summon Witch");
								p.getInventory().addItem(li);
							}
						}
					}
					break;
				default:
					p.sendMessage("You have leveled up!");
					break;
				}
			}
		}
	}
	
	private double GetPlayerDamageWithArmor(Player p, double damage) {
		int armor = 0;
		if(p.getInventory().getHelmet() != null) {
			ItemStack helmet = p.getInventory().getHelmet();
			switch(helmet.getType())
			{
				case LEATHER_HELMET:
				{
					armor += 1;
				}
				case IRON_HELMET:
				{
					armor += 2;
				}
				case DIAMOND_HELMET:
				{
					armor += 3;
				}
				default:
				{
					armor += 0;
				}
			}
		}
		if(p.getInventory().getChestplate() != null) {
			ItemStack chestplate = p.getInventory().getChestplate();
			switch(chestplate.getType())
			{
				case LEATHER_CHESTPLATE:
				{
					armor += 3;
				}
				case IRON_CHESTPLATE:
				{
					armor += 6;
				}
				case DIAMOND_CHESTPLATE:
				{
					armor += 8;
				}
				default:
				{
					armor += 0;
				}
			}
		}
		if(p.getInventory().getLeggings() != null) {
			ItemStack leggings = p.getInventory().getLeggings();
			switch(leggings.getType())
			{
				case LEATHER_LEGGINGS:
				{
					armor += 2;
				}
				case IRON_LEGGINGS:
				{
					armor += 5;
				}
				case DIAMOND_LEGGINGS:
				{
					armor += 6;
				}
				default:
				{
					armor += 0;
				}
			}
		}
		if(p.getInventory().getBoots() != null) {
			ItemStack boots = p.getInventory().getBoots();
			switch(boots.getType())
			{
				case LEATHER_BOOTS:
				{
					armor += 1;
				}
				case IRON_BOOTS:
				{
					armor += 2;
				}
				case DIAMOND_BOOTS:
				{
					armor += 3;
				}
				default:
				{
					armor += 0;
				}
			}
		}
		double a, b, dmg;
		dmg = damage;
		a = armor / 5.0d;
		b = armor - (dmg / (2.0d / 4.0d));
		if(b > a) {
			a = b;
			b = 0.0d;
		}
		if(a > 20.0d) {
			a = 20.0d;
		}
		a = a / 25.0d;
		a = 1 - a;
		dmg = dmg * a;
		return dmg;
	}
}