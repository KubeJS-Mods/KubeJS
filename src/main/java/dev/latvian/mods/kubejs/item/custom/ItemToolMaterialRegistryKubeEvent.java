package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import dev.latvian.mods.kubejs.item.MutableToolMaterial;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.item.ToolMaterial;

import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

@Info("""
	Invoked when the game is starting up and the item tool materials are being registered.
	""")
public record ItemToolMaterialRegistryKubeEvent(Map<String, ToolMaterial> materials) implements KubeStartupEvent {
	@Info("Adds a new tool material.")
	public void add(String id, Consumer<MutableToolMaterial> material) {
		var t = new MutableToolMaterial(ToolMaterial.IRON);
		material.accept(t);
		materials.put(id.toLowerCase(Locale.ROOT), t.toToolMaterial());
	}

	public void addBasedOnExisting(String id, String existing, Consumer<MutableToolMaterial> material) {
		var base = materials.getOrDefault(existing.toLowerCase(Locale.ROOT), ToolMaterial.IRON);
		var t = new MutableToolMaterial(base);
		material.accept(t);
		materials.put(id.toLowerCase(Locale.ROOT), t.toToolMaterial());
	}

	public void addExisting(String id, ToolMaterial material) {
		materials.put(id.toLowerCase(Locale.ROOT), material);
	}
}
