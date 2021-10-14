package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.architectury.annotations.ForgeEvent;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.registry.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
@ForgeEvent
public class RegisterRecipeHandlersEvent {
	/**
	 * @deprecated Use {@link dev.latvian.kubejs.KubeJSPlugin#addRecipes(RegisterRecipeHandlersEvent)} instead
	 */
	@Deprecated
	public static final Event<Consumer<RegisterRecipeHandlersEvent>> EVENT = EventFactory.createConsumerLoop(RegisterRecipeHandlersEvent.class);
	private final Map<ResourceLocation, RecipeTypeJS> map;

	public RegisterRecipeHandlersEvent(Map<ResourceLocation, RecipeTypeJS> m) {
		map = m;
	}

	public void register(RecipeTypeJS type) {
		map.put(Registries.getId(type.serializer, Registry.RECIPE_SERIALIZER_REGISTRY), type);
		KubeJS.LOGGER.info("Registered custom recipe handler for type " + type);
	}

	public void register(ResourceLocation id, Supplier<RecipeJS> f) {
		try {
			register(new RecipeTypeJS(Objects.requireNonNull(KubeJSRegistries.recipeSerializers().get(id)), f));
		} catch (NullPointerException e) {
			if (ServerSettings.instance.logErroringRecipes) {
				ConsoleJS.SERVER.warn("Failed to register handler for recipe type " + id + " as it doesn't exist!");
			}
		}
	}

	public void register(String id, Supplier<RecipeJS> f) {
		register(new ResourceLocation(id), f);
	}

	public void ignore(ResourceLocation id) {
		try {
			register(new IgnoredRecipeTypeJS(Objects.requireNonNull(KubeJSRegistries.recipeSerializers().get(id))));
		} catch (NullPointerException e) {
			if (ServerSettings.instance.logErroringRecipes) {
				ConsoleJS.SERVER.warn("Failed to ignore recipe type " + id + " as it doesn't exist!");
			}
		}
	}

	public void ignore(String id) {
		ignore(new ResourceLocation(id));
	}

	public void registerShaped(ResourceLocation id) {
		register(id, ShapedRecipeJS::new);
	}

	public void registerShapeless(ResourceLocation id) {
		register(id, ShapelessRecipeJS::new);
	}

	private void handleMissingSerializer(ResourceLocation id) {
		if (ServerSettings.instance.logInvalidRecipeHandlers) {
			throw new NullPointerException("Cannot find recipe serializer: " + id);
		} else if (ServerSettings.instance.logErroringRecipes) {
			KubeJS.LOGGER.warn("Skipping recipe handler for serializer " + id + " as it does not exist!");
		}
	}
}