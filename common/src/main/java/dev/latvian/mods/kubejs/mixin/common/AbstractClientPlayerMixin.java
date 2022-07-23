package dev.latvian.mods.kubejs.mixin.common;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.kubejs.core.ClientPlayerKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author LatvianModder
 */
@Mixin(value = AbstractClientPlayer.class)
@RemapPrefixForJS("kjs$")
public abstract class AbstractClientPlayerMixin extends Player implements ClientPlayerKJS {
	public AbstractClientPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
		super(level, blockPos, f, gameProfile, profilePublicKey);
	}
}
