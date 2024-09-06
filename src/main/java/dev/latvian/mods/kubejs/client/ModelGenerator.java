package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.bindings.AABBWrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ModelGenerator {
	public static class Element {
		private AABB box = AABBWrapper.CUBE;
		private final JsonObject faces = new JsonObject();

		public JsonObject toJson() {
			var json = new JsonObject();
			var f = new JsonArray();
			f.add(box.minX * 16D);
			f.add(box.minY * 16D);
			f.add(box.minZ * 16D);
			json.add("from", f);

			var t = new JsonArray();
			t.add(box.maxX * 16D);
			t.add(box.maxY * 16D);
			t.add(box.maxZ * 16D);
			json.add("to", t);

			json.add("faces", faces);
			return json;
		}

		public Element box(AABB b) {
			box = b;
			return this;
		}

		public void face(Direction direction, Consumer<Face> consumer) {
			var f = new Face();
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
			var json = new JsonObject();
			json.addProperty("texture", texture);

			if (cullface != null) {
				json.addProperty("cullface", cullface.getSerializedName());
			}

			if (uv != null) {
				var a = new JsonArray();
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

	private static final ResourceLocation CUBE = ResourceLocation.withDefaultNamespace("block/cube");

	private ResourceLocation parent = CUBE;
	private final Map<String, String> textures = new HashMap<>(1);
	private final List<Element> elements = new ArrayList<>();

	public JsonObject toJson() {
		var json = new JsonObject();

		if (parent != null) {
			json.addProperty("parent", parent.toString());
		}

		if (!textures.isEmpty()) {
			var o = new JsonObject();

			for (var entry : textures.entrySet()) {
				o.addProperty(entry.getKey(), entry.getValue());
			}

			json.add("textures", o);
		}

		if (!elements.isEmpty()) {
			var a = new JsonArray();

			for (var e : elements) {
				a.add(e.toJson());
			}

			json.add("elements", a);
		}

		return json;
	}

	public void parent(@Nullable ResourceLocation s) {
		parent = s;
	}

	@HideFromJS
	public void parent(String s) {
		parent = s.isEmpty() ? null : ResourceLocation.parse(s);
	}

	public void texture(String name, String texture) {
		textures.put(name, texture);
	}

	public void textures(Map<String, String> map) {
		textures.putAll(map);
	}

	public void element(Consumer<Element> consumer) {
		var e = new Element();
		consumer.accept(e);
		elements.add(e);
	}
}
