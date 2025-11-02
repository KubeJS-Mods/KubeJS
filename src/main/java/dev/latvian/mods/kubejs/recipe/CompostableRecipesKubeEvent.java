package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompostableRecipesKubeEvent implements KubeEvent {
	@HideFromJS
	public static final Map<ItemPredicate, Compostable> newEntries = new LinkedHashMap<>();


	public void add(ItemPredicate match, float chance) {
		add(match, chance, true);
	}

	public void add(ItemPredicate match, float chance, boolean canVillagerCompost) {
		Compostable compostable = new Compostable(chance, canVillagerCompost);
		newEntries.put(match, compostable);
	}

	public void modify(ItemPredicate match, float newChance) {
		Compostable existing = newEntries.getOrDefault(match, new Compostable(0f, true));
		newEntries.put(match, new Compostable(newChance, existing.canVillagerCompost()));
	}

	public void modify(ItemPredicate match, boolean canVillagerCompost) {
		Compostable existing = newEntries.getOrDefault(match, new Compostable(0f, true));
		newEntries.put(match, new Compostable(existing.chance(), canVillagerCompost));
	}
}
