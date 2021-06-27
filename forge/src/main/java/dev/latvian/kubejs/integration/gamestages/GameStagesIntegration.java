package dev.latvian.kubejs.integration.gamestages;

import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.stages.Stages;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author LatvianModder
 */
public class GameStagesIntegration {
	public static void init() {
		MinecraftForge.EVENT_BUS.register(GameStagesIntegration.class);
		AttachPlayerDataEvent.EVENT.register(GameStagesIntegration::attachPlayerData);
		Stages.overrideCreation(event -> event.setPlayerStages(new GameStagesWrapper(event.getPlayer())));
	}

	public static void attachPlayerData(AttachPlayerDataEvent event) {
		event.add("gamestages", new GameStagesPlayerData(event.getParent()));
	}

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
}