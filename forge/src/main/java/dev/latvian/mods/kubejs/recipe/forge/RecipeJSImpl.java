package dev.latvian.mods.kubejs.recipe.forge;

import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.conditions.ConditionContext;

public class RecipeJSImpl {

	public static ConditionContext cachedContext = null;

	public static Recipe<?> fromJson(RecipeJS self, RecipeSerializer<?> serializer, TagManager tagManager) throws Throwable {
		if(cachedContext == null) {
			cachedContext = new ConditionContext(tagManager);
		}
		return self.type.serializer.fromJson(self.getOrCreateId(), self.json, cachedContext);
	}
}
