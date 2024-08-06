package dev.latvian.mods.kubejs.kgui.action;

import dev.latvian.mods.kubejs.kgui.KGUIType;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record ClientKGUIActions(ClientPacketListener connection, Map<String, Object> guis) implements KGUIActions {
	@Override
	public void show(KGUIType type, String id, @Nullable CompoundTag data) {
		if (id == null || id.isBlank()) {
		}

		/*
		var gui = guis.get(id);

		if (gui == null) {
			gui = new KGUI();
			gui.id = id;
			gui.type = type;
			KGUIEvents.CREATE.post(ScriptType.CLIENT, id, gui);
		}

		gui.update(data == null ? new CompoundTag() : data);
		Minecraft.getInstance().setScreen(new KGUIScreen(gui));
		 */
	}

	@Override
	public void update(String id, @Nullable CompoundTag data) {
	}

	@Override
	public void hide(String id) {
		/*
		if (Minecraft.getInstance().screen instanceof KGUIScreen) {
			Minecraft.getInstance().popGuiLayer();
		}
		 */
	}

	@Override
	public void mouseClicked(String id, int clickType, int button) {
	}
}
