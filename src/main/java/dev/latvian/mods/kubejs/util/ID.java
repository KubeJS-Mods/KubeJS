package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.RegistryObjectKJS;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
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
		return switch (o) {
			case null -> null;
			case ResourceLocation id -> id;
			case ResourceKey<?> key -> key.location();
			case Holder<?> holder -> holder.getKey().location();
			case RegistryObjectKJS<?> key -> key.kjs$getIdLocation();
			default -> {
				var s = o instanceof JsonPrimitive p ? p.getAsString() : o.toString();

				if (s.indexOf(':') == -1 && preferKJS) {
					s = "kubejs:" + s;
				}

				try {
					yield ResourceLocation.parse(s);
				} catch (ResourceLocationException ex) {
					throw new KubeRuntimeException("Could not create ID from '%s'!".formatted(s));
				}
			}
		};
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

	static String reduceKjs(ResourceLocation id) {
		return id.getNamespace().equals(KubeJS.MOD_ID) ? id.getPath() : id.toString();
	}

	static String resourcePath(ResourceLocation id) {
		return id.getNamespace().equals("minecraft") ? id.getPath() : (id.getNamespace() + "/" + id.getPath());
	}

	static DataResult<ResourceLocation> read(StringReader reader) {
		return ResourceLocation.read(readGreedy(reader));
	}

	private static String readGreedy(StringReader reader) {
		int i = reader.getCursor();

		while (reader.canRead() && ResourceLocation.isAllowedInResourceLocation(reader.peek())) {
			reader.skip();
		}

		return reader.getString().substring(i, reader.getCursor());
	}
}
