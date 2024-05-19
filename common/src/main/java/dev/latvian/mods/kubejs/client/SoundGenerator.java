package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SoundGenerator {

	private final Map<String, SoundEntry> entries = new HashMap<>();

	public void addSound(String path, Consumer<SoundEntry> consumer) {
		if (entries.containsKey(path)) {
			consumer.accept(entries.get(path));
		} else {
			entries.put(path, Util.make(new SoundEntry(), consumer));
		}
	}

	public JsonObject toJson() {
		final JsonObject json = new JsonObject();
		entries.forEach((path, entry) -> json.add(path, entry.toJson()));
		return json;
	}

	public static class SoundEntry {

		private boolean replace = false;
		@Nullable
		private String subtitle;
		private final List<SoundInstance> sounds = new ArrayList<>();

		public SoundEntry replace(boolean b) {
			replace = b;
			return this;
		}

		public SoundEntry subtitle(String subtitle) {
			this.subtitle = subtitle;
			return this;
		}

		public SoundEntry sounds(ResourceLocation... sounds) {
			this.sounds.addAll(Stream.of(sounds).map(SoundInstance::new).toList());
			return this;
		}

		public SoundEntry sound(ResourceLocation file, Consumer<SoundInstance> consumer) {
			sounds.add(Util.make(new SoundInstance(file), consumer));
			return this;
		}

		public JsonObject toJson() {
			final JsonObject json = new JsonObject();
			if (replace) {
				json.addProperty("replace", true);
			}
			if (subtitle != null) {
				json.addProperty("subtitle", subtitle);
			}
			if (!sounds.isEmpty()) {
				final JsonArray array = new JsonArray(sounds.size());
				sounds.forEach(instance -> array.add(instance.toJson()));
				json.add("sounds" ,array);
			}
			return json;
		}
	}

	public static class SoundInstance {

		private final ResourceLocation fileLocation;
		private boolean complex = false;
		private float volume = 1.0F;
		private float pitch = 1.0F;
		private int weight = 1;
		private boolean stream = false;
		private int attenuationDistance = 16;
		private boolean preload = false;
		private boolean isEventReference = false;


		public SoundInstance(ResourceLocation fileLocation) {
			this.fileLocation = fileLocation;
		}

		private SoundInstance complex() {
			complex = true;
			return this;
		}

		public SoundInstance volume(float f) {
			volume = Mth.clamp(f, 0.0F, 1.0F);
			return complex();
		}

		public SoundInstance pitch(float f) {
			pitch = Mth.clamp(f, 0.0F, 1.0F);
			return complex();
		}

		public SoundInstance weight(int i) {
			weight = i;
			return complex();
		}

		public SoundInstance stream(boolean b) {
			stream = b;
			return complex();
		}

		public SoundInstance attenuationDistance(int i) {
			attenuationDistance = i;
			return complex();
		}

		public SoundInstance preload(boolean b) {
			preload = b;
			return complex();
		}

		public SoundInstance asReferenceToEvent() {
			isEventReference = true;
			return complex();
		}

		public JsonElement toJson() {
			if (!complex) {
				return new JsonPrimitive(fileLocation.toString());
			}

			final JsonObject json = new JsonObject();
			json.addProperty("name", fileLocation.toString());
			json.addProperty("volume", volume);
			json.addProperty("pitch", pitch);
			json.addProperty("weight", weight);
			json.addProperty("stream", stream);
			json.addProperty("attenuation_distance", attenuationDistance);
			json.addProperty("preload", preload);
			if (isEventReference) {
				json.addProperty("type", "event");
			}
			return json;
		}
	}
}
