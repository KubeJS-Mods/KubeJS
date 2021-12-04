package dev.latvian.mods.kubejs.recipe;

import dev.architectury.annotations.ForgeEvent;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.registry.registries.Registries;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.recipe.minecraft.ShapedRecipeJS;
import dev.latvian.mods.kubejs.recipe.minecraft.ShapelessRecipeJS;
import dev.latvian.mods.kubejs.server.ServerSettings;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 * @apiNote This class and others like it will be changed significantly in 4.1,
 * including the removal of {@code EVENT} and the {@code @ForgeEvent}
 * annotation, honestly, just use the KubeJS plugin system instead...
 */
@ForgeEvent
public class RegisterRecipeHandlersEvent {
	/**
	 * @deprecated Use {@link KubeJSPlugin#addRecipes(RegisterRecipeHandlersEvent)} instead
	 */
	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "4.1")
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