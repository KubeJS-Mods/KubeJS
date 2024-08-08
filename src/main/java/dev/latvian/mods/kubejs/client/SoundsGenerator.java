package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SoundsGenerator {

	private final Map<String, SoundGen> sounds = new HashMap<>();

	public void addSound(String path, Consumer<SoundGen> consumer, boolean overlayExisting) {
		if (overlayExisting && sounds.containsKey(path)) {
			consumer.accept(sounds.get(path));
		} else {
			sounds.put(path, Util.make(new SoundGen(), consumer));
		}
	}

	public void addSound(String path, Consumer<SoundGen> consumer) {
		addSound(path, consumer, false);
	}

	public JsonObject toJson() {
		var json = new JsonObject();
		sounds.forEach((path, gen) -> json.add(path, gen.toJson()));
		return json;
	}

	public static class SoundGen {

		private boolean replace = false;
		@Nullable
		private String subtitle;
		private final List<SoundInstance> instances = new ArrayList<>();

		public SoundGen replace(boolean b) {
			replace = b;
			return this;
		}

		public SoundGen replace() { return replace(true); }

		public SoundGen subtitle(@Nullable String subtitle) {
			this.subtitle = subtitle;
			return this;
		}

		public SoundGen sound(String file) {
			instances.add(new SoundInstance(file));
			return this;
		}

		public SoundGen sounds(String... sounds) {
			instances.addAll(Stream.of(sounds).map(SoundInstance::new).toList());
			return this;
		}

		public SoundGen sound(String file, Consumer<SoundInstance> consumer) {
			instances.add(Util.make(new SoundInstance(file), consumer));
			return this;
		}

		public JsonObject toJson() {
			var json = new JsonObject();
			if (replace) {
				json.addProperty("replace", true);
			}
			if (subtitle != null) {
				json.addProperty("subtitle", subtitle);
			}
			if (!instances.isEmpty()) {
				var array = new JsonArray(instances.size());
				instances.forEach(inst -> array.add(inst.toJson()));
				json.add("sounds", array);
			}
			return json;
		}

	}

	public static class SoundInstance {

		private final String fileLocation;
		private boolean complex = false;
		private float volume = 1.0F;
		private float pitch = 1.0F;
		private int weight = 1;
		private boolean stream = false;
		private int attenuationDistance = 16;
		private boolean preload = false;
		private boolean isEventReference = false;

		public SoundInstance(String fileLocation) {
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

		public SoundInstance stream() { return stream(true); }

		public SoundInstance attenuationDistance(int i) {
			attenuationDistance = i;
			return complex();
		}

		public SoundInstance preload(boolean b) {
			preload = b;
			return complex();
		}

		public SoundInstance preload() { return preload(true); }

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
