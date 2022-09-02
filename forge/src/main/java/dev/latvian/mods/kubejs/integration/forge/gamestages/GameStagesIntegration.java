package dev.latvian.mods.kubejs.integration.forge.gamestages;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.stages.NoStages;
import dev.latvian.mods.kubejs.stages.StageCreationEvent;
import dev.latvian.mods.kubejs.stages.Stages;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author LatvianModder
 */
public class GameStagesIntegration extends KubeJSPlugin {

	EventGroup group = EventGroup.of("GameStageEvents");

	EventHandler STAGE_ADDED = group.server("stageAdded", () -> GameStageEventJS.class);
	EventHandler STAGE_REMOVED = group.server("stageRemoved", () -> GameStageEventJS.class);

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.<GameStageEvent.Added>addListener(e -> {
			STAGE_ADDED.post(e.getStageName(), new GameStageEventJS(e));
			Stages.invokeAdded(Stages.get(e.getEntity()), e.getStageName());
		});
		MinecraftForge.EVENT_BUS.<GameStageEvent.Removed>addListener(e -> {
			STAGE_REMOVED.post(e.getStageName(), new GameStageEventJS(e));
			Stages.invokeRemoved(Stages.get(e.getEntity()), e.getStageName());
		});
		Stages.overrideCreation(event -> event.setPlayerStages(new GameStagesWrapper(event.getPlayer())));
	}

	private void override(StageCreationEvent event) {
		event.setPlayerStages(NoStages.NULL_INSTANCE);
	}
}