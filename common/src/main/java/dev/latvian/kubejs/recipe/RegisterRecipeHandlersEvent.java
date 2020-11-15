package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.util.UtilsJS;
import me.shedaniel.architectury.ForgeEvent;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;
import me.shedaniel.architectury.registry.Registries;
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
public class RegisterRecipeHandlersEvent
{
	public static final Event<Consumer<RegisterRecipeHandlersEvent>> EVENT = EventFactory.createConsumerLoop(RegisterRecipeHandlersEvent.class);
	private final Map<ResourceLocation, RecipeTypeJS> map;

	public RegisterRecipeHandlersEvent(Map<ResourceLocation, RecipeTypeJS> m)
	{
		map = m;
	}

	public void register(RecipeTypeJS type)
	{
		map.put(Registries.getId(type.serializer, Registry.RECIPE_SERIALIZER_REGISTRY), type);
	}

	public void register(@ID String id, Supplier<RecipeJS> f)
	{
		register(new RecipeTypeJS(Objects.requireNonNull(Registries.get(KubeJS.MOD_ID).get(Registry.RECIPE_SERIALIZER_REGISTRY).get(UtilsJS.getMCID(id)), "Cannot found recipe serializer: " + UtilsJS.getMCID(id).toString()), f));
	}
}