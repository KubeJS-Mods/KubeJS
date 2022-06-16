package dev.latvian.mods.kubejs.recipe.ingredientaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.level.LevelPlatformHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class IngredientAction extends IngredientActionFilter {
	private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
	public static final Map<String, Function<JsonObject, IngredientAction>> FACTORY_MAP = new HashMap<>();

	static {
		FACTORY_MAP.put("custom", json -> new CustomIngredientAction(json.get("id").getAsString()));
		FACTORY_MAP.put("damage", json -> new DamageAction(json.get("damage").getAsInt()));
		FACTORY_MAP.put("replace", json -> new ReplaceAction(ItemStackJS.of(json.get("item")).getItemStack()));
		FACTORY_MAP.put("keep", json -> new KeepAction());
	}

	public static List<IngredientAction> parseList(JsonElement json) {
		if (json == null || !json.isJsonArray()) {
			return Collections.emptyList();
		}

		List<IngredientAction> list = new ArrayList<>();

		for (var e : json.getAsJsonArray()) {
			var o = e.getAsJsonObject();

			var factory = FACTORY_MAP.get(o.has("type") ? o.get("type").getAsString() : "");
			var action = factory == null ? null : factory.apply(o);

			if (action != null) {
				action.filterIndex = o.has("filter_index") ? o.get("filter_index").getAsInt() : -1;
				action.filterIngredient = o.has("filter_ingredient") ? IngredientJS.of(o.get("filter_ingredient")) : null;
				list.add(action);
			}
		}

		return list.isEmpty() ? Collections.emptyList() : list;
	}

	public static List<IngredientAction> readList(FriendlyByteBuf buf) {
		var s = buf.readVarInt();

		if (s <= 0) {
			return Collections.emptyList();
		}

		List<IngredientAction> list = new ArrayList<>();

		for (var i = 0; i < s; i++) {
			var factory = FACTORY_MAP.get(buf.readUtf());
			var action = factory == null ? null : factory.apply(GSON.fromJson(buf.readUtf(), JsonObject.class));

			if (action != null) {
				action.filterIndex = buf.readVarInt();
				var ij = buf.readUtf();
				action.filterIngredient = ij.isEmpty() ? null : IngredientJS.of(GSON.fromJson(ij, JsonObject.class));
				list.add(action);
			}
		}

		return list.isEmpty() ? Collections.emptyList() : list;
	}

	public static void writeList(FriendlyByteBuf buf, List<IngredientAction> list) {
		if (list == null || list.isEmpty()) {
			buf.writeVarInt(0);
			return;
		}

		buf.writeVarInt(list.size());

		for (var action : list) {
			buf.writeUtf(action.getType());
			var json = new JsonObject();
			action.toJson(json);
			buf.writeUtf(GSON.toJson(json));
			buf.writeVarInt(action.filterIndex);
			buf.writeUtf(action.filterIngredient == null ? "" : GSON.toJson(action.filterIngredient.toJson()));
		}
	}

	public static ItemStack getRemaining(CraftingContainer container, int index, List<IngredientAction> ingredientActions) {
		var stack = container.getItem(index);

		if (stack == null || stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		for (var action : ingredientActions) {
			if (action.checkFilter(index, stack)) {
				return action.transform(stack.copy(), index, container);
			}
		}

		return LevelPlatformHelper.get().getContainerItem(stack);
	}

	public abstract ItemStack transform(ItemStack old, int index, CraftingContainer container);

	public abstract String getType();

	public void toJson(JsonObject json) {
	}

	public final JsonObject toJson() {
		var json = new JsonObject();
		json.addProperty("type", getType());

		if (filterIngredient != null) {
			json.add("filter_ingredient", filterIngredient.toJson());
		}

		if (filterIndex != -1) {
			json.addProperty("filter_index", filterIndex);
		}

		toJson(json);
		return json;
	}
}
