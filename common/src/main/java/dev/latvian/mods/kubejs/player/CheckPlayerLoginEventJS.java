package dev.latvian.mods.kubejs.player;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.net.SocketAddress;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class CheckPlayerLoginEventJS extends ServerEventJS {
	private final SocketAddress address;
	private final GameProfile profile;
	private Component reason;

	public CheckPlayerLoginEventJS(SocketAddress a, GameProfile p) {
		address = a;
		profile = p;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	public String getName() {
		return profile.getName();
	}

	public String getUuid() {
		return UUIDTypeAdapter.fromUUID(profile.getId());
	}

	public UUID getActualUuid() {
		return profile.getId();
	}

	public Component getReason() {
		if (reason == null) {
			reason = new TextComponent("No reason specified");
		}

		return reason;
	}

	public String getIp() {
		var string = address.toString();
		if (string.contains("/")) {
			string = string.substring(string.indexOf(47) + 1);
		}

		if (string.contains(":")) {
			string = string.substring(0, string.indexOf(58));
		}

		return string;
	}

	public void reject(Component r) {
		reason = r;
		cancel();
	}
}