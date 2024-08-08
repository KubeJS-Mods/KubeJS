package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ParticleGenerator {

	public transient List<String> textures = new ArrayList<>();

	public ParticleGenerator texture(String texture) {
		textures.add(texture);
		return this;
	}

	public ParticleGenerator textures(List<String> textures) {
		this.textures = textures;
		return this;
	}

	public JsonObject toJson() {
		var array = new JsonArray(textures.size());
		textures.forEach(array::add);
		var json = new JsonObject();
		json.add("textures", array);
		return json;
	}
}
