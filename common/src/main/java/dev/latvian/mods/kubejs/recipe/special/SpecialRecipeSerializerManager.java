package dev.latvian.mods.kubejs.recipe.special;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.registry.registries.Registries;
import net.minecraft.core.Registry;
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
	protected void afterPosted(boolean result) {
		super.afterPosted(result);
		EVENT.invoker().run();
	}

	public boolean isSpecial(Recipe<?> recipe) {
		return data.getOrDefault(Registries.getId(recipe.getSerializer(), Registry.RECIPE_SERIALIZER_REGISTRY), recipe.isSpecial());
	}

	public void ignoreSpecialFlag(String id) {
		synchronized (data) {
			data.put(new ResourceLocation(id), false);
		}
	}

	public void addSpecialFlag(String id) {
		synchronized (data) {
			data.put(new ResourceLocation(id), true);
		}
	}
}
