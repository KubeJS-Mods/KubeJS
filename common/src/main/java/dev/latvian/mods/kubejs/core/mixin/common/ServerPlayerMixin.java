package dev.latvian.mods.kubejs.core.mixin.common;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.kubejs.core.ServerPlayerKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ServerPlayer.class)
@RemapPrefixForJS("kjs$")
public abstract class ServerPlayerMixin extends Player implements ServerPlayerKJS {
	public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
		super(level, blockPos, f, gameProfile);
	}
}
