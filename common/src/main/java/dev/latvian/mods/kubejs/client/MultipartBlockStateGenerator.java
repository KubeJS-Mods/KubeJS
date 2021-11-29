package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MultipartBlockStateGenerator {
	public static class Part {
		private String when;
		private final List<VariantBlockStateGenerator.Model> apply = new ArrayList<>();

		public VariantBlockStateGenerator.Model model(String s) {
			VariantBlockStateGenerator.Model model = new VariantBlockStateGenerator.Model();
			model.model(s);
			apply.add(model);
			return model;
		}

		public JsonObject toJson() {
			JsonObject json = new JsonObject();

			if (!when.isEmpty()) {
				JsonObject whenJson = new JsonObject();

				for (var s : when.split(",")) {
					String[] s1 = s.split("=", 2);

					if (s1.length == 2 && !s1[0].isEmpty() && !s1[1].isEmpty()) {
						whenJson.addProperty(s1[0], s1[1]);
					}
				}

				json.add("when", whenJson);
			}

			if (apply.size() == 1) {
				json.add("apply", apply.get(0).toJson());
			} else {
				JsonArray a = new JsonArray();

				for (VariantBlockStateGenerator.Model m : apply) {
					a.add(m.toJson());
				}

				json.add("apply", a);
			}

			return json;
		}
	}

	private final JsonArray multipart = new JsonArray();

	public void part(String when, Consumer<Part> consumer) {
		Part v = new Part();
		v.when = when;
		consumer.accept(v);
		multipart.add(v.toJson());
	}

	public void part(String when, String model) {
		part(when, v -> v.model(model));
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add("multipart", multipart);
		return json;
	}
}
