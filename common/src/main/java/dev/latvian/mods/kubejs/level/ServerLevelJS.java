package dev.latvian.mods.kubejs.level;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.hooks.level.entity.PlayerHooks;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.mods.kubejs.player.ServerPlayerDataJS;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerJS;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ServerLevelData;

/**
 * @author LatvianModder
 */
public class ServerLevelJS extends LevelJS {
	private final ServerJS server;
	public final CompoundTag persistentData;

	public ServerLevelJS(ServerJS s, ServerLevel w) {
		super(w);
		server = s;

		var t = w.dimension().location().toString();
		persistentData = s.persistentData.getCompound(t);
		s.persistentData.put(t, persistentData);
	}

	@Override
	public ScriptType getSide() {
		return ScriptType.SERVER;
	}

	@Override
	public ServerJS getServer() {
		return server;
	}

	public long getSeed() {
		return getMinecraftLevel().getSeed();
	}

	public void setTime(long time) {
		((ServerLevelData) minecraftLevel.getLevelData()).setGameTime(time);
	}

	public void setLocalTime(long time) {
		((ServerLevelData) minecraftLevel.getLevelData()).setDayTime(time);
	}

	@Override
	public ServerPlayerDataJS getPlayerData(Player player) {
		var data = server.playerMap.get(player.getUUID());

		if (!PlayerHooks.isFake(player) && data != null) {
			return data;
		}

		var fakeData = server.fakePlayerMap.get(player.getUUID());

		if (fakeData == null) {
			fakeData = new FakeServerPlayerDataJS(server, (ServerPlayer) player);
			AttachDataEvent.forPlayer(fakeData).invoke();
		}

		fakeData.player = (ServerPlayer) player;
		return fakeData;
	}

	@Override
	public String toString() {
		return "ServerWorld:" + getDimension();
	}

	@Override
	public EntityArrayList getEntities() {
		return new EntityArrayList(this, Lists.newArrayList(getMinecraftLevel().getAllEntities()));
	}

	@Override
	public ServerLevel getMinecraftLevel() {
		return (ServerLevel) super.getMinecraftLevel();
	}

	public EntityArrayList getEntities(String filter) {
		if (filter.equals("@e")) {
			return getEntities();
		} else if (filter.equals("@a")) {
			return getPlayers();
		}

		try {
			return createEntityList(new EntitySelectorParser(new StringReader(filter), true).parse().findEntities(new WorldlyCommandSender(this)));
		} catch (CommandSyntaxException e) {
			return new EntityArrayList(this, 0);
		}
	}
}