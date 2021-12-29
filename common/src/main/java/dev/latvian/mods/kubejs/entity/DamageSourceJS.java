package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.player.PlayerJS;
import dev.latvian.mods.kubejs.world.WorldJS;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class DamageSourceJS {
	private final WorldJS level;
	public final DamageSource source;

	public DamageSourceJS(WorldJS l, DamageSource s) {
		level = l;
		source = s;
	}

	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.1")
	public final WorldJS getWorld() {
		return getLevel();
	}

	public WorldJS getLevel() {
		return level;
	}

	public String getType() {
		return source.msgId;
	}

	@Nullable
	public EntityJS getImmediate() {
		return getLevel().getEntity(source.getDirectEntity());
	}

	@Nullable
	public EntityJS getActual() {
		return getLevel().getEntity(source.getEntity());
	}

	@Nullable
	public PlayerJS<?> getPlayer() {
		return getLevel().getPlayer(source.getEntity());
	}
}