package dev.latvian.mods.kubejs.player;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.kubejs.util.WithAttachedData;
import dev.latvian.mods.kubejs.world.WorldJS;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public abstract class PlayerDataJS<E extends Player, P extends PlayerJS<E>> implements WithAttachedData {
	private AttachedData data;

	public abstract UUID getId();

	public abstract String getName();

	public GameProfile getProfile() {
		return new GameProfile(getId(), getName());
	}

	@Override
	public AttachedData getData() {
		if (data == null) {
			data = new AttachedData(this);
		}

		return data;
	}

	public boolean hasClientMod() {
		return true;
	}

	public abstract WorldJS getOverworld();

	@Nullable
	public abstract E getMinecraftPlayer();

	public abstract P getPlayer();
}