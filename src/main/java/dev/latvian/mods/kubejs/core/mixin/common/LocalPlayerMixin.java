package dev.latvian.mods.kubejs.core.mixin.common;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayerMixin {
	public LocalPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
		super(level, blockPos, f, gameProfile);
	}

	@Shadow
	@RemapForJS("getStatsCounter")
	public abstract StatsCounter getStats();
}
