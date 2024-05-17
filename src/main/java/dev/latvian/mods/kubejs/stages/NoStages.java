package dev.latvian.mods.kubejs.stages;

import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Set;

public record NoStages(Player player) implements Stages {
	public static final NoStages NULL_INSTANCE = new NoStages(null);

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean addNoUpdate(String stage) {
		return false;
	}

	@Override
	public boolean removeNoUpdate(String stage) {
		return false;
	}

	@Override
	public Collection<String> getAll() {
		return Set.of();
	}

	@Override
	public boolean has(String stage) {
		return false;
	}

	@Override
	public boolean clear() {
		return false;
	}

	@Override
	public void sync() {
	}

	@Override
	public void replace(Collection<String> stages) {
	}
}
