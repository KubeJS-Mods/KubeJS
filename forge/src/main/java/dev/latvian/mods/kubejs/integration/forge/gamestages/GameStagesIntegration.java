package dev.latvian.mods.kubejs.integration.forge.gamestages;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.stages.StageCreationEvent;
import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author LatvianModder
 */
public class GameStagesIntegration extends KubeJSPlugin {

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(GameStagesIntegration.class);
		// FIXME: Gamestages Stages.overrideCreation(event -> event.setPlayerStages(new GameStagesWrapper(event.getPlayer())));
		Stages.overrideCreation(this::override);
	}

	private void override(StageCreationEvent event) {
		// event.setPlayerStages(NoStages.NULL_INSTANCE);
	}

	/* FIXME: Gamestages
	@SubscribeEvent
	public static void gameStageAdded(GameStageEvent.Added e) {
		new GameStageEventJS(e).post("gamestage.added", e.getStageName());
		Stages.invokeAdded(Stages.get(e.getPlayer()), e.getStageName());
	}

	@SubscribeEvent
	public static void gameStageRemoved(GameStageEvent.Removed e) {
		new GameStageEventJS(e).post("gamestage.removed", e.getStageName());
		Stages.invokeRemoved(Stages.get(e.getPlayer()), e.getStageName());
	}
	 */
}