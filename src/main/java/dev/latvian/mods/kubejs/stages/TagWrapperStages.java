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
		return player.entityTags();
	}

	@Override
	public boolean clear() {
		if (!player.entityTags().isEmpty()) {
			player.entityTags().clear();
			sync();
			return true;
		}

		return false;
	}

	@Override
	public void replace(Collection<String> stages) {
		if (!(getPlayer() instanceof ServerPlayer) || !player.entityTags().equals(stages)) {
			player.entityTags().clear();
			player.entityTags().addAll(stages);
			sync();
		}
	}
}
