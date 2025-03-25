package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;

public class BookCategoryComponent {
	public static final RecipeComponentType<CraftingBookCategory> CRAFTING_BOOK_CATEGORY = EnumComponent.of(KubeJS.id("crafting_book_category"), CraftingBookCategory.class, CraftingBookCategory.CODEC);
	public static final RecipeComponentType<CookingBookCategory> COOKING_BOOK_CATEGORY = EnumComponent.of(KubeJS.id("cooking_book_category"), CookingBookCategory.class, CookingBookCategory.CODEC);
}
