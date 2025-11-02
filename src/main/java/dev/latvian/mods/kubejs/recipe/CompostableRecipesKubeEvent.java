package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CompostableRecipesKubeEvent implements KubeEvent {
	@HideFromJS
	public static final Map<ItemPredicate, Compostable> newEntries = new LinkedHashMap<>();

	@HideFromJS
	public static final Set<ItemPredicate> toRemove = new HashSet<>();

	@HideFromJS
	public static final Map<ItemPredicate, Compostable> toModify = new LinkedHashMap<>();

	public CompostableRecipesKubeEvent() {
		newEntries.clear();
		toRemove.clear();
		toModify.clear();
	}

	public void add(ItemPredicate match, float chance) {
		add(match, chance, false);
	}

	public void add(ItemPredicate match, float chance, boolean canVillagerCompost) {
		Compostable compostable = new Compostable(chance, canVillagerCompost);
		newEntries.put(match, compostable);
	}

	public void modify(ItemPredicate match, float newChance) {
		Compostable existing = newEntries.getOrDefault(match, new Compostable(0f, false));
		toModify.put(match, new Compostable(newChance, existing.canVillagerCompost()));
	}

	public void modify(ItemPredicate match, boolean canVillagerCompost) {
		Compostable existing = newEntries.getOrDefault(match, new Compostable(0f, false));
		toModify.put(match, new Compostable(existing.chance(), canVillagerCompost));
	}

	public void remove(ItemPredicate match) {
		toRemove.add(match);
	}

	@HideFromJS
	public static void generateData(KubeDataGenerator generator) {
		JsonObject root = new JsonObject();

		if (!toRemove.isEmpty()) {
			JsonArray removeJson = new JsonArray();
			for (ItemPredicate match : toRemove) {
				for (Item item : match.kjs$getItemTypes()) {
					removeJson.add(item.toString());
				}
			}
			root.add("remove", removeJson);
		}

		JsonObject values = new JsonObject();

		if (!newEntries.isEmpty()) {
			for (Map.Entry<ItemPredicate, Compostable> entry : newEntries.entrySet()) {
				JsonObject struct = new JsonObject();

				struct.addProperty("chance", entry.getValue().chance());
				struct.addProperty("can_villager_compost", entry.getValue().canVillagerCompost());

				values.add(entry.getKey().kjs$getFirst().getItem().toString(), struct);
			}
		}

		if (!toModify.isEmpty()) {
			for (Map.Entry<ItemPredicate, Compostable> entry : toModify.entrySet()) {
				JsonObject struct = new JsonObject();
				struct.addProperty("replace", true);

				JsonObject v = new JsonObject();
				v.addProperty("chance", entry.getValue().chance());
				struct.addProperty("can_villager_compost", entry.getValue().canVillagerCompost());

				struct.add("value", v);
				values.add(entry.getKey().kjs$getFirst().getItem().toString(), struct);
			}
		}

		root.add("values", values);

		generator.add(GeneratedData.json(ResourceLocation.parse("neoforge:data_maps/item/compostables.json"), () -> root));


		System.out.println("JSON DATA");
		System.out.println(root);
	}
}
