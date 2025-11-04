package dev.latvian.mods.kubejs.recipe;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.script.data.VirtualDataMapFile;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;

import java.util.stream.Stream;

public record CompostableRecipesKubeEvent(VirtualDataMapFile<Item, Compostable> compostables) implements KubeEvent {
	public void add(ItemPredicate match, float f) {
		add(match, f, false);
	}

	public void add(ItemPredicate match, float f, boolean villager) {
		var data = new Compostable(f, villager);

		dissolve(match)
			.ifLeft(tag -> compostables.addTag(tag, data))
			.ifRight(items -> items.forEach(item -> compostables.add(item, data)));
	}

	public void addReplace(ItemPredicate match, float f) {
		addReplace(match, f, false);
	}

	public void addReplace(ItemPredicate match, float f, boolean villager) {
		var data = new Compostable(f, villager);

		dissolve(match)
			.ifLeft(tag -> compostables.addTag(tag, data, true))
			.ifRight(items -> items.forEach(item -> compostables.add(item, data, true)));
	}

	public void replaceAll() {
		compostables.replaceAll();
	}

	public void remove(ItemPredicate match) {
		dissolve(match)
			.ifLeft(compostables::removeTag)
			.ifRight(items -> items.forEach(compostables::remove));
	}

	public void removeAll() {
		compostables.clear();
		replaceAll();
	}

	private static Either<TagKey<Item>, Stream<Item>> dissolve(ItemPredicate filter) {
		var tag = filter instanceof Ingredient ingredient ? ingredient.kjs$getTagKey() : null;

		if (tag != null) {
			return Either.left(tag);
		} else {
			return Either.right(filter.kjs$getItemStream());
		}
	}
}