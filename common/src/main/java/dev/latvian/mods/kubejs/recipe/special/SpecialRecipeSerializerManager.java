package dev.latvian.mods.kubejs.recipe.special;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.HashMap;
import java.util.Map;

public class SpecialRecipeSerializerManager extends EventJS {
	public static final SpecialRecipeSerializerManager INSTANCE = new SpecialRecipeSerializerManager();
	public static final Event<Runnable> EVENT = EventFactory.createLoop();
	private final Map<ResourceLocation, Boolean> data = new HashMap<>();

	public void reset() {
		synchronized (data) {
			data.clear();
		}
	}

	@Override
	protected void afterPosted(EventResult result) {
		EVENT.invoker().run();
	}

	public boolean isSpecial(Recipe<?> recipe) {
		return data.getOrDefault(KubeJSRegistries.recipeSerializers().getId(recipe.getSerializer()), recipe.isSpecial());
	}

	public void ignoreSpecialFlag(ResourceLocation id) {
		synchronized (data) {
			data.put(id, false);
		}
	}

	public void addSpecialFlag(ResourceLocation id) {
		synchronized (data) {
			data.put(id, true);
		}
	}

	public void ignoreSpecialMod(String modid) {
		synchronized (data) {
			KubeJSRegistries.recipeSerializers().getIds().forEach(id -> {
				if (id.getNamespace().equals(modid)) {
					data.put(id, false);
				}
			});
		}
	}

	public void addSpecialMod(String modid) {
		synchronized (data) {
			KubeJSRegistries.recipeSerializers().getIds().forEach(id -> {
				if (id.getNamespace().equals(modid)) {
					data.put(id, true);
				}
			});
		}
	}
}
