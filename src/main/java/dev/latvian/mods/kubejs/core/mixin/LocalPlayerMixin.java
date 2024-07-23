package dev.latvian.mods.kubejs.core.mixin;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.kubejs.client.KubeSessionData;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
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

	@Shadow
	@Final
	public ClientPacketListener connection;

	@Shadow
	@Final
	protected Minecraft minecraft;

	@Override
	public void kjs$runCommand(String command) {
		connection.sendCommand(command);
	}

	@Override
	public void kjs$runCommandSilent(String command) {
		connection.sendCommand(command);
	}

	@Override
	public void kjs$setActivePostShader(@Nullable ResourceLocation id) {
		var sessionData = KubeSessionData.of(connection);

		if (sessionData != null) {
			sessionData.activePostShader = id;
			minecraft.gameRenderer.checkEntityPostEffect(minecraft.options.getCameraType().isFirstPerson() ? minecraft.getCameraEntity() : null);
		}
	}
}
