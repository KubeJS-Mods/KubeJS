package dev.latvian.mods.kubejs.integration.gamestages;

import dev.latvian.mods.kubejs.stages.StageCreationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "gamestages")
public class GameStagesIntegration {
	@SubscribeEvent
	public static void override(StageCreationEvent event) {
		//event.setPlayerStages(new GameStagesWrapper(event.getPlayer()));
	}

	/*
	@SubscribeEvent
	public static void stageAdded(GameStageEvent.Added event) {
		if (STAGE_ADDED.hasListeners()) {
			STAGE_ADDED.post(event.getEntity(), event.getStageName(), new GameStageEventJS(event));
		}

		Stages.invokeAdded(Stages.get(event.getEntity()), event.getStageName());
	}

	@SubscribeEvent
	public static void stageRemoved(GameStageEvent.Removed event) {
		if (STAGE_REMOVED.hasListeners()) {
			STAGE_REMOVED.post(event.getEntity(), event.getStageName(), new GameStageEventJS(event));
		}

		Stages.invokeRemoved(Stages.get(event.getEntity()), event.getStageName());
	}
	*/
}