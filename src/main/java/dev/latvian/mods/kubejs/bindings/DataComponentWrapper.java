package dev.latvian.mods.kubejs.bindings;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;

public interface DataComponentWrapper {
	DynamicCommandExceptionType ERROR_UNKNOWN_COMPONENT = new DynamicCommandExceptionType((object) -> Component.translatableEscape("arguments.item.component.unknown", object));
	Dynamic2CommandExceptionType ERROR_MALFORMED_COMPONENT = new Dynamic2CommandExceptionType((object, object2) -> Component.translatableEscape("arguments.item.component.malformed", object, object2));
	SimpleCommandExceptionType ERROR_EXPECTED_COMPONENT = new SimpleCommandExceptionType(Component.translatable("arguments.item.component.expected"));

	static DataComponentMap readMap(RegistryOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
		reader.skipWhitespace();
		DataComponentMap.Builder builder = null;

		if (reader.canRead() && reader.peek() == '[') {
			reader.skip();

			while (reader.canRead() && reader.peek() != ']') {
				reader.skipWhitespace();
				var dataComponentType = readComponentType(reader);

				reader.skipWhitespace();
				reader.expect('=');
				reader.skipWhitespace();
				int i = reader.getCursor();
				var dataResult = dataComponentType.codecOrThrow().parse(registryOps, new TagParser(reader).readValue());

				if (builder == null) {
					builder = DataComponentMap.builder();
				}

				builder.set(dataComponentType, Cast.to(dataResult.getOrThrow((string) -> {
					reader.setCursor(i);
					return ERROR_MALFORMED_COMPONENT.createWithContext(reader, dataComponentType.toString(), string);
				})));

				reader.skipWhitespace();
				if (!reader.canRead() || reader.peek() != ',') {
					break;
				}

				reader.skip();
				reader.skipWhitespace();
				if (!reader.canRead()) {
					throw ERROR_EXPECTED_COMPONENT.createWithContext(reader);
				}
			}

			reader.expect(']');
		}

		if (reader.canRead() && reader.peek() == '{') {
			var tag = new TagParser(reader).readStruct();

			if (builder == null) {
				builder = DataComponentMap.builder();
			}

			builder.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		}

		return builder == null ? DataComponentMap.EMPTY : builder.build();
	}

	static DataComponentPatch readPatch(RegistryOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
		reader.skipWhitespace();
		DataComponentPatch.Builder builder = null;

		if (reader.canRead() && reader.peek() == '[') {
			reader.skip();

			while (reader.canRead() && reader.peek() != ']') {
				reader.skipWhitespace();
				var dataComponentType = readComponentType(reader);

				reader.skipWhitespace();
				reader.expect('=');
				reader.skipWhitespace();
				int i = reader.getCursor();
				var dataResult = dataComponentType.codecOrThrow().parse(registryOps, new TagParser(reader).readValue());

				if (builder == null) {
					builder = DataComponentPatch.builder();
				}

				builder.set(dataComponentType, Cast.to(dataResult.getOrThrow((string) -> {
					reader.setCursor(i);
					return ERROR_MALFORMED_COMPONENT.createWithContext(reader, dataComponentType.toString(), string);
				})));

				reader.skipWhitespace();
				if (!reader.canRead() || reader.peek() != ',') {
					break;
				}

				reader.skip();
				reader.skipWhitespace();
				if (!reader.canRead()) {
					throw ERROR_EXPECTED_COMPONENT.createWithContext(reader);
				}
			}

			reader.expect(']');
		}

		if (reader.canRead() && reader.peek() == '{') {
			var tag = new TagParser(reader).readStruct();

			if (builder == null) {
				builder = DataComponentPatch.builder();
			}

			builder.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		}

		return builder == null ? DataComponentPatch.EMPTY : builder.build();
	}

	static DataComponentType<?> readComponentType(StringReader stringReader) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw ERROR_EXPECTED_COMPONENT.createWithContext(stringReader);
		}

		int i = stringReader.getCursor();
		ResourceLocation resourceLocation = ResourceLocation.read(stringReader);
		DataComponentType<?> dataComponentType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(resourceLocation);
		if (dataComponentType != null && !dataComponentType.isTransient()) {
			return dataComponentType;
		} else {
			stringReader.setCursor(i);
			throw ERROR_UNKNOWN_COMPONENT.createWithContext(stringReader, resourceLocation);
		}
	}

	static DataComponentMap mapOf(Context cx, Object o) {
		try {
			return readMap(((KubeJSContext) cx).getRegistries().createSerializationContext(NbtOps.INSTANCE), new StringReader(o.toString()));
		} catch (CommandSyntaxException ex) {
			((KubeJSContext) cx).getConsole().error("Error parsing DataComponentMap", ex);
			return DataComponentMap.EMPTY;
		}
	}

	static DataComponentPatch patchOf(Context cx, Object o) {
		try {
			return readPatch(((KubeJSContext) cx).getRegistries().createSerializationContext(NbtOps.INSTANCE), new StringReader(o.toString()));
		} catch (CommandSyntaxException ex) {
			((KubeJSContext) cx).getConsole().error("Error parsing DataComponentPatch", ex);
			return DataComponentPatch.EMPTY;
		}
	}

	static StringBuilder mapToString(StringBuilder builder, HolderLookup.Provider registries, DataComponentMap map) {
		var dynamicOps = registries.createSerializationContext(NbtOps.INSTANCE);
		builder.append('[');

		boolean first = true;

		for (var comp : map) {
			if (first) {
				first = false;
			} else {
				builder.append(',');
			}

			var id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(comp.type());
			var optional = comp.encodeValue(dynamicOps).result();

			if (id != null && !optional.isEmpty()) {
				builder.append(id).append('=').append(optional.get());
			}
		}

		builder.append(']');
		return builder;
	}

	static StringBuilder patchToString(StringBuilder builder, HolderLookup.Provider registries, DataComponentPatch patch) {
		var dynamicOps = registries.createSerializationContext(NbtOps.INSTANCE);
		builder.append('[');

		boolean first = true;

		for (var comp : patch.entrySet()) {
			if (first) {
				first = false;
			} else {
				builder.append(',');
			}

			var id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(comp.getKey());

			if (id != null) {
				if (comp.getValue().isPresent()) {
					var value = comp.getKey().codecOrThrow().encodeStart(dynamicOps, Cast.to(comp.getValue().get())).result().get();
					builder.append(id).append('=').append(value);
				} else {
					builder.append('!').append(id).append("={}");
				}
			}
		}

		builder.append(']');
		return builder;
	}
}
