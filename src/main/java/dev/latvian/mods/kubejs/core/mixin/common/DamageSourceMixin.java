package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DamageSource.class)
@RemapPrefixForJS("kjs$")
public abstract class DamageSourceMixin {
	@Shadow
	@RemapForJS("getType")
	public abstract String getMsgId();

	@Shadow
	@RemapForJS("getImmediate")
	public abstract Entity getDirectEntity();

	@Shadow
	@RemapForJS("getActual")
	public abstract Entity getEntity();

	@Nullable
	public Player kjs$getPlayer() {
		return getEntity() instanceof Player p ? p : null;
	}
}