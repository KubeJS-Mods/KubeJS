package dev.latvian.mods.kubejs.core.mixin.common;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.kubejs.core.ServerPlayerKJS;
import dev.latvian.mods.kubejs.player.KubeJSInventoryListener;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author LatvianModder
 */
@Mixin(value = ServerPlayer.class)
@RemapPrefixForJS("kjs$")
public abstract class ServerPlayerMixin extends Player implements ServerPlayerKJS {
	private KubeJSInventoryListener kjs$inventoryChangeListener;

	public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
		super(level, blockPos, f, gameProfile, profilePublicKey);
	}

	@Override
	public KubeJSInventoryListener kjs$getInventoryChangeListener() {
		if (kjs$inventoryChangeListener == null) {
			kjs$inventoryChangeListener = new KubeJSInventoryListener((ServerPlayer) (Object) this);
		}

		return kjs$inventoryChangeListener;
	}
}
