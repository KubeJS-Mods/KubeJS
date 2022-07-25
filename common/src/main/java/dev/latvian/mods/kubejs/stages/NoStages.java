package dev.latvian.mods.kubejs.stages;

import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Collections;

public class NoStages extends Stages {
	public static final NoStages NULL_INSTANCE = new NoStages(null);

	private NoStages(Player player) {
		super(player);
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
		return Collections.emptyList();
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
