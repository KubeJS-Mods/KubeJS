package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.server.ServerJS;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class FakeServerPlayerDataJS extends ServerPlayerDataJS {
	public ServerPlayer player;

	public FakeServerPlayerDataJS(ServerJS s, ServerPlayer p) {
		super(s, p.getUUID(), p.getGameProfile().getName(), true);
		player = p;
	}

	@Override
	public LevelJS getOverworld() {
		return getServer().getOverworld();
	}

	@Override
	@Nullable
	public ServerPlayer getMinecraftPlayer() {
		return player;
	}
}