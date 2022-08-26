package dev.latvian.mods.kubejs.integration.forge.gamestages;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.player.PlayerDataJS;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.stages.Stages;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

/**
 * @author LatvianModder
 */
public class GameStagesIntegration extends KubeJSPlugin {

	private boolean gameStagesLoaded = false;

	public void init() {
		if (ModList.get().isLoaded("gamestages")) {
			gameStagesLoaded = true;
			MinecraftForge.EVENT_BUS.<GameStageEvent.Added>addListener(e -> {
				new GameStageEventJS(e).post("gamestage.added", e.getStageName());
				Stages.invokeAdded(Stages.get(e.getPlayer()), e.getStageName());
			});
			MinecraftForge.EVENT_BUS.<GameStageEvent.Removed>addListener(e -> {
				new GameStageEventJS(e).post("gamestage.removed", e.getStageName());
				Stages.invokeRemoved(Stages.get(e.getPlayer()), e.getStageName());
			});
			Stages.overrideCreation(event -> event.setPlayerStages(new GameStagesWrapper(event.getPlayer())));
		}
	}

	public void attachPlayerData(AttachDataEvent<PlayerDataJS> event) {
		if (gameStagesLoaded) {
			event.add("gamestages", new GameStagesPlayerData(event.parent()));
		}
	}
}