package com.gmail.jpk.stu.Listeners;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftCreeper;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.jpk.stu.Arena.Arena;
import com.gmail.jpk.stu.Arena.GlobalArena;
import com.gmail.jpk.stu.Entities.AttackType;
import com.gmail.jpk.stu.Entities.ClassRole;
import com.gmail.jpk.stu.Entities.GameEntity;
import com.gmail.jpk.stu.Entities.GameEntityType;
import com.gmail.jpk.stu.Entities.GamePlayer;
import com.gmail.jpk.stu.Entities.SlaveEntity;
import com.gmail.jpk.stu.Entities.SlaveEntity.SlaveTargetReason;
import com.gmail.jpk.stu.Entities.StatusEffect.StatusEffects;
import com.gmail.jpk.stu.Timers.StatChangeTimer;


public class DamageListener implements Listener {
	
	private Plugin plugin;
	
	public DamageListener(Arena plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e ) {
		if(e.getCause() == DamageCause.BLOCK_EXPLOSION) {
			if(e.getEntity() instanceof LivingEntity) {
				LivingEntity le = (LivingEntity) e.getEntity();
				if(GlobalArena.GetGameEntity(le) != null) {
					GameEntity ge = GlobalArena.GetGameEntity(le);
					double dmg = ClassRole.MAGE.getAbility("nuke").getPow();
					dmg = ge.getTakenDamage(dmg, AttackType.MAGICAL);
					BigDecimal b = new BigDecimal(dmg).setScale(2, RoundingMode.HALF_EVEN);
					e.setDamage(b.doubleValue());
				}
				else if(e.getEntity() instanceof Player) {
					Player p = (Player) e.getEntity();
					if(GlobalArena.GetQueuePlayer(p) != null) {
						GamePlayer gp = GlobalArena.GetQueuePlayer(p);
						e.setDamage(0.0d);
					}
				}
			}
		}
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(GlobalArena.GetQueuePlayer(p) != null) {
				GamePlayer gp = GlobalArena.GetQueuePlayer(p);
				if(gp.hasStatusEffect(StatusEffects.INVULNERABLE)) {
					e.setCancelled(true);
				}
				else if(gp.hasStatusEffect(StatusEffects.HEROIC_LINK)) {
					Player caster = (Player) gp.getStatusEffect(StatusEffects.HEROIC_LINK).getCaster();
					double dmg = e.getDamage();
					if(GlobalArena.GetQueuePlayer(caster) != null) {
						GamePlayer cgp = GlobalArena.GetQueuePlayer(caster);
						if(cgp.getRole() == ClassRole.WARRIOR) {
							BigDecimal b;
							if((caster.getLevel() >= cgp.getAbility("vigour").getReqLvl()) && !(cgp.getAbility("vigour").getIsActive())) {
								GlobalArena.PlayerUseAbility(cgp, null, cgp.getAbility("vigour"));
							}
							dmg = gp.getTakenDamage(dmg, AttackType.PHYSICAL);
							b = new BigDecimal(dmg).setScale(2, RoundingMode.HALF_EVEN);
							caster.damage(b.doubleValue());
						}
						else {
							caster.damage(dmg);
						}
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(GlobalArena.GetQueuePlayer(p) != null) {
				GamePlayer gp = GlobalArena.GetQueuePlayer(p);
				if(e.getDamager() instanceof LivingEntity) {
					LivingEntity le = (LivingEntity) e.getDamager();
					if(GlobalArena.GetGameEntity(le) != null) {
						GameEntity ge = GlobalArena.GetGameEntity(le);
						boolean isReversed = ge.hasType(GameEntityType.REVERSAL);
						double dmg;
						BigDecimal b;
						if(le instanceof Creeper) {
							if(isReversed) {
								dmg = ge.getAtk();
								dmg = gp.getTakenDamage(dmg, AttackType.PHYSICAL);
								b = new BigDecimal(dmg).setScale(2, RoundingMode.HALF_EVEN);
								p.sendMessage("You took " + ChatColor.RED + b.doubleValue() + ChatColor.WHITE + " damage!");
							}
							else {
								dmg = ge.getMagic();
								dmg = gp.getTakenDamage(dmg, AttackType.MAGICAL);
								b = new BigDecimal(dmg).setScale(2, RoundingMode.HALF_EVEN);
								p.sendMessage("You took " + ChatColor.RED + b.doubleValue() + ChatColor.WHITE + " magic damage!");
							}
							e.setDamage(b.doubleValue());
							
							if(ge.hasType(GameEntityType.GUNKER)) {
								gp.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 0));
							}
							if(ge.hasType(GameEntityType.BLINDER)) {
								gp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
							}
							if(ge.hasType(GameEntityType.CONFUSER)) {
								gp.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 140, 0));
							}
							if(ge.hasType(GameEntityType.POISONOUS)) {
								gp.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
							}
							if(ge.hasType(GameEntityType.WITHEROUS)) {
								gp.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 0));
							}
							if(ge.hasType(GameEntityType.FEEBLER)) {
								double decrease = gp.getDefense(true) - (gp.getDefense(true) * 0.67d);
								gp.setDefense(gp.getDefense(true) - decrease);
								p.sendMessage("Your defense has been lowered!");
								new StatChangeTimer(p, "defense", decrease).runTaskLater(plugin, 100);
							}
							if(ge.hasType(GameEntityType.HINDERER)) {
								double decrease = gp.getResistance(true) - (gp.getResistance(true) * 0.67d);
								gp.setResistance(gp.getResistance(true) - decrease);
								p.sendMessage("Your resistance has been lowered!");
								new StatChangeTimer(p, "resistance", decrease).runTaskLater(plugin, 100);
							}
							if(ge.hasType(GameEntityType.DULLER)) {
								double decrease = gp.getAtk(true) - (gp.getAtk(true) * 0.67d);
								gp.setAtk(gp.getAtk(true) - decrease);
								p.sendMessage("Your attack has been lowered!");
								new StatChangeTimer(p, "atk", decrease).runTaskLater(plugin, 100);
							}
							if(ge.hasType(GameEntityType.DIMINISHER)) {
								double decrease = gp.getMagic(true) - (gp.getMagic(true) * 0.67d);
								gp.setMagic(gp.getMagic(true) - decrease);
								p.sendMessage("Your magic has been lowered!");
								new StatChangeTimer(p, "magic", decrease).runTaskLater(plugin, 100);
							}
						}
						else {
							int roll = new Random().nextInt(100) + 1;
							if(roll < gp.getDodgeChance()) {
								if(isReversed) {
									dmg = ge.getMagic();
									dmg = gp.getTakenDamage(dmg, AttackType.MAGICAL);
									b = new BigDecimal(dmg).setScale(2, RoundingMode.HALF_EVEN);
									p.sendMessage("You took " + ChatColor.RED + b.doubleValue() + ChatColor.WHITE + " magic damage!");
								}
								else {
									dmg = ge.getAtk();
									if(gp.getRole() == ClassRole.WARRIOR) {
										if((p.getLevel() >= gp.getAbility("vigour").getReqLvl()) && !(gp.getAbility("vigour").getIsActive())) { //Sets the vigour ability to active
											GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("vigour"));
										}
										else if((p.getLevel() >= gp.getAbility("defensive paradigm").getReqLvl()) && !(gp.getAbility("defensive paradigm").getIsActive())) { //Sets the defensive paradigm ability to active
											GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("defensive paradigm"));
										}
									}
									dmg = gp.getTakenDamage(dmg, AttackType.PHYSICAL);
									b = new BigDecimal(dmg).setScale(2, RoundingMode.HALF_EVEN);
									p.sendMessage("You took " + ChatColor.RED + b.doubleValue() + ChatColor.WHITE + " damage!");
								}
								e.setDamage(b.doubleValue());
								
								if(ge.hasType(GameEntityType.GUNKER)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 0));
								}
								if(ge.hasType(GameEntityType.BLINDER)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
								}
								if(ge.hasType(GameEntityType.CONFUSER)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 140, 0));
								}
								if(ge.hasType(GameEntityType.POISONOUS)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
								}
								if(ge.hasType(GameEntityType.WITHEROUS)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 0));
								}
								if(ge.hasType(GameEntityType.FEEBLER)) {
									double decrease = gp.getDefense(true) - (gp.getDefense(true) * 0.67d);
									gp.setDefense(gp.getDefense(true) - decrease);
									p.sendMessage("Your defense has been lowered!");
									new StatChangeTimer(p, "defense", decrease).runTaskLater(plugin, 100);
								}
								if(ge.hasType(GameEntityType.HINDERER)) {
									double decrease = gp.getResistance(true) - (gp.getResistance(true) * 0.67d);
									gp.setResistance(gp.getResistance(true) - decrease);
									p.sendMessage("Your resistance has been lowered!");
									new StatChangeTimer(p, "resistance", decrease).runTaskLater(plugin, 100);
								}
								if(ge.hasType(GameEntityType.DULLER)) {
									double decrease = gp.getAtk(true) - (gp.getAtk(true) * 0.67d);
									gp.setAtk(gp.getAtk(true) - decrease);
									p.sendMessage("Your attack has been lowered!");
									new StatChangeTimer(p, "atk", decrease).runTaskLater(plugin, 100);
								}
								if(ge.hasType(GameEntityType.DIMINISHER)) {
									double decrease = gp.getMagic(true) - (gp.getMagic(true) * 0.67d);
									gp.setMagic(gp.getMagic(true) - decrease);
									p.sendMessage("Your magic has been lowered!");
									new StatChangeTimer(p, "magic", decrease).runTaskLater(plugin, 100);
								}
								
								for(int i = 0; i < GlobalArena.getPlayersInQueue().size(); i++) {
									for(SlaveEntity se : GlobalArena.getPlayersInQueue().get(i).getAllSummons()) {
										if(se.getTargetReason() != SlaveTargetReason.PLAYERCOMMAND) {
											Creature c = (Creature) se.getMinecraftEntity();
											se.setCurrentTarget(le, SlaveTargetReason.PLAYERATTACKED);
											c.setTarget(le);
										}
									}
								}
							}
							else {
								e.setCancelled(true);
								gp.getMinecraftPlayer().sendMessage("You dodged the attack!");
							}
						}
					}
				}
				else if(e.getDamager() instanceof Arrow) {
					Arrow a = (Arrow) e.getDamager();
					if(a.getShooter() instanceof LivingEntity) {
						LivingEntity le = (LivingEntity) a.getShooter();
						if(GlobalArena.GetGameEntity(le) != null) {
							GameEntity ge = GlobalArena.GetGameEntity(le);
							int roll = new Random().nextInt(100) + 1;
							if(roll < gp.getDodgeChance()) {
								double dmg = ge.getAtk();
								BigDecimal b;
								if(gp.getRole() == ClassRole.WARRIOR) {
									if((p.getLevel() >= gp.getAbility("vigour").getReqLvl()) && !(gp.getAbility("vigour").getIsActive())) { //Sets the vigour ability to active
										GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("vigour"));
									}
									else if((p.getLevel() >= gp.getAbility("defensive paradigm").getReqLvl()) && !(gp.getAbility("defensive paradigm").getIsActive())) { //Sets the defensive paradigm ability to active
										GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("defensive paradigm"));
									}
								}
								dmg = gp.getTakenDamage(dmg, AttackType.PHYSICAL);
								b = new BigDecimal(dmg).setScale(2, RoundingMode.HALF_EVEN);
								p.sendMessage("You took " + ChatColor.RED + b.doubleValue() + ChatColor.WHITE + " damage!");
								e.setDamage(b.doubleValue());
								
								if(ge.hasType(GameEntityType.GUNKER)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 0));
								}
								if(ge.hasType(GameEntityType.BLINDER)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
								}
								if(ge.hasType(GameEntityType.CONFUSER)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 140, 0));
								}
								if(ge.hasType(GameEntityType.POISONOUS)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
								}
								if(ge.hasType(GameEntityType.WITHEROUS)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 0));
								}
								if(ge.hasType(GameEntityType.FEEBLER)) {
									double decrease = gp.getDefense(true) - (gp.getDefense(true) * 0.67d);
									gp.setDefense(gp.getDefense(true) - decrease);
									p.sendMessage("Your defense has been lowered!");
									new StatChangeTimer(p, "defense", decrease).runTaskLater(plugin, 100);
								}
								if(ge.hasType(GameEntityType.HINDERER)) {
									double decrease = gp.getResistance(true) - (gp.getResistance(true) * 0.67d);
									gp.setResistance(gp.getResistance(true) - decrease);
									p.sendMessage("Your resistance has been lowered!");
									new StatChangeTimer(p, "resistance", decrease).runTaskLater(plugin, 100);
								}
								if(ge.hasType(GameEntityType.DULLER)) {
									double decrease = gp.getAtk(true) - (gp.getAtk(true) * 0.67d);
									gp.setAtk(gp.getAtk(true) - decrease);
									p.sendMessage("Your attack has been lowered!");
									new StatChangeTimer(p, "atk", decrease).runTaskLater(plugin, 100);
								}
								if(ge.hasType(GameEntityType.DIMINISHER)) {
									double decrease = gp.getMagic(true) - (gp.getMagic(true) * 0.67d);
									gp.setMagic(gp.getMagic(true) - decrease);
									p.sendMessage("Your magic has been lowered!");
									new StatChangeTimer(p, "magic", decrease).runTaskLater(plugin, 100);
								}
							}
							else {
								e.setCancelled(true);
								gp.getMinecraftPlayer().sendMessage("You dodged the arrow!");
							}
						}
						else {
							e.setCancelled(true);
						}
					}
				}
				else if(e.getDamager() instanceof Fireball) {
					Fireball fireball = (Fireball) e.getDamager();
					if(fireball.getShooter() instanceof LivingEntity) {
						LivingEntity le = (LivingEntity) fireball.getShooter();
						if(GlobalArena.GetGameEntity(le) != null) {
							GameEntity ge = GlobalArena.GetGameEntity(le);
							int roll = new Random().nextInt(100) + 1;
							if(roll < gp.getDodgeChance()) {
								double dmg = 4.0d + ge.getMagic();
								BigDecimal b;
								dmg = gp.getTakenDamage(dmg, AttackType.MAGICAL);
								b  = new BigDecimal(dmg).setScale(2, RoundingMode.HALF_EVEN);
								p.sendMessage("You took " + ChatColor.RED + b.doubleValue() + ChatColor.WHITE + " magic damage!");
								e.setDamage(b.doubleValue());
								
								if(ge.hasType(GameEntityType.GUNKER)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 0));
								}
								if(ge.hasType(GameEntityType.BLINDER)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
								}
								if(ge.hasType(GameEntityType.CONFUSER)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 140, 0));
								}
								if(ge.hasType(GameEntityType.POISONOUS)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
								}
								if(ge.hasType(GameEntityType.WITHEROUS)) {
									gp.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 0));
								}
								if(ge.hasType(GameEntityType.FEEBLER)) {
									double decrease = gp.getDefense(true) - (gp.getDefense(true) * 0.67d);
									gp.setDefense(gp.getDefense(true) - decrease);
									p.sendMessage("Your defense has been lowered!");
									new StatChangeTimer(p, "defense", decrease).runTaskLater(plugin, 100);
								}
								if(ge.hasType(GameEntityType.HINDERER)) {
									double decrease = gp.getResistance(true) - (gp.getResistance(true) * 0.67d);
									gp.setResistance(gp.getResistance(true) - decrease);
									p.sendMessage("Your resistance has been lowered!");
									new StatChangeTimer(p, "resistance", decrease).runTaskLater(plugin, 100);
								}
								if(ge.hasType(GameEntityType.DULLER)) {
									double decrease = gp.getAtk(true) - (gp.getAtk(true) * 0.67d);
									gp.setAtk(gp.getAtk(true) - decrease);
									p.sendMessage("Your attack has been lowered!");
									new StatChangeTimer(p, "atk", decrease).runTaskLater(plugin, 100);
								}
								if(ge.hasType(GameEntityType.DIMINISHER)) {
									double decrease = gp.getMagic(true) - (gp.getMagic(true) * 0.67d);
									gp.setMagic(gp.getMagic(true) - decrease);
									p.sendMessage("Your magic has been lowered!");
									new StatChangeTimer(p, "magic", decrease).runTaskLater(plugin, 100);
								}
							}
						}
						else {
							e.setCancelled(true);
							gp.getMinecraftPlayer().sendMessage("You dodged the fireball!");
						}
					}
				}
				if(gp.hasStatusEffect(StatusEffects.INVULNERABLE)) {
					e.setCancelled(true);
				}
				else if(gp.hasStatusEffect(StatusEffects.HEROIC_LINK)) {
					Player caster = (Player) gp.getStatusEffect(StatusEffects.HEROIC_LINK).getCaster();
					double dmg = e.getDamage();
					if(GlobalArena.GetQueuePlayer(caster) != null) {
						GamePlayer cgp = GlobalArena.GetQueuePlayer(caster);
						if(cgp.getRole() == ClassRole.WARRIOR) {
							BigDecimal b;
							if((caster.getLevel() >= cgp.getAbility("vigour").getReqLvl()) && !(cgp.getAbility("vigour").getIsActive())) {
								GlobalArena.PlayerUseAbility(cgp, null, cgp.getAbility("vigour"));
							}
							b = new BigDecimal(dmg).setScale(2, RoundingMode.HALF_EVEN);
							caster.damage(b.doubleValue());
						}
						else {
							caster.damage(dmg);
						}
						e.setCancelled(true);
					}
				}
				else {
					if(gp.getRole() == ClassRole.WARRIOR) {
						double dmg = e.getDamage();
						BigDecimal b;
						if((p.getLevel() >= gp.getAbility("vigour").getReqLvl()) && !(gp.getAbility("vigour").getIsActive())) { //Sets the vigour ability to active
							GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("vigour"));
						}
						b = new BigDecimal(dmg).setScale(2, RoundingMode.HALF_EVEN);
						e.setDamage(b.doubleValue());
					}
				}
			}
		}
		else if(e.getEntity() instanceof LivingEntity) {
			LivingEntity le = (LivingEntity) e.getEntity();
			if(GlobalArena.GetGameEntity(le) != null) {
				GameEntity ge = GlobalArena.GetGameEntity(le);
				if (e.getDamager() instanceof Player) {
					Player p = (Player) e.getDamager();
					if(GlobalArena.GetQueuePlayer(p) != null) {
						GamePlayer gp = GlobalArena.GetQueuePlayer(p);
						
						double rechargeDebuff = 4.0d;
						if(p.getInventory().getItemInMainHand() != null) {
							switch(p.getInventory().getItemInMainHand().getType()) {
								case WOOD_SWORD:
									rechargeDebuff = 4.0d;
									break;
								case STONE_SWORD:
									rechargeDebuff = 5.0d;
									break;
								case IRON_SWORD:
									rechargeDebuff = 6.0d;
									break;
								case GOLD_SWORD:
									rechargeDebuff = 4.0d;
									break;
								case DIAMOND_SWORD:
									rechargeDebuff = 7.0d;
									break;
								case WOOD_AXE:
									rechargeDebuff = 7.0d;
									break;
								case STONE_AXE:
									rechargeDebuff = 9.0d;
									break;
								case IRON_AXE:
									rechargeDebuff = 9.0d;
									break;
								case GOLD_AXE:
									rechargeDebuff = 7.0d;
									break;
								case DIAMOND_AXE:
									rechargeDebuff = 9.0d;
									break;
								default:
									rechargeDebuff = 4.0d;
									break;
							}
						}
						
						double dmg = (gp.getAtk(false) * (e.getOriginalDamage(DamageModifier.BASE) / rechargeDebuff));
						BigDecimal b;
						if(gp.getRole() == ClassRole.FIGHTER) {
							if((p.getLevel() >= gp.getAbility("true damager").getReqLvl()) && !(gp.getAbility("true damager").getIsActive())) {
								dmg = GlobalArena.PlayerUseAbility(gp, le, gp.getAbility("true damager"), dmg);
							}
							if((p.getLevel() >= gp.getAbility("rage").getReqLvl()) && !(gp.getAbility("rage").getIsActive())) { //Sets the rage ability to active
								dmg = GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("rage"), dmg);
							}
							if((p.getLevel() >= gp.getAbility("blood rush").getReqLvl()) && !(gp.getAbility("blood rush").getIsActive())) { //Sets the venomous blade ability to active
								GlobalArena.PlayerUseAbility(gp, le, gp.getAbility("blood rush"));
							}
							if((p.getLevel() >= gp.getAbility("lifesteal").getReqLvl()) && !(gp.getAbility("lifesteal").getIsActive())) { // Sets the lifesteal ability to active
								GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("lifesteal"));
							}
							if((p.getLevel() >= gp.getAbility("fervor").getReqLvl()) && !(gp.getAbility("fervor").getIsActive())) { //Sets the fervor ability to active
								GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("fervor"));
							}
							if((p.getLevel() >= gp.getAbility("death axe").getReqLvl()) && !(gp.getAbility("death axe").getIsActive())) { //Sets the death axe ability to active
								GlobalArena.PlayerUseAbility(gp, le, gp.getAbility("death axe"));
							}
						}
						else if(gp.getRole() == ClassRole.WARRIOR) {
							if((p.getLevel() >= gp.getAbility("victory smash").getReqLvl()) && !(gp.getAbility("victory smash").getIsActive())) { //Sets the victory smash ability to active
								dmg = GlobalArena.PlayerUseAbility(gp, null, gp.getAbility("victory smash"), dmg);
							}
						}
						int roll = new Random().nextInt(100) + 1;
						plugin.getLogger().info("Roll: " + roll + " Crit: " + gp.getCriticalChance());
						double critMulti = 0.0d;
						if(roll > gp.getCriticalChance()) {
							critMulti = 1.0d + (gp.getCritical(false) / 100.0d);
							dmg *= critMulti;
						}
						dmg = ge.getTakenDamage(dmg, AttackType.PHYSICAL);
						b = new BigDecimal(dmg).setScale(2, RoundingMode.HALF_EVEN);
						if(critMulti > 0.0d) {
							p.sendMessage("You dealt " + ChatColor.DARK_RED + b.doubleValue() + ChatColor.WHITE + " damage!");
						}
						else {
							p.sendMessage("You dealt " + ChatColor.GOLD + b.doubleValue() + ChatColor.WHITE + " damage!");
						}
						e.setDamage(b.doubleValue());
						gp.addScore((int) e.getDamage());
					}
				}
				else if(e.getDamager() instanceof Arrow) {
					Arrow a = (Arrow) e.getDamager();
					if(a.getShooter() instanceof Player) {
						Player p = (Player) a.getShooter();
						if(GlobalArena.GetQueuePlayer(p) != null) {
							GamePlayer gp = GlobalArena.GetQueuePlayer(p);
							if(gp.getRole() == ClassRole.ARCHER) {
								double dmg = (gp.getAtk(false) * 4.0d);
								BigDecimal b;
								if((p.getLevel() >= gp.getAbility("slow").getReqLvl()) && !(gp.getAbility("slow").getIsActive())) { //Sets the  slow ability to active
									GlobalArena.PlayerUseAbility(gp, le, gp.getAbility("slow"));
								}
								if((p.getLevel() >= gp.getAbility("wither").getReqLvl()) && !(gp.getAbility("wither").getIsActive())) { //Sets the wither ability to active
									GlobalArena.PlayerUseAbility(gp, le, gp.getAbility("wither"));
								}
								if((p.getLevel() >= gp.getAbility("strike weakness").getReqLvl()) && !(gp.getAbility("strike weakness").getIsActive())) { //Sets the strike weakness ability to active
									dmg = GlobalArena.PlayerUseAbility(gp, le, gp.getAbility("strike weakness"), dmg);
								}
								if((p.getLevel() >= gp.getAbility("woodman's mark").getReqLvl()) && !(gp.getAbility("woodman's mark").getIsActive())) { //Sets the woodman's mark ability to active
									GlobalArena.PlayerUseAbility(gp, le, gp.getAbility("woodman's mark"));
								}
								if((p.getLevel() >= gp.getAbility("triple shot").getReqLvl()) && !(gp.getAbility("triple shot").getIsActive())) { //Sets the triple shot ability to active
									dmg = GlobalArena.PlayerUseAbility(gp, le, gp.getAbility("triple shot"), dmg);
								}
								int roll = new Random().nextInt(100) + 1;
								double critMulti = 0.0d;
								if(roll > gp.getCriticalChance()) {
									critMulti = 1.0d + (gp.getCritical(false) / 100.0d);
									dmg *= critMulti;
								}
								dmg = ge.getTakenDamage(dmg, AttackType.PHYSICAL);
								b = new BigDecimal(dmg).setScale(2, RoundingMode.HALF_EVEN);
								if(critMulti > 0.0d) {
									p.sendMessage("You dealt " + ChatColor.DARK_RED + b.doubleValue() + ChatColor.WHITE + " damage!");
								}
								else {
									p.sendMessage("You dealt " + ChatColor.GOLD + b.doubleValue() + ChatColor.WHITE + " damage!");
								}
								e.setDamage(b.doubleValue());
								
								if(ge.hasStatusEffect(StatusEffects.WOODMAN_MARK)) {
									if(ge.getStatusEffect(StatusEffects.WOODMAN_MARK).getCaster() == p) {
										for(Entity ent : le.getLocation().getChunk().getEntities()) {
											if(ent instanceof LivingEntity && (le.getLocation().distance(ent.getLocation()) <= 6)) {
												LivingEntity lent = (LivingEntity) ent;
												if(lent instanceof Player) {
													continue;
												}
												if(lent == le) {
													continue;
												}
												lent.damage((b.doubleValue() * gp.getAbility("woodman's mark").getPow()), p);
											}
										}
									}
								}
							}
						}
					}
				}
				else if(e.getDamager() instanceof Fireball) {
					Fireball f = (Fireball) e.getDamager();
					if(f.getShooter() instanceof Player) {
						Player p = (Player) f.getShooter();
						if(GlobalArena.GetQueuePlayer(p) != null) {
							GamePlayer gp = GlobalArena.GetQueuePlayer(p);
							if(gp.getRole() == ClassRole.MAGE) {
								double dmg;
								BigDecimal b;
								if(gp.hasStatusEffect(StatusEffects.ARCANE)) {
									dmg = (gp.getAbility("fire charge").getPow() * 2.0d);
								}
								else {
									dmg = gp.getAbility("fire charge").getPow();
								}
								dmg += gp.getMagic(false);
								int roll = new Random().nextInt(100) + 1;
								double critMulti = 0.0d;
								if(roll > gp.getCriticalChance()) {
									critMulti = 1.0d + (gp.getCritical(false) / 100.0d);
									dmg *= critMulti;
								}
								dmg = ge.getTakenDamage(dmg, AttackType.MAGICAL);
								b = new BigDecimal(dmg).setScale(2, RoundingMode.HALF_EVEN);
								if(critMulti > 0.0d) {
									p.sendMessage("You dealt " + ChatColor.DARK_RED + b.doubleValue() + ChatColor.WHITE + " magic damage!");
								}
								else {
									p.sendMessage("You dealt " + ChatColor.GOLD + b.doubleValue() + ChatColor.WHITE + " magic damage!");
								}
								e.setDamage(dmg);
							}
						}
					}
				}
			}
		}
	}

}
