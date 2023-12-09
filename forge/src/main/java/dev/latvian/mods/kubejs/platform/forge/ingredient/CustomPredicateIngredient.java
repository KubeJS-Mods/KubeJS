package dev.latvian.mods.kubejs.platform.forge.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.platform.forge.IngredientForgeHelper;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CustomPredicateIngredient extends KubeJSIngredient {
	public static final Codec<CustomPredicateIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Ingredient.CODEC.fieldOf("parent").forGetter(i -> i.parent),
		Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("uuid").forGetter(i -> i.uuid)
	).apply(instance, CustomPredicateIngredient::fromCodec));

	private final Ingredient parent;
	@Nullable
	private final UUID uuid;
	private final boolean isServer;

	public CustomPredicateIngredient(Ingredient parent, UUID uuid, boolean isServer) {
		super(IngredientForgeHelper.CUSTOM_PREDICATE);
		this.parent = parent;
		this.uuid = uuid;
		this.isServer = isServer;
	}

	private static CustomPredicateIngredient fromCodec(Ingredient parent, UUID uuid) {
		return new CustomPredicateIngredient(parent, uuid, false);
	}

	@Override
	public ItemStack[] getItems() {
		return parent.getItems();
	}

	@Override
	@NotNull
	public IntList getStackingIds() {
		return parent.getStackingIds();
	}

	@Override
	public boolean test(@Nullable ItemStack target) {
		if (isServer && target != null && parent.test(target) && RecipesEventJS.customIngredientMap != null) {
			var i = RecipesEventJS.customIngredientMap.get(uuid);
			return i != null && i.predicate.test(target);
		}

		return false;
	}
}
