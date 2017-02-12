package com.gmail.jpk.stu.Timers;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.jpk.stu.Arena.GlobalArena;
import com.gmail.jpk.stu.Entities.GameEntity;
import com.gmail.jpk.stu.Entities.GamePlayer;
import com.gmail.jpk.stu.Entities.StatusEffect;
import com.gmail.jpk.stu.Entities.StatusEffect.StatusEffects;

public class StatusEffectTimer extends BukkitRunnable {

	private StatusEffect se;
	private LivingEntity le;
	
	/***
	 * 
	 * @param se The status effect this references with.
	 * @param le The affected entity of the status effect.
	 */
	public StatusEffectTimer(StatusEffect se, LivingEntity le) {
		this.se = se;
		this.le = le;
	}
	
	public void run() {
		if(le instanceof Player) {
			Player p = (Player) le;
			if(GlobalArena.GetQueuePlayer(p) != null) {
				GamePlayer gp = GlobalArena.GetQueuePlayer(p);
				gp.removeStatusEffect(se.getEffect());
				p.sendMessage("Your " + ChatColor.GREEN + se.getEffect().name() + ChatColor.WHITE + " has worn off!");
				if(se.getCaster() instanceof Player && se.getCaster() != p) {
					Player caster = (Player) se.getCaster();
					caster.sendMessage(p.getDisplayName() + "'s " + ChatColor.GREEN + se.getEffect().name() + ChatColor.WHITE + " has worn off!");
				}
				if(se.getEffect() == StatusEffects.FLIGHT) {
					p.setAllowFlight(false);
				}
			}
		}
		else if(GlobalArena.GetGameEntity(le) != null) {
			GameEntity ge = GlobalArena.GetGameEntity(le);
			ge.removeStatusEffect(se.getEffect());
		}
	}
}
