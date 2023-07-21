package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VariantBlockStateGenerator {
	public static class Variant {
		private String key;
		private final List<Model> models = new ArrayList<>();

		public Model model(String s) {
			var model = new Model();
			model.model(s);
			models.add(model);
			return model;
		}

		public JsonElement toJson() {
			if (models.size() == 1) {
				return models.get(0).toJson();
			}

			var a = new JsonArray();

			for (var m : models) {
				a.add(m.toJson());
			}

			return a;
		}
	}

	public static class Model {
		private String model = "broken";
		private int x = 0;
		private int y = 0;
		private boolean uvlock = false;

		public Model model(String s) {
			model = s;
			return this;
		}

		public Model x(int _x) {
			x = _x;
			return this;
		}

		public Model y(int _y) {
			y = _y;
			return this;
		}

		public Model uvlock() {
			uvlock = true;
			return this;
		}

		public JsonObject toJson() {
			var json = new JsonObject();
			json.addProperty("model", model);

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

	public void simpleVariant(String key, String model) {
		variant(key, v -> v.model(model));
	}

	public JsonObject toJson() {
		var json = new JsonObject();
		json.add("variants", variants);
		return json;
	}
}
