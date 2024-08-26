package dev.latvian.mods.kubejs.core.mixin;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.kubejs.core.LocalClientPlayerKJS;
import dev.latvian.mods.kubejs.kgui.action.ClientKGUIActions;
import dev.latvian.mods.kubejs.kgui.action.KGUIActions;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayerMixin implements LocalClientPlayerKJS {
	@Unique
	private KGUIActions kjs$kguiActions;

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
	@Accessor("minecraft")
	public abstract Minecraft kjs$getMinecraft();

	@Override
	public KGUIActions kjs$getKgui() {
		if (kjs$kguiActions == null) {
			// kjs$kguiActions = new ClientKGUIActions(connection, Objects.requireNonNull(KubeSessionData.of(connection)).kgui);
			kjs$kguiActions = new ClientKGUIActions(connection, Map.of());
		}

		return kjs$kguiActions;
	}
}
