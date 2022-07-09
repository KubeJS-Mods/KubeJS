package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.core.AsKJS;
import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.player.PlayerJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author LatvianModder
 */
@Mixin(DamageSource.class)
@RemapPrefixForJS("kjs$")
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
	public EntityJS kjs$getImmediate() {
		return AsKJS.wrapSafe(getDirectEntity());
	}

	@Nullable
	public EntityJS kjs$getActual() {
		return AsKJS.wrapSafe(getEntity());
	}

	@Nullable
	public PlayerJS<?> kjs$getPlayer() {
		var entity = getEntity();
		if (entity != null) {
			return entity.asKJS().getPlayer();
		}
		return null;
	}
}