package dev.latvian.mods.kubejs.recipe.special;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

import java.util.HashMap;
import java.util.Map;

public class SpecialRecipeSerializerManager implements KubeEvent {
	public static final class AfterPost extends Event {
	}

	public static final SpecialRecipeSerializerManager INSTANCE = new SpecialRecipeSerializerManager();
	private final Map<Identifier, Boolean> data = new HashMap<>();

	public void reset() {
		synchronized (data) {
			data.clear();
		}
	}

	@Override
	public void afterPosted(EventResult result) {
		NeoForge.EVENT_BUS.post(new AfterPost());
	}

	public boolean isSpecial(Recipe<?> recipe) {
		return data.getOrDefault(BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe.getSerializer()), recipe.isSpecial());
	}

	public void ignoreSpecialFlag(Identifier id) {
		synchronized (data) {
			data.put(id, false);
		}
	}

	public void addSpecialFlag(Identifier id) {
		synchronized (data) {
			data.put(id, true);
		}
	}

	public void ignoreSpecialMod(String modid) {
		synchronized (data) {
			for (var entry : BuiltInRegistries.RECIPE_SERIALIZER.entrySet()) {
				if (entry.getKey().identifier().getNamespace().equals(modid)) {
					data.put(entry.getKey().identifier(), false);
				}
			}
		}
	}

	public void addSpecialMod(String modid) {
		synchronized (data) {
			for (var entry : BuiltInRegistries.RECIPE_SERIALIZER.entrySet()) {
				if (entry.getKey().identifier().getNamespace().equals(modid)) {
					data.put(entry.getKey().identifier(), true);
				}
			}
		}
	}
}
