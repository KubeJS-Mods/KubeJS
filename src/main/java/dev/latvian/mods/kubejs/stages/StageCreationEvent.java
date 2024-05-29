package dev.latvian.mods.kubejs.stages;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;

public class StageCreationEvent extends PlayerEvent implements ICancellableEvent {
	private Stages stages;

	StageCreationEvent(Player p) {
		super(p);
	}

	public void setPlayerStages(Stages s) {
		stages = s;
	}

	@Nullable
	public Stages getPlayerStages() {
		return stages;
	}
}
