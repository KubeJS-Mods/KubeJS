package dev.latvian.mods.kubejs.level.world;

import dev.latvian.mods.kubejs.player.ClientPlayerDataJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class ClientLevelJS extends LevelJS {
	private static ClientLevelJS instance;

	private final Minecraft minecraft;
	public final ClientPlayerDataJS clientPlayerData;

	public ClientLevelJS(Minecraft mc, LocalPlayer e) {
		super(mc.level);
		minecraft = mc;
		clientPlayerData = new ClientPlayerDataJS(this, e, true);
	}

	public Minecraft getMinecraft() {
		return minecraft;
	}

	@Override
	public ScriptType getSide() {
		return ScriptType.CLIENT;
	}

	@Override
	public ClientPlayerDataJS getPlayerData(Player player) {
		if (player == minecraft.player || player.getUUID().equals(clientPlayerData.getId())) {
			return clientPlayerData;
		} else {
			return new ClientPlayerDataJS(this, player, false);
		}
	}

	@Override
	public String toString() {
		return "ClientWorld:" + getDimension();
	}

	@Override
	public EntityArrayList getEntities() {
		return new EntityArrayList(this, ((ClientLevel) minecraftLevel).entitiesForRendering());
	}

	public LocalPlayer getMinecraftPlayer() {
		return minecraft.player;
	}

	public static ClientLevelJS getInstance() {
		var level = Minecraft.getInstance().level;
		if (level == null) {
			return instance = null;
		}
		if (instance != null && instance.minecraftLevel == level) {
			return instance;
		}

		return instance = new ClientLevelJS(Minecraft.getInstance(), Minecraft.getInstance().player);
	}

	public static void setInstance(ClientLevelJS instance) {
		ClientLevelJS.instance = instance;
	}
}