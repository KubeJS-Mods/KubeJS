package dev.latvian.mods.kubejs.integration.forge.gamestages;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.stages.StageCreationEvent;
import dev.latvian.mods.kubejs.stages.Stages;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author LatvianModder
 */
public class GameStagesIntegration extends KubeJSPlugin {

	public static final EventGroup GROUP = EventGroup.of("GameStageEvents");

	public static final EventHandler STAGE_ADDED = GROUP.server("stageAdded", () -> GameStageEventJS.class);
	public static final EventHandler STAGE_REMOVED = GROUP.server("stageRemoved", () -> GameStageEventJS.class);

	@Override
	public void init() {
		Stages.overrideCreation(GameStagesIntegration::override);
		MinecraftForge.EVENT_BUS.addListener(GameStagesIntegration::stageAdded);
		MinecraftForge.EVENT_BUS.addListener(GameStagesIntegration::stageRemoved);
	}

	@Override
	public void registerEvents() {
		GROUP.register();
	}

	private static void override(StageCreationEvent event) {
		event.setPlayerStages(new GameStagesWrapper(event.getPlayer()));
	}

	private static void stageAdded(GameStageEvent.Added event) {
		if (STAGE_ADDED.hasListeners()) {
			STAGE_ADDED.post(ScriptType.of(event.getEntity()), event.getStageName(), new GameStageEventJS(event));
		}

		Stages.invokeAdded(Stages.get(event.getEntity()), event.getStageName());
	}

	private static void stageRemoved(GameStageEvent.Removed event) {
		if (STAGE_REMOVED.hasListeners()) {
			STAGE_REMOVED.post(ScriptType.of(event.getEntity()), event.getStageName(), new GameStageEventJS(event));
		}

		Stages.invokeRemoved(Stages.get(event.getEntity()), event.getStageName());
	}
}