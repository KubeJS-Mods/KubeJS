package dev.latvian.kubejs.integration.gamestages;

import dev.latvian.kubejs.documentation.DocumentationEvent;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.script.DataType;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
public class GameStagesIntegration
{
	public static void preInit()
	{
		MinecraftForge.EVENT_BUS.register(GameStagesIntegration.class);
	}

	public static boolean hasStage(EntityPlayer player, String stage)
	{
		return GameStageHelper.hasStage(player, stage);
	}

	public static void addStage(EntityPlayer player, String stage)
	{
		GameStageHelper.addStage(player, stage);
		GameStageHelper.syncPlayer(player);
	}

	public static void removeStage(EntityPlayer player, String stage)
	{
		GameStageHelper.removeStage(player, stage);
		GameStageHelper.syncPlayer(player);
	}

	@SubscribeEvent
	public static void registerDocumentation(DocumentationEvent event)
	{
		event.registerAttachedData(DataType.PLAYER, "gamestages", GameStagesPlayerData.class);

		event.registerEvent("gamestage.added", GameStageEventJS.class).doubleParam("stage");
		event.registerEvent("gamestage.removed", GameStageEventJS.class).doubleParam("stage");
	}

	@SubscribeEvent
	public static void attachPlayerData(AttachPlayerDataEvent event)
	{
		event.add("gamestages", new GameStagesPlayerData(event.getParent()));
	}

	@SubscribeEvent
	public static void onGameStageAdded(GameStageEvent.Added e)
	{
		EventsJS.postDouble("gamestage.added", e.getStageName(), new GameStageEventJS(e.getEntityPlayer(), e.getStageName()));
	}

	@SubscribeEvent
	public static void onGameStageRemoved(GameStageEvent.Removed e)
	{
		EventsJS.postDouble("gamestage.removed", e.getStageName(), new GameStageEventJS(e.getEntityPlayer(), e.getStageName()));
	}
}