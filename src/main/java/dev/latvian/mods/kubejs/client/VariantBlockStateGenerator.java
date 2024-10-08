package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VariantBlockStateGenerator {
	public static class Variant {
		private String key;
		private final List<Model> models = new ArrayList<>();

		public Model model(ResourceLocation s) {
			var model = new Model();
			model.model(s);
			models.add(model);
			return model;
		}

		public JsonElement toJson() {
			if (models.size() == 1) {
				return models.getFirst().toJson();
			}

			var a = new JsonArray();

			for (var m : models) {
				a.add(m.toJson());
			}

			return a;
		}
	}

	public static class Model {
		private ResourceLocation model = ID.UNKNOWN;
		private int x = 0;
		private int y = 0;
		private boolean uvlock = false;

		public Model model(ResourceLocation s) {
			model = s;
			return this;
		}

		public Model x(int x) {
			this.x = x;
			return this;
		}

		public Model y(int y) {
			this.y = y;
			return this;
		}

		public Model uvlock() {
			uvlock = true;
			return this;
		}

		public JsonObject toJson() {
			var json = new JsonObject();
			json.addProperty("model", model.toString());

			if (x != 0) {
				json.addProperty("x", x);
			}

			if (y != 0) {
				json.addProperty("y", y);
			}

			if (uvlock) {
				json.addProperty("uvlock", true);
			}

			return json;
		}
	}

	private final JsonObject variants = new JsonObject();

	public void variant(String key, Consumer<Variant> consumer) {
		var v = new Variant();
		v.key = key;
		consumer.accept(v);
		variants.add(v.key, v.toJson());
	}

	@HideFromJS
	@Deprecated
	public void variant(String key, ResourceLocation model) {
		simpleVariant(key, model);
	}

	public void simpleVariant(String key, ResourceLocation model) {
		variant(key, v -> v.model(model));
	}

	public JsonObject toJson() {
		var json = new JsonObject();
		json.add("variants", variants);
		return json;
	}
}
