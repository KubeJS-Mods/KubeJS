package dev.latvian.mods.kubejs.core.mixin;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.kubejs.core.ClientPlayerKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractClientPlayer.class)
@RemapPrefixForJS("kjs$")
public abstract class AbstractClientPlayerMixin extends Player implements ClientPlayerKJS {
	public AbstractClientPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
		super(level, blockPos, f, gameProfile);
	}
}
