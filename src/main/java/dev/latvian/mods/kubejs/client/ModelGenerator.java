package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.AABBWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.DirectionWrapper;
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
		private AABB size = AABBWrapper.CUBE;
		private final Face[] faces = new Face[6];

		public JsonObject toJson() {
			var json = new JsonObject();
			var f = new JsonArray();
			f.add(size.minX * 16D);
			f.add(size.minY * 16D);
			f.add(size.minZ * 16D);
			json.add("from", f);

			var t = new JsonArray();
			t.add(size.maxX * 16D);
			t.add(size.maxY * 16D);
			t.add(size.maxZ * 16D);
			json.add("to", t);

			var fc = new JsonObject();

			for (var face : faces) {
				if (face != null) {
					fc.add(face.side.getSerializedName(), face.toJson());
				}
			}

			json.add("faces", fc);
			return json;
		}

		public Element size(AABB b) {
			size = b;
			return this;
		}

		public void allFaces(Consumer<Face> face) {
			faces(DirectionWrapper.VALUES, face);
		}

		public void faces(Direction[] sides, Consumer<Face> face) {
			for (var d : sides) {
				var f = faces[d.ordinal()];

				if (f == null) {
					f = new Face(d);
					faces[d.ordinal()] = f;
				}

				face.accept(f);
			}
		}
	}

	public static class Face {
		public final Direction side;
		private String texture = "kubejs:block/unknown";
		private Direction cullface = null;
		private double[] uv = null;
		private int tintindex = -1;

		public Face(Direction side) {
			this.side = side;
		}

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
			return cull(side);
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
	public void texture(String name, String texture) {
		textures.put(name, texture);
	}

	public void texture(String[] name, String texture) {
		for (var n : name) {
			textures.put(n, texture);
		}
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
