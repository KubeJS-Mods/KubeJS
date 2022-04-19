package dev.latvian.mods.kubejs.recipe.fabric;

import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecipeJSImpl {
	public static Recipe<?> fromJson(RecipeJS self, RecipeSerializer<?> serializer, TagManager tagManager) throws Throwable {
		return self.type.serializer.fromJson(self.getOrCreateId(), self.json);
	}
}
