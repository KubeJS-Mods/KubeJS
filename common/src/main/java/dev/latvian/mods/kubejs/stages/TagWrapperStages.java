package dev.latvian.mods.kubejs.stages;

import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class TagWrapperStages extends Stages {
	public TagWrapperStages(Player player) {
		super(player);
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
}
