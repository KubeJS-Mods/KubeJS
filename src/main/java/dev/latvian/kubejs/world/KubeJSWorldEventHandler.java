package dev.latvian.kubejs.world;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.block.MissingMappingEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author LatvianModder
 */
public class KubeJSWorldEventHandler
{
	public void init()
	{
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::worldLoaded);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::worldUnloaded);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::worldTick);
		MinecraftForge.EVENT_BUS.addListener(this::explosionStart);
		MinecraftForge.EVENT_BUS.addListener(this::explosionDetonate);
		MinecraftForge.EVENT_BUS.addGenericListener(Block.class, this::missingBlockMappings);
		MinecraftForge.EVENT_BUS.addGenericListener(Item.class, this::missingItemMappings);
	}

	private void worldLoaded(WorldEvent.Load event)
	{
		if (ServerJS.instance != null && ServerJS.instance.overworld != null && event.getWorld() instanceof ServerWorld && !ServerJS.instance.worldMap.containsKey(event.getWorld().getDimension().getType()))
		{
			ServerWorldJS w = new ServerWorldJS(ServerJS.instance, (ServerWorld) event.getWorld());
			ServerJS.instance.worldMap.put(event.getWorld().getDimension().getType(), w);
			ServerJS.instance.updateWorldList();
			MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(w));
			new SimpleWorldEventJS(w).post(ScriptType.SERVER, KubeJSEvents.WORLD_LOAD);
		}
	}

	private void worldUnloaded(WorldEvent.Unload event)
	{
		if (ServerJS.instance != null && ServerJS.instance.overworld != null && event.getWorld() instanceof ServerWorld && ServerJS.instance.worldMap.containsKey(event.getWorld().getDimension().getType()))
		{
			WorldJS w = ServerJS.instance.getWorld(event.getWorld());
			new SimpleWorldEventJS(w).post(ScriptType.SERVER, KubeJSEvents.WORLD_UNLOAD);
			ServerJS.instance.worldMap.remove(w.getDimension());
			ServerJS.instance.updateWorldList();
		}
	}

	private void worldTick(TickEvent.WorldTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END && !event.world.isRemote)
		{
			WorldJS w = ServerJS.instance.getWorld(event.world);
			new SimpleWorldEventJS(w).post(ScriptType.SERVER, KubeJSEvents.WORLD_TICK);
		}
	}

	private void explosionStart(ExplosionEvent.Start event)
	{
		if (new ExplosionEventJS.Pre(event).post(KubeJSEvents.WORLD_EXPLOSION_PRE))
		{
			event.setCanceled(true);
		}
	}

	private void explosionDetonate(ExplosionEvent.Detonate event)
	{
		new ExplosionEventJS.Post(event).post(KubeJSEvents.WORLD_EXPLOSION_POST);
	}

	private void missingBlockMappings(RegistryEvent.MissingMappings<Block> event)
	{
		new MissingMappingEventJS<>(event, ForgeRegistries.BLOCKS::getValue).post(ScriptType.STARTUP, KubeJSEvents.BLOCK_MISSING_MAPPINGS);
	}

	private void missingItemMappings(RegistryEvent.MissingMappings<Item> event)
	{
		new MissingMappingEventJS<>(event, ForgeRegistries.ITEMS::getValue).post(ScriptType.STARTUP, KubeJSEvents.ITEM_MISSING_MAPPINGS);
	}
}