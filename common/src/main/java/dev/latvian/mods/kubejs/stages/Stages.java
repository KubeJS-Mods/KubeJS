package dev.latvian.mods.kubejs.stages;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.hooks.level.entity.PlayerHooks;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.PlayerKJS;
import dev.latvian.mods.kubejs.net.AddStageMessage;
import dev.latvian.mods.kubejs.net.RemoveStageMessage;
import dev.latvian.mods.kubejs.net.SyncStagesMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public abstract class Stages {
	// TODO: Sync initial stages

	private static final Event<Consumer<StageCreationEvent>> OVERRIDE_CREATION = EventFactory.createConsumerLoop();
	private static final Event<Consumer<StageChangeEvent>> ADDED = EventFactory.createConsumerLoop();
	private static final Event<Consumer<StageChangeEvent>> REMOVED = EventFactory.createConsumerLoop();

	private static Stages createEntityStages(Player player) {
		if (PlayerHooks.isFake(player) || KubeJS.PROXY.isClientButNotSelf(player)) {
			return NoStages.NULL_INSTANCE;
		}

		var event = new StageCreationEvent(player);
		OVERRIDE_CREATION.invoker().accept(event);

		if (event.getPlayerStages() != null) {
			return event.getPlayerStages();
		}

		return new TagWrapperStages(player);
	}

	public static void overrideCreation(Consumer<StageCreationEvent> event) {
		OVERRIDE_CREATION.register(event);
	}

	public static void added(Consumer<StageChangeEvent> event) {
		ADDED.register(event);
	}

	public static void invokeAdded(Stages stages, String stage) {
		ADDED.invoker().accept(new StageChangeEvent(stages, stage));
	}

	public static void removed(Consumer<StageChangeEvent> event) {
		REMOVED.register(event);
	}

	public static void invokeRemoved(Stages stages, String stage) {
		REMOVED.invoker().accept(new StageChangeEvent(stages, stage));
	}

	public static Stages get(@Nullable Player player) {
		if (player == null) {
			return NoStages.NULL_INSTANCE;
		}

		var playerKJS = ((PlayerKJS) player);
		var stages = playerKJS.kjs$getStagesRaw();

		if (stages == null) {
			stages = createEntityStages(player);
			playerKJS.kjs$setStages(stages);
		}

		return stages;
	}

	// End of static //

	public final Player player;

	public Stages(Player p) {
		player = p;
	}

	public abstract boolean addNoUpdate(String stage);

	public abstract boolean removeNoUpdate(String stage);

	public abstract Collection<String> getAll();

	public boolean has(String stage) {
		return getAll().contains(stage);
	}

	public boolean add(String stage) {
		if (addNoUpdate(stage)) {
			if (player instanceof ServerPlayer serverPlayer) {
				new AddStageMessage(player.getUUID(), stage).sendToAll(serverPlayer.server);
			}

			invokeAdded(this, stage);
			return true;
		}

		return false;
	}

	public boolean remove(String stage) {
		if (removeNoUpdate(stage)) {
			if (player instanceof ServerPlayer serverPlayer) {
				new RemoveStageMessage(player.getUUID(), stage).sendToAll(serverPlayer.server);
			}

			invokeRemoved(this, stage);
			return true;
		}

		return false;
	}

	public final boolean set(String stage, boolean enabled) {
		return enabled ? add(stage) : remove(stage);
	}

	public final boolean toggle(String stage) {
		return set(stage, !has(stage));
	}

	public boolean clear() {
		var all = getAll();

		if (all.isEmpty()) {
			return false;
		}

		for (var s : new ArrayList<>(all)) {
			remove(s);
		}

		return true;
	}

	public void sync() {
		if (player instanceof ServerPlayer serverPlayer) {
			new SyncStagesMessage(player.getUUID(), getAll()).sendToAll(serverPlayer.server);
		}
	}

	public void replace(Collection<String> stages) {
		var all = getAll();

		for (var s : new ArrayList<>(all)) {
			removeNoUpdate(s);
		}

		for (var s : stages) {
			addNoUpdate(s);
		}
	}
}
