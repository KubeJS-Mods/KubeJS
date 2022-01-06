package dev.latvian.mods.kubejs.integration.forge.gamestages;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.player.PlayerDataJS;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

/**
 * @author LatvianModder
 */
public class GameStagesIntegration extends KubeJSPlugin {

	private boolean gameStagesLoaded = false;

	public void init() {
		if (ModList.get().isLoaded("gamestages")) {
			gameStagesLoaded = true;
			MinecraftForge.EVENT_BUS.register(GameStagesIntegration.class);
			// FIXME: Gamestages Stages.overrideCreation(event -> event.setPlayerStages(new GameStagesWrapper(event.getPlayer())));
		}
	}

	public void attachPlayerData(AttachDataEvent<PlayerDataJS> event) {
		if (gameStagesLoaded) {
			event.add("gamestages", new GameStagesPlayerData(event.parent()));
		}
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