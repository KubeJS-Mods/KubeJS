package dev.latvian.mods.kubejs.stages;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public record TagWrapperStages(Player player) implements Stages {
	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean addNoUpdate(String stage) {
		return player.addTag(stage);
	}

	@Override
	public boolean removeNoUpdate(String stage) {
		return player.removeTag(stage);
	}

	@Override
	public Collection<String> getAll() {
		return player.getTags();
	}

	@Override
	public boolean clear() {
		if (!player.getTags().isEmpty()) {
			player.getTags().clear();
			sync();
			return true;
		}

		return false;
	}

	@Override
	public void replace(Collection<String> stages) {
		if (!(getPlayer() instanceof ServerPlayer) || !player.getTags().equals(stages)) {
			player.getTags().clear();
			player.getTags().addAll(stages);
			sync();
		}
	}
}
