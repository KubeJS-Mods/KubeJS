package dev.latvian.kubejs.recipe.mod;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.RecipeJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TechRebornCompat {
	private static final Map<Class, MethodHandle> TR_CONSTRUCTORS = new ConcurrentHashMap<>();
	private static final Map<Class, MethodHandle> TR_DESERIALIZERS = new ConcurrentHashMap<>();

	public static MethodHandle getTRRecipeConstructor(Recipe<?> resultRecipe, RecipeJS recipe) throws NoSuchMethodException, IllegalAccessException {
		Class<? extends Recipe> recipeClass = resultRecipe.getClass();
		MethodHandle handle = TR_CONSTRUCTORS.get(recipeClass);
		if (handle != null) {
			return handle;
		}

		handle = MethodHandles.lookup().findConstructor(recipeClass, MethodType.methodType(void.class, recipe.type.serializer.getClass(), ResourceLocation.class));
		TR_CONSTRUCTORS.put(recipeClass, handle);
		return handle;
	}

	public static MethodHandle getTRRecipeSerializer(Recipe<?> resultRecipe) throws NoSuchMethodException, IllegalAccessException {
		Class<? extends Recipe> recipeClass = resultRecipe.getClass();
		MethodHandle handle = TR_DESERIALIZERS.get(recipeClass);
		if (handle != null) {
			return handle;
		}

		handle = MethodHandles.lookup().findVirtual(resultRecipe.getClass(), "deserialize", MethodType.methodType(void.class, JsonObject.class));
		TR_DESERIALIZERS.put(recipeClass, handle);
		return handle;
	}
}
