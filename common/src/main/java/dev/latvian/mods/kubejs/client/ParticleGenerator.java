package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ParticleGenerator {

	public transient List<String> textures = new ArrayList<>();

	public ParticleGenerator texture(ResourceLocation location) {
		textures.add(location.toString());
		return this;
	}

	public ParticleGenerator textures(List<String> textures) {
		this.textures = textures;
		return this;
	}

	public JsonObject toJson() {
		final JsonObject json = new JsonObject();
		final JsonArray textures = new JsonArray(this.textures.size());
		this.textures.forEach(textures::add);
		json.add("textures", textures);
		return json;
	}
}
