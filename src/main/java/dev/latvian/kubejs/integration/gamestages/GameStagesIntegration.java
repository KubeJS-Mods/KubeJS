package dev.latvian.kubejs.integration.gamestages;

import dev.latvian.kubejs.documentation.AttachedDataType;
import dev.latvian.kubejs.documentation.DocumentationEvent;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.player.PlayerDataCreatedEvent;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraft.entity.player.EntityPlayerMP;
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

	@SubscribeEvent
	public static void registerDocumentation(DocumentationEvent event)
	{
		event.registerAttachedData(AttachedDataType.PLAYER, "gamestages", GameStagesPlayerData.class);

		event.registerDoubleEvent("gamestage.added", "stage", GameStageEventJS.class);
		event.registerDoubleEvent("gamestage.removed", "stage", GameStageEventJS.class);
	}

	@SubscribeEvent
	public static void onPlayerDataCreated(PlayerDataCreatedEvent event)
	{
		event.setData("gamestages", new GameStagesPlayerData(event.getPlayerData()));
	}

	@SubscribeEvent
	public static void onGameStageAdded(GameStageEvent.Added e)
	{
		if (e.getEntityPlayer() instanceof EntityPlayerMP)
		{
			EventsJS.postDouble("gamestage.added", e.getStageName(), new GameStageEventJS(e.getEntity(), e.getStageName()));
		}
	}

	@SubscribeEvent
	public static void onGameStageRemoved(GameStageEvent.Removed e)
	{
		if (e.getEntityPlayer() instanceof EntityPlayerMP)
		{
			EventsJS.postDouble("gamestage.removed", e.getStageName(), new GameStageEventJS(e.getEntity(), e.getStageName()));
		}
	}
}