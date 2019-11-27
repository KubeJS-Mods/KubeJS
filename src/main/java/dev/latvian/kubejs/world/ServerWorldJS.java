package dev.latvian.kubejs.world;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.kubejs.player.ServerPlayerDataJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class ServerWorldJS extends WorldJS
{
	private final ServerJS server;

	public ServerWorldJS(ServerJS s, ServerWorld w)
	{
		super(w);
		server = s;
	}

	@Override
	public ScriptType getSide()
	{
		return ScriptType.SERVER;
	}

	@Override
	public ServerJS getServer()
	{
		return server;
	}

	@Override
	public ServerPlayerDataJS getPlayerData(PlayerEntity player)
	{
		ServerPlayerDataJS data = server.playerMap.get(player.getUniqueID());

		if (data != null)
		{
			return data;
		}

		FakeServerPlayerDataJS fakeData = server.fakePlayerMap.get(player.getUniqueID());

		if (fakeData == null)
		{
			fakeData = new FakeServerPlayerDataJS(server, (ServerPlayerEntity) player);
			MinecraftForge.EVENT_BUS.post(new AttachPlayerDataEvent(fakeData));
		}

		fakeData.player = (ServerPlayerEntity) player;
		return fakeData;
	}

	@Override
	public String toString()
	{
		return "ServerWorld:" + minecraftWorld.getDimension().getType().getRegistryName();
	}

	@Override
	public EntityArrayList getEntities()
	{
		return new EntityArrayList(this, ((ServerWorld) minecraftWorld).getEntities().collect(Collectors.toList()));
	}

	public EntityArrayList getEntities(@P("filter") String filter)
	{
		try
		{
			return createEntityList(new EntitySelectorParser(new StringReader(filter), true).build().select(new WorldCommandSender(this)));
		}
		catch (CommandSyntaxException e)
		{
			return new EntityArrayList(this, 0);
		}
	}
}