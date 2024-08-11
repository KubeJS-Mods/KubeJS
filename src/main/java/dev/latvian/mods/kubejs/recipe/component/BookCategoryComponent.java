package dev.latvian.mods.kubejs.recipe.component;

import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;

public class BookCategoryComponent {
	public static final EnumComponent<CraftingBookCategory> CRAFTING_BOOK_CATEGORY = EnumComponent.of("crafting_book_category", CraftingBookCategory.class, CraftingBookCategory.CODEC);
	public static final EnumComponent<CookingBookCategory> COOKING_BOOK_CATEGORY = EnumComponent.of("cooking_book_category", CookingBookCategory.class, CookingBookCategory.CODEC);
}
