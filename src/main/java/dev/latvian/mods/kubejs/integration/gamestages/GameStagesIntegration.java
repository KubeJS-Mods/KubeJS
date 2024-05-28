package dev.latvian.mods.kubejs.integration.gamestages;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.stages.StageCreationEvent;
import dev.latvian.mods.kubejs.stages.StageEvents;

public class GameStagesIntegration implements KubeJSPlugin {
	@Override
	public void init() {
		StageEvents.overrideCreation(GameStagesIntegration::override);
		/*NeoForge.EVENT_BUS.addListener(GameStagesIntegration::stageAdded);
		NeoForge.EVENT_BUS.addListener(GameStagesIntegration::stageRemoved);*/
	}

	private static void override(StageCreationEvent event) {
		//event.setPlayerStages(new GameStagesWrapper(event.getPlayer()));
	}

	/*private static void stageAdded(GameStageEvent.Added event) {
		if (STAGE_ADDED.hasListeners()) {
			STAGE_ADDED.post(event.getEntity(), event.getStageName(), new GameStageEventJS(event));
		}

		Stages.invokeAdded(Stages.get(event.getEntity()), event.getStageName());
	}

	private static void stageRemoved(GameStageEvent.Removed event) {
		if (STAGE_REMOVED.hasListeners()) {
			STAGE_REMOVED.post(event.getEntity(), event.getStageName(), new GameStageEventJS(event));
		}

		Stages.invokeRemoved(Stages.get(event.getEntity()), event.getStageName());
	}*/
}