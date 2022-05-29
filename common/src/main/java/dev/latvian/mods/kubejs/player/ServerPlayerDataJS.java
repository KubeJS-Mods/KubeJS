package dev.latvian.mods.kubejs.player;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.level.ServerLevelJS;
import dev.latvian.mods.kubejs.server.ServerJS;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ServerPlayerDataJS extends PlayerDataJS<ServerPlayer, ServerPlayerJS> {
	private final ServerJS server;
	private final UUID id;
	private final String name;
	private final GameProfile profile;
	private final boolean hasClientMod;

	public ServerPlayerDataJS(ServerJS s, UUID i, String n, boolean h) {
		server = s;
		id = i;
		name = n;
		profile = new GameProfile(id, name);
		hasClientMod = h;
	}

	public ServerJS getServer() {
		return server;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public GameProfile getProfile() {
		return profile;
	}

	@Override
	public LevelJS getOverworld() {
		return server.getOverworld();
	}

	@Override
	@Nullable
	public ServerPlayer getMinecraftPlayer() {
		return server.getMinecraftServer().getPlayerList().getPlayer(getId());
	}

	@Override
	public ServerPlayerJS getPlayer() {
		var p = getMinecraftPlayer();

		if (p == null) {
			throw new NullPointerException("Player entity for " + getName() + " not found!");
		}

		return new ServerPlayerJS(this, (ServerLevelJS) server.wrapMinecraftLevel(p.level), p);
	}

	@Override
	public boolean hasClientMod() {
		return hasClientMod;
	}
}