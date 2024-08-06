package dev.latvian.mods.kubejs.kgui.action;

import dev.latvian.mods.kubejs.kgui.KGUIType;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface KGUIActions {
	default void show(KGUIType type, String id) {
		show(type, id, null);
	}

	void show(KGUIType type, String id, @Nullable CompoundTag data);

	void update(String id, @Nullable CompoundTag data);

	void hide(String id);

	void mouseClicked(String id, int clickType, int button);
}
