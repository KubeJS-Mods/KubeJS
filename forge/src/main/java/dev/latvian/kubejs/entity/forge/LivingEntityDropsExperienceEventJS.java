package dev.latvian.kubejs.entity.forge;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityEventJS;
import dev.latvian.kubejs.player.PlayerJS;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import org.jetbrains.annotations.Nullable;

public class LivingEntityDropsExperienceEventJS extends LivingEntityEventJS {
	private final LivingExperienceDropEvent event;

	public LivingEntityDropsExperienceEventJS(LivingExperienceDropEvent event) {
		this.event = event;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(event.getEntity());
	}

	@Nullable
	public PlayerJS<?> getAttacker() {
		return getWorld().getPlayer(event.getAttackingPlayer());
	}

	public int getVanillaXp() {
		return event.getOriginalExperience();
	}

	public int getXp() {
		return event.getDroppedExperience();
	}

	public void setXp(int xp) {
		event.setDroppedExperience(xp);
	}
}
