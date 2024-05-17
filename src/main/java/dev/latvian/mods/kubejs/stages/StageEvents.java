package dev.latvian.mods.kubejs.stages;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.hooks.level.entity.PlayerHooks;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface StageEvents {
	Event<Consumer<StageCreationEvent>> OVERRIDE_CREATION = EventFactory.createConsumerLoop();

	static Stages create(Player player) {
		if (PlayerHooks.isFake(player)) {
			return NoStages.NULL_INSTANCE;
		}

		var event = new StageCreationEvent(player);
		OVERRIDE_CREATION.invoker().accept(event);
		return event.getPlayerStages() == null ? new TagWrapperStages(player) : event.getPlayerStages();
	}

	static void overrideCreation(Consumer<StageCreationEvent> event) {
		OVERRIDE_CREATION.register(event);
	}

	static Stages get(@Nullable Player player) {
		return player == null ? NoStages.NULL_INSTANCE : player.kjs$getStages();
	}
}
