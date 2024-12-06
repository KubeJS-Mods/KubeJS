package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.RegistryObjectKJS;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.UnaryOperator;

public interface ID {
	ResourceLocation UNKNOWN = ResourceLocation.fromNamespaceAndPath("unknown", "unknown");
	ResourceLocation AIR = ResourceLocation.withDefaultNamespace("air");
	UnaryOperator<String> BLOCKSTATE = s -> "blockstates/" + s;
	UnaryOperator<String> BLOCK = s -> "block/" + s;
	UnaryOperator<String> ITEM = s -> "item/" + s;
	UnaryOperator<String> MODEL = s -> "models/" + s;
	UnaryOperator<String> BLOCK_MODEL = s -> "models/block/" + s;
	UnaryOperator<String> ITEM_MODEL = s -> "models/item/" + s;
	UnaryOperator<String> BLOCK_LOOT_TABLE = s -> "loot_table/blocks/" + s;
	UnaryOperator<String> PNG_TEXTURE = s -> "textures/" + s + ".png";
	UnaryOperator<String> PNG_TEXTURE_MCMETA = s -> "textures/" + s + ".png.mcmeta";
	UnaryOperator<String> PARTICLE = s -> "particles/" + s;

	static String string(@Nullable String id) {
		if (id == null || id.isEmpty()) {
			return "";
		}

		if (id.indexOf(':') == -1) {
			return "minecraft:" + id;
		}

		return id;
	}

	static String kjsString(String id) {
		if (id == null || id.isEmpty()) {
			return "";
		}

		if (id.indexOf(':') == -1) {
			return KubeJS.MOD_ID + ":" + id;
		}

		return id;
	}

	static String namespace(@Nullable String s) {
		if (s == null || s.isEmpty()) {
			return "minecraft";
		}

		var i = s.indexOf(':');
		return i == -1 ? "minecraft" : s.substring(0, i);
	}

	static String path(@Nullable String s) {
		if (s == null || s.isEmpty()) {
			return "air";
		}

		var i = s.indexOf(':');
		return i == -1 ? s : s.substring(i + 1);
	}

	static ResourceLocation of(@Nullable Object o, boolean preferKJS) {
		if (o == null) {
			return null;
		} else if (o instanceof ResourceLocation id) {
			return id;
		} else if (o instanceof ResourceKey<?> key) {
			return key.location();
		} else if (o instanceof Holder<?> holder) {
			return holder.getKey().location();
		} else if (o instanceof RegistryObjectKJS<?> key) {
			return key.kjs$getIdLocation();
		}

		var s = o instanceof JsonPrimitive p ? p.getAsString() : o.toString();

		if (s.indexOf(':') == -1 && preferKJS) {
			s = "kubejs:" + s;
		}

		try {
			return ResourceLocation.parse(s);
		} catch (ResourceLocationException ex) {
			throw new IllegalArgumentException("Could not create ID from '%s'!".formatted(s));
		}
	}

	static ResourceLocation mc(@Nullable Object o) {
		return of(o, false);
	}

	static ResourceLocation kjs(@Nullable Object o) {
		return of(o, true);
	}

	static boolean isKey(Object from) {
		return from instanceof CharSequence || from instanceof ResourceLocation || from instanceof ResourceKey<?>;
	}

	static String url(ResourceLocation id) {
		return URLEncoder.encode(id.getNamespace(), StandardCharsets.UTF_8) + "/" + URLEncoder.encode(id.getPath(), StandardCharsets.UTF_8);
	}

	static String reduce(ResourceLocation id) {
		return id.getNamespace().equals("minecraft") ? id.getPath() : id.toString();
	}
}
