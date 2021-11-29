package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ModelGenerator {
	public static class Element {
		private AABB box = new AABB(0D, 0D, 0D, 1D, 1D, 1D);
		private final JsonObject faces = new JsonObject();

		public Element box(AABB b) {
			box = b;
			return this;
		}

		public JsonObject toJson() {
			JsonObject json = new JsonObject();
			JsonArray f = new JsonArray();
			f.add(box.minX * 16D);
			f.add(box.minY * 16D);
			f.add(box.minZ * 16D);
			json.add("from", f);

			JsonArray t = new JsonArray();
			t.add(box.maxX * 16D);
			t.add(box.maxY * 16D);
			t.add(box.maxZ * 16D);
			json.add("to", t);

			json.add("faces", faces);
			return json;
		}

		public void face(Direction direction, Consumer<Face> consumer) {
			Face f = new Face();
			f.direction = direction;
			consumer.accept(f);
			faces.add(direction.getSerializedName(), f.toJson());
		}
	}

	public static class Face {
		private Direction direction;
		private String texture = "broken";
		private Direction cullface = null;
		private double[] uv = null;
		private int tintindex = -1;

		public JsonObject toJson() {
			JsonObject json = new JsonObject();
			json.addProperty("texture", texture);

			if (cullface != null) {
				json.addProperty("cullface", cullface.getSerializedName());
			}

			if (uv != null) {
				JsonArray a = new JsonArray();
				a.add(uv[0]);
				a.add(uv[1]);
				a.add(uv[2]);
				a.add(uv[3]);
				json.add("uv", a);
			}

			if (tintindex >= 0) {
				json.addProperty("tintindex", tintindex);
			}

			return json;
		}

		public Face tex(String t) {
			texture = t;
			return this;
		}

		public Face cull(Direction d) {
			cullface = d;
			return this;
		}

		public Face cull() {
			return cull(direction);
		}

		public Face uv(double u0, double v0, double u1, double v1) {
			uv = new double[]{u0, v0, u1, v1};
			return this;
		}

		public Face tintindex(int i) {
			tintindex = i;
			return this;
		}
	}

	private String parent = "minecraft:block/cube";
	private final JsonObject textures = new JsonObject();
	private final List<Element> elements = new ArrayList<>();

	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		if (!parent.isEmpty()) {
			json.addProperty("parent", parent);
		}

		if (textures.size() > 0) {
			json.add("textures", textures);
		}

		if (!elements.isEmpty()) {
			JsonArray a = new JsonArray();

			for (Element e : elements) {
				a.add(e.toJson());
			}

			json.add("elements", a);
		}

		return json;
	}

	public void parent(String s) {
		parent = s;
	}

	public void texture(String name, String texture) {
		textures.addProperty(name, texture);
	}

	public void textures(JsonObject json) {
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
			textures.add(entry.getKey(), entry.getValue());
		}
	}

	public void element(Consumer<Element> consumer) {
		Element e = new Element();
		consumer.accept(e);
		elements.add(e);
	}
}
