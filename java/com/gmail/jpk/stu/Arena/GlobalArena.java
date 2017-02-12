package com.gmail.jpk.stu.Arena;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.gmail.jpk.stu.Abilities.Ability;
import com.gmail.jpk.stu.Entities.AttackType;
import com.gmail.jpk.stu.Entities.ClassRole;
import com.gmail.jpk.stu.Entities.GameEntity;
import com.gmail.jpk.stu.Entities.GamePlayer;
import com.gmail.jpk.stu.Entities.StatusEffect;
import com.gmail.jpk.stu.Entities.SlaveEntity.SlaveTargetReason;
import com.gmail.jpk.stu.Entities.StatusEffect.StatusEffects;
import com.gmail.jpk.stu.Timers.AbilityTimer;
import com.gmail.jpk.stu.Timers.StackTimer;
import com.gmail.jpk.stu.Timers.StatChangeTimer;
import com.gmail.jpk.stu.Timers.StatusEffectTimer;

public class GlobalArena {
	
	private static class DelaySpawnTask extends BukkitRunnable {
		private Location spawn;
		private EntityType entity;
		private int level;
		
		public DelaySpawnTask(Location spawn, EntityType entity, int level) {
			this.spawn = spawn;
			this.entity = entity;
			this.level = level;
		}
		
		public void run() {
			LivingEntity le = (LivingEntity) inWorld.spawnEntity(spawn, entity);
			CreaturesInArena.add(new GameEntity(le, level, 1));
		}
	}
	
	private static class DelayShopTeleTask extends BukkitRunnable {

		public void run() {
			QueuePlayersTele(playerSpawns.get(0));
			toQueuePlayers("Shop for the things you need and then type /ready to continue the arena.");
			toSpectatorPlayers("The players are in the shop. They'll be back when they're ready.");
		}
	}
	
	public static class DelayRoundStartTask extends BukkitRunnable {

		public void run() {
			NextRound();
		}
	}
	
	private static Arena plugin;
	private static ScoreboardManager Manager;
	private static Scoreboard[] Scoreboard;
	private static int Round = 0;
	private static boolean HasStarted = false;
	private static int startPpl = 0;
	private static final int MaxSize = 10;
	private static World inWorld;
	private static List<GamePlayer> PlayersInQueue = new ArrayList<GamePlayer>();
	private static List<Player> SpectatorPlayers = new ArrayList<Player>();
	private static List<GameEntity> CreaturesInArena = new ArrayList<GameEntity>();
	private static List<Location> playerSpawns = new ArrayList<Location>();
	private static List<Location> monsterSpawn1 = new ArrayList<Location>();
	private static List<Location> monsterSpawn2 = new ArrayList<Location>();
	private static List<Block> explodeBlocks = new ArrayList<Block>();
	
	public static void InitWorld(Arena plugin) {
		GlobalArena.plugin = plugin;
		Manager = Bukkit.getScoreboardManager();
		Scoreboard = new Scoreboard[3];
		Scoreboard[0] = Manager.getNewScoreboard();
		Scoreboard[1] = Manager.getNewScoreboard();
		Scoreboard[2] = Manager.getNewScoreboard();
		Objective objective = Scoreboard[0].registerNewObjective("showlevel", "level");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GREEN + "Levels" + ChatColor.WHITE);
		objective = Scoreboard[1].registerNewObjective("showscore", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GREEN + "Contribution Score" + ChatColor.WHITE);
		objective = Scoreboard[2].registerNewObjective("showhealth", "health");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GREEN + "Health" + ChatColor.WHITE);
		inWorld = plugin.getServer().getWorlds().get(0);
		playerSpawns.add(new Location(inWorld, -11.5d, 249.0d, 204.6d)); //Shop
		playerSpawns.add(new Location(inWorld, 18.6d, 249.0d, 244.482d)); //Spectator Area
		playerSpawns.add(new Location(inWorld, 12.0d, 212.0d, 214.0d)); // Pre-Game Area
		playerSpawns.add(new Location(inWorld, 38.0d, 218.0d, 230.0d)); //First Arena
		playerSpawns.add(new Location(inWorld, -41.0d, 224.0d, 43.0d)); //Second Arena
		monsterSpawn1.add(new Location(inWorld, 10.0d, 218.0d, 230.0d)); //First Arena
		monsterSpawn1.add(new Location(inWorld, 10.0d, 218.0d, 202.0d)); //First Arena
		monsterSpawn1.add(new Location(inWorld, 38.0d, 218.0d, 202.0d)); //First Arena
		monsterSpawn2.add(new Location(inWorld, -50.0d, 224.0d, 52.0d)); //Second Arena
		monsterSpawn2.add(new Location(inWorld, -33.0d, 224.0d, 52.0d)); //Second Arena
		monsterSpawn2.add(new Location(inWorld, -33.0d, 224.0d, 35.0d)); //Second Arena
		monsterSpawn2.add(new Location(inWorld, -50.0d, 224.0d, 35.0d)); //Second Arena
	}
	
	public static ScoreboardManager getScoreboardManager() {
		return Manager;
	}
	
	public static Scoreboard getScoreboard(int i) {
		return Scoreboard[i];
	}
	
	public static int getRound() {
		return Round;
	}
	
	public static Location getSpawn(int i) {
		return playerSpawns.get(i);
	}
	
	public static void toQueuePlayers(String msg) {
		for(int i = 0; i < PlayersInQueue.size(); i++) {
			PlayersInQueue.get(i).getMinecraftPlayer().sendMessage(msg);
		}
	}
	
	public static void toSpectatorPlayers(String msg) {
		for(int i = 0; i < SpectatorPlayers.size(); i++) {
			SpectatorPlayers.get(i).sendMessage(msg);
		}
	}
	
	public static void QueuePlayersTele(Location loc) {
		for(int i = 0; i < PlayersInQueue.size(); i++) {
			PlayersInQueue.get(i).getMinecraftPlayer().teleport(loc);
		}
	}
	
	public static void PlayerShopTele() {
		new DelayShopTeleTask().runTaskLater(GlobalArena.plugin, 200);
	}
	
	private static void NextRound() {
		Round++;
		toQueuePlayers("Round " + Round + " has started!");
		toSpectatorPlayers("Round " + Round + " has started!");
		int[] levels;
		switch(Round) {
		case 1:
			levels = new int[]{1};
			SpawnInArena(levels, EntityType.SPIDER);
			break;
		case 2:
			levels = new int[]{1,1};
			SpawnInArena(levels, EntityType.SPIDER, EntityType.SPIDER);
			break;
		case 3:
			levels = new int[]{2,1,1};
			SpawnInArena(levels, EntityType.SPIDER, EntityType.SPIDER, EntityType.SPIDER);
			break;
		case 4:
			levels = new int[]{2,2,1};
			SpawnInArena(levels, EntityType.SPIDER, EntityType.SPIDER, EntityType.ZOMBIE);
			break;
		case 5:
			levels = new int[]{2,1,1};
			SpawnInArena(levels, EntityType.SPIDER, EntityType.ZOMBIE, EntityType.ZOMBIE);
			break;
		case 6:
			levels = new int[]{2,1,1,2};
			SpawnInArena(levels, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER);
			break;
		case 7:
			levels = new int[]{1, 2, 1, 3};
			SpawnInArena(levels, EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER);
			break;
		case 8:
			levels = new int[]{1, 1, 2, 3};
			SpawnInArena(levels, EntityType.SKELETON, EntityType.SKELETON, EntityType.ZOMBIE, EntityType.SPIDER);
			break;
		case 9:
			levels = new int[]{2, 1, 1, 3};
			SpawnInArena(levels, EntityType.SKELETON, EntityType.SKELETON, EntityType.SKELETON, EntityType.ZOMBIE);
			break;
		case 10:
			levels = new int[]{1, 1};
			SpawnInArena(levels, EntityType.BLAZE, EntityType.BLAZE);
			break;
		case 11:
			levels = new int[]{1, 2, 3, 3};
			SpawnInArena(levels, EntityType.CREEPER, EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE);
			break;
		case 12:
			levels = new int[]{1, 1, 2, 2, 3};
			SpawnInArena(levels, EntityType.CREEPER, EntityType.CREEPER, EntityType.SKELETON, EntityType.SKELETON, EntityType.ZOMBIE);
			break;
		case 13:
			levels = new int[]{1, 1, 1, 2, 2};
			SpawnInArena(levels, EntityType.CREEPER, EntityType.CREEPER, EntityType.CREEPER, EntityType.SKELETON, EntityType.SKELETON);
			break;
		case 14:
			levels = new int[]{2, 1, 1, 1, 3};
			SpawnInArena(levels, EntityType.CREEPER, EntityType.CREEPER, EntityType.CREEPER, EntityType.CREEPER, EntityType.SKELETON);
			break;
		case 15:
			levels = new int[]{2, 2, 1, 1, 1};
			SpawnInArena(levels, EntityType.CREEPER, EntityType.CREEPER, EntityType.CREEPER, EntityType.CREEPER, EntityType.CREEPER);
			break;
		case 16:
			levels = new int[]{1, 1, 1, 5, 5};
			SpawnInArena(levels, EntityType.CAVE_SPIDER, EntityType.CAVE_SPIDER, EntityType.CAVE_SPIDER, EntityType.SPIDER, EntityType.SPIDER);
			break;
		case 17:
			levels = new int[]{2, 1, 3, 4, 2};
			SpawnInArena(levels, EntityType.CAVE_SPIDER, EntityType.CAVE_SPIDER, EntityType.SKELETON, EntityType.ZOMBIE, EntityType.CREEPER);
			break;
		case 18:
			levels = new int[]{2, 3, 4, 5, 1};
			SpawnInArena(levels, EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.SKELETON, EntityType.ZOMBIE, EntityType.SILVERFISH);
			break;
		case 19:
			levels = new int[]{1, 1, 1, 1, 1, 1};
			SpawnInArena(levels, EntityType.SILVERFISH, EntityType.SILVERFISH, EntityType.SILVERFISH, EntityType.SILVERFISH, EntityType.SILVERFISH, EntityType.GIANT);
			break;
		case 20:
			levels = new int[]{1, 2, 2};
			SpawnInArena(levels, EntityType.GHAST, EntityType.BLAZE, EntityType.BLAZE);
			break;
		default:
			levels = new int[]{1};
			SpawnInArena(levels, EntityType.PIG);
			break;
		}
	}
	
	public static double PlayerUseAbility(GamePlayer player, LivingEntity target, Ability ability)
	{
		return PlayerUseAbility(player, target, ability, 0.0d);
	}
	
	public static double PlayerUseAbility(GamePlayer player, LivingEntity target, Ability ability, double damage)
	{
		Player p = player.getMinecraftPlayer();
		switch(ability.getName().toLowerCase()) {
			case "bountiful feast":
			{
				if(player.getRole() == ClassRole.WARRIOR) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
					player.getAbility("bountiful feast").setIsActive(true);
					new AbilityTimer(player.getMinecraftPlayer(), player.getAbility("bountiful feast"), player.getRole()).runTaskLater(plugin, player.getAbility("bountiful feast").getCooldown());
				}
				break;
			}
			case "taunt":
			{
				if(player.getRole() == ClassRole.WARRIOR) {
					if(GetGameEntity(target) != null) {
						GameEntity ge = GetGameEntity(target);
						if(!ge.hasStatusEffect(StatusEffects.TAUNTED)) {
							ge.addStatusEffect(new StatusEffect(StatusEffects.TAUNTED, 99999, p));
							Creature c = (Creature) target;
							c.setTarget(p);
							p.sendMessage("Creature taunted!");
							player.getAbility("taunt").setIsActive(true);
							player.addScore(10);
							new AbilityTimer(p, player.getAbility("taunt"), player.getRole()).runTaskLater(plugin, player.getAbility("taunt").getCooldown());
						}
					}
				}
				break;
			}
			case "victory smash":
			{
				if(player.getRole() == ClassRole.WARRIOR) {
					double dmg = damage * player.getAbility("victory smash").getPow();
					player.getAbility("victory smash").setIsActive(true);
					if((p.getHealth() + (p.getMaxHealth() * 0.3)) <= p.getMaxHealth()) {
						p.setHealth(p.getHealth() + (p.getMaxHealth() * 0.3));
					}
					else {
						p.setHealth(p.getMaxHealth());
					}
					new AbilityTimer(p, player.getAbility("victory smash"), player.getRole()).runTaskLater(plugin, player.getAbility("victory smash").getCooldown());
					return dmg;
				}
				break;
			}
			case "defensive paradigm":
			{
				if(player.getRole() == ClassRole.WARRIOR) {
					player.setDefense(player.getDefense(true) + ability.getPow());
					ability.setIsActive(true);
					new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
					new StatChangeTimer(p, "defense", -(ability.getPow())).runTaskLater(plugin, 40);
				}
				break;
			}
			case "vigour":
			{
				if(player.getRole() == ClassRole.WARRIOR) {
					player.setDefense(player.getDefense(true) + ability.getPow());
					ability.setIsActive(true);
					new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
					new StatChangeTimer(p, "defense", -(ability.getPow())).runTaskLater(plugin, 40);
				}
				break;
			}
			case "heroic tank":
			{
				if(player.getRole() == ClassRole.WARRIOR) {
					if(target instanceof Player) {
						Player tarP = (Player) target;
						if(GetQueuePlayer(tarP) != null) {
							GamePlayer gp = GetQueuePlayer(tarP);
							if(!gp.hasStatusEffect(StatusEffects.HEROIC_LINK)) {
								StatusEffect se = new StatusEffect(StatusEffects.HEROIC_LINK, 140, p);
								gp.addStatusEffect(se);
								tarP.sendMessage(p.getDisplayName() + " has heroic linked with you! You won't take damage!");
								p.sendMessage(tarP.getDisplayName() + " has been linked to you!");
								ability.setIsActive(true);
								new StatusEffectTimer(se, tarP).runTaskLater(plugin, se.getDuration());
								new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
							}
						}
					}
				}
				break;
			}
			case "true damager":
			{
				if(player.getRole() == ClassRole.FIGHTER) {
					damage += (target.getMaxHealth() * player.getAbility("true damager").getPow());
					return damage;
				}
				break;
			}
			case "rage":
			{
				if(player.getRole() == ClassRole.FIGHTER) {
					double multi = (1.0d - (p.getHealth() / p.getMaxHealth()));
					multi *= 100.0d;
					if(multi < 25.0d) { // Check if multi is less than 25%
						multi = 25.0d;
					}
					if((player.getCritical(false) + multi) > 100.0d) { // Check if setting critical would be above 100% chance
						multi = 100.0d - player.getCritical(false);
					}
					player.setCritical(player.getCritical(true) + multi);
					plugin.getLogger().info("Critical Chance: " + player.getCritical(false) + " Multi: " + multi);
					player.getAbility("rage").setIsActive(true);
					new AbilityTimer(p, player.getAbility("rage"), player.getRole()).runTaskLater(plugin, player.getAbility("rage").getCooldown());
					new StatChangeTimer(p, "critical", -(multi)).runTaskLater(plugin, 10);
				}
				break;
			}
			case "blood rush":
			{
				if(player.getRole() == ClassRole.FIGHTER) {
					if(GetGameEntity(target) != null) {
						if(ability.isStackable()) {
							ability.setCurrentStacks(ability.getCurrentStacks() + 1);
							if(ability.getCurrentStacks() >= ability.getMaxStacks()) {
								player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));
								ability.setCurrentStacks(0);
								ability.setIsActive(true);
								new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
							}
							else {
								new StackTimer(p, ability).runTaskLater(plugin, 140);
							}
						}
					}
				}
				break;
			}
			case "lifesteal":
			{
				if(player.getRole() == ClassRole.FIGHTER) {
					if(p.getHealth() + ability.getPow() <= p.getMaxHealth()) {
						p.setHealth(p.getHealth() + player.getAbility("lifesteal").getPow());
						player.getAbility("lifesteal").setIsActive(true);
						new AbilityTimer(p, player.getAbility("lifesteal"), player.getRole()).runTaskLater(plugin, player.getAbility("lifesteal").getCooldown());
					}
				}
				break;
			}
			case "fervor":
			{
				if(player.getRole() == ClassRole.FIGHTER) {
					if(player.getHarmfulPotionEffects().size() > 0) {
						for(PotionEffect effect : player.getHarmfulPotionEffects()) {
							player.removePotionEffect(effect);
						}
						ability.setIsActive(true);
						new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
					}
				}
				break;
			}
			case "death axe":
			{
				if(player.getRole() == ClassRole.FIGHTER) {
					if(GetGameEntity(target) != null) {
						GameEntity ge = GetGameEntity(target);
						ge.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 5, 1));
						player.addScore(7);
						ability.setIsActive(true);
						new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
					}
				}
				break;
			}
			case "summon snowman":
			{
				if(player.getRole() == ClassRole.MYSTIC) {
					if(p.getInventory().getItemInMainHand().getType() == Material.AIR) {
						if(!(player.hasEntityInSummon(EntityType.SNOWMAN))) {
							GlobalArena.PlayerSummonLivingEntity(player, EntityType.SNOWMAN);
						}
					}
				}
				break;
			}
			case "sanic's blessing":
			{
				if(player.getRole() == ClassRole.MYSTIC) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: sanic's blessing")) {
								Location loc = p.getLocation();
								for(Entity ent : loc.getChunk().getEntities()) {
									if(ent instanceof Player && (loc.distance(ent.getLocation()) <= 8)) {
										Player tarP = (Player) ent;
										if(GetQueuePlayer(tarP) != null) {
											GamePlayer gp = GetQueuePlayer(tarP);
											gp.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
											gp.getMinecraftPlayer().sendMessage(p.getDisplayName() + " has sped you up!");
											player.addScore(3);
										}
									}
								}
								p.sendMessage("Your team's been sped up!");
								ability.setIsActive(true);
								new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
							}
						}
					}
				}
				break;
			}
			case "mass defense":
			{
				if(player.getRole() == ClassRole.MYSTIC) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: mass defense")) {
								Location loc = p.getLocation();
								for(Entity ent : loc.getChunk().getEntities()) {
									if(ent instanceof Player && (loc.distance(ent.getLocation()) <= 8)) {
										Player tarP = (Player) ent;
										if(GetQueuePlayer(tarP) != null) {
											GamePlayer gp = GetQueuePlayer(tarP);
											gp.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0));
											gp.getMinecraftPlayer().sendMessage(p.getDisplayName() + " has boosted your defense!");
											player.addScore(3);
										}
									}
								}
								p.sendMessage("You have boosted your team's defense!");
								ability.setIsActive(true);
								new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
							}
						}
					}
				}
				break;
			}
			case "light burst":
			{
				if(player.getRole() == ClassRole.MYSTIC) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: light burst")) {
								Location loc = p.getTargetBlock((Set<Material>) null, 25).getLocation();
								p.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 4f, false, false);
								ability.setIsActive(true);
								player.addScore(8);
								new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
							}
						}
					}
				}
				break;
			}
			case "auria's shielding":
			{
				if(player.getRole() == ClassRole.MYSTIC) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: auria's shielding")) {
								Location loc = p.getLocation();
								for(Entity ent : loc.getChunk().getEntities()) {
									if(ent instanceof Player && (loc.distance(ent.getLocation()) <= 8)) {
										Player tarP = (Player) ent;
										if(GetQueuePlayer(tarP) != null) {
											GamePlayer gp = GetQueuePlayer(tarP);
											gp.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 300, 1));
											gp.getMinecraftPlayer().sendMessage(p.getDisplayName() + " has given your Auria's Shielding!");
											player.addScore(3);
										}
									}
								}
								p.sendMessage("You have given your team Auria's Shielding!");
								ability.setIsActive(true);
								new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
							}
						}
					}
				}
				break;
			}
			case "mystic insight":
			{
				if(player.getRole() == ClassRole.MYSTIC) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: mystic insight")) {
								for(GamePlayer gp : getPlayersInQueue()) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 0));
									gp.getMinecraftPlayer().sendMessage(p.getDisplayName() + " has sped you up!");
									player.addScore(3);
									gp.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 0));
									gp.getMinecraftPlayer().sendMessage(p.getDisplayName() + " has boosted your defense!");
									player.addScore(3);
									gp.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, 1));
									gp.getMinecraftPlayer().sendMessage(p.getDisplayName() + " has given you Auria's Shielding!");
									player.addScore(3);
									gp.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 400, 0));
									gp.getMinecraftPlayer().sendMessage(p.getDisplayName() + "has increased your damage!");
									player.addScore(3);
								}
								p.sendMessage("Your team has been given Mystic's Insight!");
								ability.setIsActive(true);
								new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
							}
						}
					}
				}
				break;
			}
			case "mend":
			{
				if(player.getRole() == ClassRole.PALADIN) {
					if(p.getInventory().getItemInMainHand().getType() == Material.WOOD_SWORD || p.getInventory().getItemInMainHand().getType() == Material.STONE_SWORD ||
							p.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD || p.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD) {
						if(target instanceof Player) {
							Player tarP = (Player) target;
							GamePlayer targetPlayer = GlobalArena.GetQueuePlayer(tarP);
							if(targetPlayer.getMinecraftPlayer().getHealth() < targetPlayer.getMinecraftPlayer().getMaxHealth()) {
								if(player.hasStatusEffect(StatusEffects.AURIA_POSSESSED)) {
									targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 1));
									targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60, 1));
									p.sendMessage(targetPlayer.getMinecraftPlayer().getDisplayName() + " is being healed!");
									targetPlayer.getMinecraftPlayer().sendMessage(p.getDisplayName() + " is healing you!");
									player.addScore(3);
								}
								else {
									targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
									p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 30, 1));
									p.sendMessage(targetPlayer.getMinecraftPlayer().getDisplayName() + " is being healed!");
									targetPlayer.getMinecraftPlayer().sendMessage(p.getDisplayName() + " is healing you!");
									player.addScore(3);
								}
								
								for(GamePlayer gp : getPlayersInQueue()) {
									if(gp.hasStatusEffect(StatusEffects.SOUL_LINK)) {
										if(gp.getStatusEffect(StatusEffects.SOUL_LINK).getCaster() == p) {
											if(gp.getMinecraftPlayer().getHealth() < gp.getMinecraftPlayer().getMaxHealth()) {
												if(player.hasStatusEffect(StatusEffects.AURIA_POSSESSED)) {
													gp.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 1));
													gp.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60, 1));
													p.sendMessage(gp.getMinecraftPlayer().getDisplayName() + " is being healed");
													gp.getMinecraftPlayer().sendMessage(p.getDisplayName() + " is heling you!");
													player.addScore(3);
												}
												else {
													gp.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
													p.sendMessage(gp.getMinecraftPlayer().getDisplayName() + " is being healed");
													gp.getMinecraftPlayer().sendMessage(p.getDisplayName() + " is heling you!");
													player.addScore(3);
												}
											}
											break;
										}
									}
								}
								player.getAbility("mend").setIsActive(true);
								new AbilityTimer(p, player.getAbility("mend"), player.getRole()).runTaskLater(plugin, player.getAbility("mend").getCooldown());
							}
						}
					}
				}
				break;
			}
			case "knight's blessing":
			{
				if(player.getRole() == ClassRole.PALADIN) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: knight's blessing")) {
								if(target instanceof Player) {
									Player tarP = (Player) target;
									if(GetQueuePlayer(tarP) != null) {
										GamePlayer targetPlayer = GlobalArena.GetQueuePlayer(tarP);
										if(player.hasStatusEffect(StatusEffects.AURIA_POSSESSED)) {
											targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 1));
											targetPlayer.getMinecraftPlayer().sendMessage(p.getDisplayName() + " has blessed you! You take less damage!");
											p.sendMessage(targetPlayer.getMinecraftPlayer().getDisplayName() + " has been blessed! You gain the blessing as well!");
											player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0));
											player.addScore(3);
										}
										else {
											targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 1));
											targetPlayer.getMinecraftPlayer().sendMessage(p.getDisplayName() + " has blessed you! You take less damage!");
											p.sendMessage(targetPlayer.getMinecraftPlayer().getDisplayName() + " has been blessed! You gain the blessing as well!");
											player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0));
											player.addScore(3);
										}
										
										for(GamePlayer gp : getPlayersInQueue()) {
											if(gp.hasStatusEffect(StatusEffects.SOUL_LINK)) {
												if(gp.getStatusEffect(StatusEffects.SOUL_LINK).getCaster() == p) {
													if(player.hasStatusEffect(StatusEffects.AURIA_POSSESSED)) {
														gp.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 1));
														gp.getMinecraftPlayer().sendMessage(p.getDisplayName() + " has blessed you! You take less damage!");
														p.sendMessage(gp.getMinecraftPlayer().getDisplayName() + " has been blessed!");
														player.addScore(3);
													}
													else {
														gp.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 1));
														gp.getMinecraftPlayer().sendMessage(p.getDisplayName() + " has blessed you! You take less damage!");
														p.sendMessage(gp.getMinecraftPlayer().getDisplayName() + " has been blessed!");
														player.addScore(3);
													}
													break;
												}
											}
										}
										player.getAbility("knight's blessing").setIsActive(true);
										int cooldown = player.hasStatusEffect(StatusEffects.AURIA_POSSESSED) ? (ability.getCooldown() / 2) : ability.getCooldown();
										new AbilityTimer(p, player.getAbility("knight's blessing"), player.getRole()).runTaskLater(plugin, cooldown);
									}
								}
							}
						}
					}
				}
				break;
			}
			case "auria's linking":
			{
				if(player.getRole() == ClassRole.PALADIN) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: auria's linking")) {
								if(target instanceof Player) {
									Player tarP = (Player) target;
									GamePlayer targetPlayer = GlobalArena.GetQueuePlayer(tarP);
									if(!targetPlayer.hasStatusEffect(StatusEffects.SOUL_LINK)) {
										targetPlayer.addStatusEffect(new StatusEffect(StatusEffects.SOUL_LINK, 99999, p));
										tarP.sendMessage(p.getDisplayName() + " has linked with you! Their abilities affect you!");
										p.sendMessage(tarP.getDisplayName() + " has been linked!");
										player.addScore(2);
										ability.setIsActive(true);
									}
								}
							}
						}
					}
				}
				break;
			}
			case "holy shield":
			{
				if(player.getRole() == ClassRole.PALADIN) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: holy shield")) {
								if(target instanceof Player) {
									Player tarP = (Player) target;
									GamePlayer targetPlayer = GlobalArena.GetQueuePlayer(tarP);
									StatusEffect se = player.hasStatusEffect(StatusEffects.AURIA_POSSESSED) ? 
											new StatusEffect(StatusEffects.INVULNERABLE, 200, p) : new StatusEffect(StatusEffects.INVULNERABLE, 100, p);
									if(!targetPlayer.hasStatusEffect(StatusEffects.INVULNERABLE)) {
										targetPlayer.addStatusEffect(se);
										player.addStatusEffect(se);
										tarP.sendMessage(p.getDisplayName() + " has made you invulnerable!");
										p.sendMessage(tarP.getDisplayName() + " has been given holy shield! You gain it as well!");
										player.addScore(15);
										
										Player linked = null;
										for(GamePlayer gp : getPlayersInQueue()) {
											if(gp.hasStatusEffect(StatusEffects.SOUL_LINK)) {
												if(gp.getStatusEffect(StatusEffects.SOUL_LINK).getCaster() == p) {
													if(!gp.hasStatusEffect(StatusEffects.INVULNERABLE)) {
														gp.addStatusEffect(se);
														gp.getMinecraftPlayer().sendMessage(p.getDisplayName() + " has made you invulnerable!");
														p.sendMessage(gp.getMinecraftPlayer().getDisplayName() + " has been given holy shield!");
														linked = gp.getMinecraftPlayer();
														player.addScore(15);
													}
													break; // For server load
												}
											}
										}
										ability.setIsActive(true);
										int cooldown = player.hasStatusEffect(StatusEffects.AURIA_POSSESSED) ? (ability.getCooldown() / 2) : ability.getCooldown();
										new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, cooldown);
										new StatusEffectTimer(se, tarP).runTaskLater(plugin, se.getDuration());
										new StatusEffectTimer(se, p).runTaskLater(plugin, se.getDuration());
										if(linked != null) {
											new StatusEffectTimer(se, linked).runTaskLater(plugin, se.getDuration());
										}
									}
								}
							}
						}
					}
				}
				break;
			}
			case "remedy":
			{
				if(player.getRole() == ClassRole.PALADIN) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: remedy")) {
								if(target instanceof Player) {
									Player tarP = (Player) target;
									if(GetQueuePlayer(tarP) != null) {
										GamePlayer gp = GetQueuePlayer(tarP);
										if(gp.getHarmfulPotionEffects().size() > 0) {
											for(PotionEffect e : gp.getHarmfulPotionEffects()) {
												gp.removePotionEffect(e);
											}
											tarP.sendMessage(p.getDisplayName() + " has remedied your afflictions!");
											player.addScore(3);
											for(PotionEffect e: player.getHarmfulPotionEffects()) {
												player.removePotionEffect(e);
											}
											p.sendMessage(tarP.getDisplayName() + " had their afflictions cured! Yours are as well!");
											for(GamePlayer other : getPlayersInQueue()) {
												if(other.hasStatusEffect(StatusEffects.SOUL_LINK)) {
													if(other.getStatusEffect(StatusEffects.SOUL_LINK).getCaster() == p) {
														if(other.getHarmfulPotionEffects().size() > 0) {
															for(PotionEffect e : other.getHarmfulPotionEffects()) {
																other.removePotionEffect(e);
															}
															other.getMinecraftPlayer().sendMessage(p.getDisplayName() + " has remedied your afflictions!");
															p.sendMessage(tarP.getDisplayName() + " had their afflictions cured!");
															player.addScore(3);
														}
														break;
													}
												}
											}
										}
										int cooldown = player.hasStatusEffect(StatusEffects.AURIA_POSSESSED) ? (ability.getCooldown() / 2) : ability.getCooldown();
										ability.setIsActive(true);
										new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, cooldown);
									}
								}
							}
						}
					}
				}
				break;
			}
			case "auria's possession":
			{
				if(player.getRole() == ClassRole.PALADIN) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: auria's possession")) {
								StatusEffect se = new StatusEffect(StatusEffects.AURIA_POSSESSED, 300, p);
								player.addStatusEffect(se);
								p.sendMessage("Your body has been possessed by Auria herself! All your abilities are stronger!");
								ability.setIsActive(true);
								new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
								new StatusEffectTimer(se, p).runTaskLater(plugin, se.getDuration());
							}
						}
					}
				}
					
				break;
			}
			case "scavanger":
			{
				if(player.getRole() == ClassRole.ARCHER) {
					Random r = new Random();
					int amt = r.nextInt(5) + 1; //Add between 1 and 5 arrows to inventory
					p.getInventory().addItem(new ItemStack(Material.ARROW, amt));
					player.getAbility("scavanger").setIsActive(true);
					new AbilityTimer(p, player.getAbility("scavanger"), player.getRole()).runTaskLater(plugin, player.getAbility("scavanger").getCooldown());
				}
				break;
			}
			case "slow":
			{
				if(player.getRole() == ClassRole.ARCHER) {
					if(GetGameEntity(target) != null) {
						GameEntity tarE = GetGameEntity(target);
						tarE.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, (int) Math.round(player.getAbility("slow").getPow())));
						player.getAbility("slow").setIsActive(true);
						player.addScore(5);
						new AbilityTimer(p, player.getAbility("slow"), player.getRole()).runTaskLater(plugin, player.getAbility("slow").getCooldown());
					}
				}
				break;
			}
			case "wither":
			{
				if(player.getRole() == ClassRole.ARCHER) {
					if(GetGameEntity(target) != null) {
						GameEntity tarE = GetGameEntity(target);
						tarE.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, (int) Math.round(player.getAbility("wither").getPow())));
						player.getAbility("wither").setIsActive(true);
						player.addScore(7);
						new AbilityTimer(p, player.getAbility("wither"), player.getRole()).runTaskLater(plugin, player.getAbility("wither").getCooldown());
					}
				}
				break;
			}
			case "strike weakness":
			{
				if(player.getRole() == ClassRole.ARCHER) {
					if(GetGameEntity(target) != null) {
						damage *= ability.getPow();
						ability.setIsActive(true);
						new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
						return damage;
					}
				}
				break;
			}
			case "woodman's mark":
			{
				if(player.getRole() == ClassRole.ARCHER) {
					if(GetGameEntity(target) != null) {
						GameEntity ge = GetGameEntity(target);
						StatusEffect se = new StatusEffect(StatusEffects.WOODMAN_MARK, 100, p);
						ge.addStatusEffect(se);
						p.sendMessage(target.getName() + " has been marked!");
						ability.setIsActive(true);
						new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
						new StatusEffectTimer(se, target).runTaskLater(plugin, se.getDuration());
					}
				}
				break;
			}
			case "triple shot":
			{
				if(player.getRole() == ClassRole.ARCHER) {
					if(GetGameEntity(target) != null) {
						GameEntity ge = GetGameEntity(target);
						damage *= 1.875d;
						ability.setIsActive(true);
						new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
						return damage;
					}
				}
				break;
			}
			case "fire charge":
			{
				if(player.getRole() == ClassRole.MAGE) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: fire charge")) {
								Fireball f = p.getWorld().spawn(p.getEyeLocation(), SmallFireball.class);
								f.setYield(0.0f);
								f.setDirection(p.getEyeLocation().getDirection().multiply(1.5d));
								f.setShooter(p);
								player.getAbility("fire charge").setIsActive(true);
								player.addScore((int)ability.getPow());
								int cooldown = player.hasStatusEffect(StatusEffects.ARCANE) ? 80 : ability.getCooldown();
								new AbilityTimer(p, player.getAbility("fire charge"), player.getRole()).runTaskLater(plugin, cooldown);
							}
						}
					}
				}
				break;
			}
			case "nuke":
			{
				if(player.getRole() == ClassRole.MAGE) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: nuke")) {
								Location loc = p.getTargetBlock((Set<Material>) null, 25).getLocation();
								p.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 12f, true, false);
								player.getAbility("nuke").setIsActive(true);
								player.addScore(18);
								int cooldown = player.hasStatusEffect(StatusEffects.ARCANE) ? 80 : ability.getCooldown();
								new AbilityTimer(p, player.getAbility("nuke"), player.getRole()).runTaskLater(plugin, cooldown);
							}
						}
					}
				}
				break;
			}
			case "mage's exchange":
			{
				if(player.getRole() == ClassRole.MAGE) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: mage's exchange")) {
								StatusEffect se;
								if(player.hasStatusEffect(StatusEffects.ARCANE)) {
									se = new StatusEffect(StatusEffects.FLIGHT, 50, p);
								}
								else {
									se = new StatusEffect(StatusEffects.FLIGHT, 30, p);
								}
								p.setAllowFlight(true);
								double cost = p.getHealth() * 0.1d;
								if(cost < 0.5d) {
									cost = 0.5d;
								}
								p.setHealth(p.getHealth() - cost);
								player.addStatusEffect(se);
								p.sendMessage("Your blood was exchanged for some flight!");
								ability.setIsActive(true);
								int cooldown = player.hasStatusEffect(StatusEffects.ARCANE) ? 80 : ability.getCooldown();
								new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, cooldown);
								new StatusEffectTimer(se, p).runTaskLater(plugin, se.getDuration());
							}
						}
					}
				}
				break;
			}
			case "hex":
			{
				if(player.getRole() == ClassRole.MAGE) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: hex")) {
								Location loc = p.getTargetBlock((Set<Material>) null, 35).getLocation();
								List<GameEntity> hitEnemies = new ArrayList<GameEntity>();
								for(Entity ent : loc.getChunk().getEntities()) {
									if(ent instanceof LivingEntity && loc.distance(ent.getLocation()) <= 20) {
										LivingEntity le = (LivingEntity) ent;
										if(GetGameEntity(le) != null) {
											hitEnemies.add(GetGameEntity(le));
										}
									}
								}
								double multi;
								double power;
								double dmg;
								BigDecimal b;
								if(player.hasStatusEffect(StatusEffects.ARCANE)) {
									multi = (1.0d + (0.5d * hitEnemies.size()));
									power = ability.getPow() * 2.0d;
								}
								else {
									multi = (1.0d + (0.25d * hitEnemies.size()));
									power = ability.getPow();
								}
								dmg = (power * multi);
								double tDmg;
								double totalDmg = 0.0d;
								for(GameEntity ge : hitEnemies) {
									tDmg = ge.getTakenDamage(dmg, AttackType.MAGICAL);
									b = new BigDecimal(tDmg).setScale(2, RoundingMode.HALF_EVEN);
									totalDmg += b.doubleValue();
									ge.getMinecraftEntity().damage(b.doubleValue());
									ge.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 0));
								}
								p.getWorld().spawnEntity(loc, EntityType.SPLASH_POTION);
								p.sendMessage("Your hex ability hit " + hitEnemies.size() + " creature(s)! " + totalDmg + " magic damage was dealt overall!");
								ability.setIsActive(true);
								int cooldown = player.hasStatusEffect(StatusEffects.ARCANE) ? 80 : ability.getCooldown();
								new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, cooldown);
							}
						}
					}
				}
				break;
			}
			case "firestorm":
			{
				if(player.getRole() == ClassRole.MAGE) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: firestorm")) {
								Fireball[] fireballs = new Fireball[5];
								for(int i = 0; i < fireballs.length; i++) {
									fireballs[i] = p.getWorld().spawn(p.getEyeLocation(), SmallFireball.class);
									fireballs[i].setYield(0.0f);
									fireballs[i].setDirection(p.getEyeLocation().getDirection().multiply((1.0d + (0.25d * i))));
									fireballs[i].setShooter(p);
								}
								ability.setIsActive(true);
								player.addScore((int)ability.getPow());
								int cooldown = player.hasStatusEffect(StatusEffects.ARCANE) ? 80 : ability.getCooldown();
								new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, cooldown);
							}
						}
					}
				}
			}
			case "arcane form":
			{
				if(player.getRole() == ClassRole.MAGE) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: arcane form")) {
								StatusEffect se = new StatusEffect(StatusEffects.ARCANE, 200, p);
								player.addStatusEffect(se);
								p.sendMessage(ability.getDisplayName() + " has been activated!");
								new AbilityTimer(p, ability, player.getRole()).runTaskLater(plugin, ability.getCooldown());
								new StatusEffectTimer(se, p).runTaskLater(plugin, se.getDuration());
							}
						}
					}
				}
				break;
			}
			case "death's grasp":
			{
				if(player.getRole() == ClassRole.NECROMANCER) {
					p.setHealth(10.0d);
					p.setMaxHealth(10.0d);
					p.sendMessage(ability.getDisplayName() + " has brought you back from death!");
					ability.setIsActive(true);
				}
				break;
			}
			case "summon zombie":
			{
				if(player.getRole() == ClassRole.NECROMANCER) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: summon zombie")) {
								if(!player.hasEntityInSummon(EntityType.ZOMBIE)) {
									PlayerSummonLivingEntity(player, EntityType.ZOMBIE);
								}
								else {
									Location loc = p.getTargetBlock((Set<Material>) null, 0).getLocation();
									for(Entity ent : loc.getChunk().getEntities()) {
										if(ent instanceof Creature && (loc.distance(ent.getLocation()) <= 2)) {
											LivingEntity le = (LivingEntity) ent;
											if(GlobalArena.GetGameEntity(le) != null) {
												Creature c = (Creature) player.getEntityInSummon(EntityType.ZOMBIE).getMinecraftEntity();
												player.getEntityInSummon(EntityType.ZOMBIE).setCurrentTarget(le, SlaveTargetReason.PLAYERCOMMAND);
												c.setTarget(le);
												break;
											}
										}
									}
								}
							}
						}
					}
				}
				break;
			}
			case "summon skeleton":
			{
				if(player.getRole() == ClassRole.NECROMANCER) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: summon skeleton")) {
								if(!player.hasEntityInSummon(EntityType.SKELETON)) {
									PlayerSummonLivingEntity(player, EntityType.SKELETON);
								}
								else {
									Location loc = p.getTargetBlock((Set<Material>) null, 0).getLocation();
									for(Entity ent : loc.getChunk().getEntities()) {
										if(ent instanceof Creature && (loc.distance(ent.getLocation()) <= 2)) {
											LivingEntity le = (LivingEntity) ent;
											if(GlobalArena.GetGameEntity(le) != null) {
												Creature c = (Creature) player.getEntityInSummon(EntityType.SKELETON).getMinecraftEntity();
												player.getEntityInSummon(EntityType.SKELETON).setCurrentTarget(le, SlaveTargetReason.PLAYERCOMMAND);
												c.setTarget(le);
												break;
											}
										}
									}
								}
							}
						}
					}
				}
				break;
			}
			case "summon witch":
			{
				if(player.getRole() == ClassRole.NECROMANCER) {
					if(p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						if(p.getInventory().getItemInMainHand().hasItemMeta()) {
							if(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("spell: summon witch")) {
								if(!player.hasEntityInSummon(EntityType.WITCH)) {
									PlayerSummonLivingEntity(player, EntityType.WITCH);
								}
								else {
									Location loc = p.getTargetBlock((Set<Material>) null, 0).getLocation();
									for(Entity ent : loc.getChunk().getEntities()) {
										if(ent instanceof Creature && (loc.distance(ent.getLocation()) <= 2)) {
											LivingEntity le = (LivingEntity) ent;
											if(GlobalArena.GetGameEntity(le) != null) {
												Creature c = (Creature) player.getEntityInSummon(EntityType.WITCH).getMinecraftEntity();
												player.getEntityInSummon(EntityType.WITCH).setCurrentTarget(le, SlaveTargetReason.PLAYERCOMMAND);
												c.setTarget(le);
												break;
											}
										}
									}
								}
							}
						}
					}
				}
				break;
			}
		}
		return damage;
	}
	
	public static void PlayerSummonLivingEntity(GamePlayer player, EntityType type)
	{
		Player p = player.getMinecraftPlayer();
		Location loc = p.getLocation().add(0.0d, 2.5d, 0.0d);
		switch(type) 
		{
			case ZOMBIE:
			{
				Zombie z = (Zombie) p.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
				player.addSummon(z);
				player.getAbility("summon zombie").setIsActive(true);
				new AbilityTimer(p, player.getAbility("summon zombie"), player.getRole()).runTaskLater(plugin, player.getAbility("summon zombie").getCooldown());
				break;
				
			}
			case SKELETON:
			{
				Skeleton s = (Skeleton) p.getWorld().spawnEntity(loc, EntityType.SKELETON);
				player.addSummon(s);
				player.getAbility("summon skeleton").setIsActive(true);
				new AbilityTimer(p, player.getAbility("summon skeleton"), player.getRole()).runTaskLater(plugin, player.getAbility("summon skeleton").getCooldown());
				break;
			}
			case WITCH:
			{
				Witch w = (Witch) p.getWorld().spawnEntity(loc, EntityType.WITCH);
				player.addSummon(w);
				player.getAbility("summon witch").setIsActive(true);
				new AbilityTimer(p, player.getAbility("summon witch"), player.getRole()).runTaskLater(plugin, player.getAbility("summon witch").getCooldown());
				break;
			}
			case SNOWMAN:
			{
				Snowman s = (Snowman) p.getWorld().spawnEntity(loc, EntityType.SNOWMAN);
				player.addSummon(s);
				player.getAbility("summon snowman").setIsActive(true);
				new AbilityTimer(p, player.getAbility("summon snowman"), player.getRole()).runTaskLater(plugin, player.getAbility("summon snowman").getCooldown());
				break;
			}
			default:
			{
				break;
			}
		}
	}
	
	public static void PlayerBuy(Player p, ItemStack currency, int price, ItemStack bought, String... tier) {
		ItemStack[] contents = p.getInventory().getContents();
		GamePlayer gp = GetQueuePlayer(p);
		if(p.getInventory().containsAtLeast(currency, price)) {
			for(int x = 0; x < contents.length; x++) {
				if(contents[x] != null) {
					if(contents[x].getType() == currency.getType()) {
						if(contents[x].getAmount() > price) {
							contents[x].setAmount(contents[x].getAmount() - price);
							p.getInventory().setContents(contents);
							if(tier.length == 1) {
								StatItem s = new StatItem(bought.getType(), gp);
								s.rollStats(gp, tier[0]);
								p.getInventory().addItem(s);
							}
							else {
								p.getInventory().addItem(bought);
							}
							p.updateInventory();
							break;
						}
						else if(contents[x].getAmount() == price) {
							p.getInventory().remove(contents[x]);
							if(tier.length == 1) {
								StatItem s = new StatItem(bought.getType(), gp);
								s.rollStats(gp, tier[0]);
								p.getInventory().addItem(s);
							}
							else {
								p.getInventory().addItem(bought);
							}
							p.updateInventory();
							break;
						}
					}
				}
			}
		}
	}
	
	public static void ResetArena() {
		Round = 0;
		startPpl = 0;
		toSpectatorPlayers("The arena has finished. Spectating mode is over.");
		for(int i = 0; i < SpectatorPlayers.size(); i++) {
			RemoveSpectatorPlayer(SpectatorPlayers.get(i));
		}
		for(int i = 0; i < CreaturesInArena.size(); i++) {
			CreaturesInArena.get(i).getMinecraftEntity().setHealth(0.0d);
			CreaturesInArena.remove(i);
		}
		for(Block b : explodeBlocks) {
			inWorld.getBlockAt(b.getLocation()).setType(b.getType());
		}
	}
	
	private static void SpawnInArena(int[] level, EntityType... entity) {
		Random r = new Random();
		for(int i = 0; i < startPpl; i++) {
			for(int x = 0; x < entity.length; x++) {
				if(Round < 21) {
					int pick = r.nextInt(100);
					if(pick > 77 ) {
						new DelaySpawnTask(monsterSpawn1.get(0), entity[x], level[x]).runTaskLater(GlobalArena.plugin, 20 * x);
					}
					else if(pick > 34){
						new DelaySpawnTask(monsterSpawn1.get(1), entity[x], level[x]).runTaskLater(GlobalArena.plugin, 20 * x);
					}
					else {
						new DelaySpawnTask(monsterSpawn1.get(2), entity[x], level[x]).runTaskLater(GlobalArena.plugin, 20 * x);
					}
				}
				else if (Round < 41) {
					
				}
			}
		}
	}
	
	public static boolean isShopRound() {
		if(Round == 0) {
			return false;
		}
		if(Round % 5 == 0) {
			return true;
		}
		return false;
	}
	
	public static World getWorld() {
		return inWorld;
	}
	
	public static boolean getHasStarted() {
		return HasStarted;
	}
	
	public static void setHasStarted(boolean HasStarted) {
		GlobalArena.HasStarted = HasStarted;
		if(HasStarted) {
			if(Round == 0) {
				startPpl = PlayersInQueue.size();
			}
			for(int i = 0; i < PlayersInQueue.size(); i++) {
				PlayersInQueue.get(i).setIsReady(false);
			}
		}
	}
	
	public static int getMaxSize()
	{
		return MaxSize;
	}
	
	public static GamePlayer GetQueuePlayer(Player p) {
		for(int i = 0; i < PlayersInQueue.size(); i++) {
			if(PlayersInQueue.get(i).getMinecraftPlayer() == p) {
				return PlayersInQueue.get(i);
			}
		}
		return null;
	}
	
	public static GameEntity GetGameEntity(LivingEntity le) {
		for(int i = 0; i < CreaturesInArena.size(); i++) {
			if(CreaturesInArena.get(i).getMinecraftEntity() == le) {
				return CreaturesInArena.get(i);
			}
		}
		return null;
	}
	
	public static void AddNewSpectatorPlayer(Player p) {
		if(GetQueuePlayer(p) != null) {
			PlayersInQueue.remove(GlobalArena.GetQueuePlayer(p));
		}
		SpectatorPlayers.add(p);
	}
	
	public static void AddNewQueuePlayer(Player p) {
		if(GetSpectatorPlayer(p) != null) {
			SpectatorPlayers.remove(GlobalArena.GetSpectatorPlayer(p));
		}
		PlayersInQueue.add(new GamePlayer(p));
	}
	
	public static void RemoveQueuePlayer(Player p) {
		GamePlayer gp = GetQueuePlayer(p);
		p.teleport(gp.getSpawn());
		p.getInventory().clear();
		ResetPlayerBlocks(p);
		PlayersInQueue.remove(gp);
	}
	
	public static void ResetPlayerBlocks(Player p) {
		GamePlayer gp = GetQueuePlayer(p);
		for(Block b : gp.getPlacedBlocks()) {
			b.setType(Material.AIR);
		}
	}
	
	public static Player GetSpectatorPlayer(Player p) {
		for(int i = 0; i < SpectatorPlayers.size(); i++) {
			if(SpectatorPlayers.get(i) == p) {
				return SpectatorPlayers.get(i);
			}
		}
		return null;
	}
	
	public static void RemoveSpectatorPlayer(Player p) {
		p.teleport(new Location(p.getWorld(), 51.5d, 81.0d, 252.0d));
		p.getInventory().clear();
		SpectatorPlayers.remove(p);
	}
	
	public static void addExplodedBlocks(List<Block> allExplodedBlocks) {
		for(Block b : allExplodedBlocks) {
			explodeBlocks.add(b);
		}
	}
	
	public static List<GamePlayer> getPlayersInQueue() {
		return PlayersInQueue;
	}
	
	public static List<Player> getSpectatorPlayers() {
		return SpectatorPlayers;
	}
	
	public static List<GameEntity> getCreaturesInArena() {
		return CreaturesInArena;
	}
}
