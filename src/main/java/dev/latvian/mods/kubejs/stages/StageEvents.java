package dev.latvian.mods.kubejs.stages;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

public interface StageEvents {
	static Stages create(Player player) {
		if (player instanceof FakePlayer) {
			return NoStages.NULL_INSTANCE;
		}

		var event = new StageCreationEvent(player);
		NeoForge.EVENT_BUS.post(event);
		return event.getPlayerStages() == null ? new TagWrapperStages(player) : event.getPlayerStages();
	}

	static Stages get(@Nullable Player player) {
		return player == null ? NoStages.NULL_INSTANCE : player.kjs$getStages();
	}
}
