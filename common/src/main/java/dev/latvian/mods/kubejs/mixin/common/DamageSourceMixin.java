package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.EntityKJS;
import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.player.PlayerJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author LatvianModder
 */
@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {
	@Shadow
	@RemapForJS("getType")
	public abstract String getMsgId();

	@Shadow
	@HideFromJS
	public abstract Entity getDirectEntity();

	@Shadow
	@HideFromJS
	public abstract Entity getEntity();

	@Nullable
	@RemapForJS("getImmediate")
	public EntityJS getImmediateKJS() {
		Entity e = getDirectEntity();
		return e == null ? null : e.asKJS();
	}

	@Nullable
	@RemapForJS("getActual")
	public EntityJS getActualKJS() {
		Entity e = getEntity();
		return e == null ? null : e.asKJS();
	}

	@Nullable
	@RemapForJS("getPlayer")
	public PlayerJS<?> getPlayerKJS() {
		Entity e = getEntity();
		return e == null ? null : (PlayerJS<?>) e.asKJS();
	}
}