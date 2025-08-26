package dev.latvian.mods.kubejs.component;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface DataComponentWrapper {
	DynamicCommandExceptionType ERROR_UNKNOWN_COMPONENT = new DynamicCommandExceptionType((object) -> Component.translatableEscape("arguments.item.component.unknown", object));
	Dynamic2CommandExceptionType ERROR_MALFORMED_COMPONENT = new Dynamic2CommandExceptionType((object, object2) -> Component.translatableEscape("arguments.item.component.malformed", object, object2));
	SimpleCommandExceptionType ERROR_EXPECTED_COMPONENT = new SimpleCommandExceptionType(Component.translatable("arguments.item.component.expected"));
	Lazy<Map<DataComponentType<?>, TypeInfo>> TYPE_INFOS = Lazy.identityMap(map -> {
		try {
			for (var field : DataComponents.class.getDeclaredFields()) {
				if (field.getType() == DataComponentType.class
					&& Modifier.isPublic(field.getModifiers())
					&& Modifier.isStatic(field.getModifiers())
					&& field.getGenericType() instanceof ParameterizedType t
				) {
					var key = (DataComponentType) field.get(null);
					var typeInfo = TypeInfo.of(t.getActualTypeArguments()[0]);
					map.put(key, typeInfo);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		KubeJSPlugins.forEachPlugin(map::put, KubeJSPlugin::registerDataComponentTypeDescriptions);
	});

	Lazy<Set<DataComponentType<?>>> VISUAL_DIFFERENCE = Lazy.of(() -> {
		var set = new HashSet<DataComponentType<?>>();

		set.add(DataComponents.DAMAGE);
		set.add(DataComponents.MAX_DAMAGE);
		set.add(DataComponents.ENCHANTMENTS);
		set.add(DataComponents.STORED_ENCHANTMENTS);
		set.add(DataComponents.CUSTOM_MODEL_DATA);
		set.add(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
		set.add(DataComponents.DYED_COLOR);
		set.add(DataComponents.MAP_COLOR);
		set.add(DataComponents.POTION_CONTENTS);
		set.add(DataComponents.TRIM);
		set.add(DataComponents.ENTITY_DATA);
		set.add(DataComponents.BLOCK_ENTITY_DATA);
		set.add(DataComponents.FIREWORK_EXPLOSION);
		set.add(DataComponents.FIREWORKS);
		set.add(DataComponents.PROFILE);
		set.add(DataComponents.BANNER_PATTERNS);
		set.add(DataComponents.BASE_COLOR);
		set.add(DataComponents.POT_DECORATIONS);
		set.add(DataComponents.BLOCK_STATE);

		return set;
	});

	static TypeInfo getTypeInfo(DataComponentType<?> type) {
		return TYPE_INFOS.get().getOrDefault(type, TypeInfo.NONE);
	}

	static DataComponentType<?> wrapType(Object object) {
		if (object instanceof DataComponentType) {
			return (DataComponentType<?>) object;
		}

		return BuiltInRegistries.DATA_COMPONENT_TYPE.get(ID.mc(object));
	}

	static DataComponentMap readMap(@Nullable DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
		reader.skipWhitespace();

		if (!reader.canRead()) {
			return DataComponentMap.EMPTY;
		}

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
				var dataResult = dataComponentType.codecOrThrow().parse(registryOps == null ? NbtOps.INSTANCE : registryOps, new TagParser(reader).readValue());

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

	static DataComponentPatch readPatch(@Nullable DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
		reader.skipWhitespace();

		if (!reader.canRead()) {
			return DataComponentPatch.EMPTY;
		}

		DataComponentPatch.Builder builder = null;

		if (reader.canRead() && reader.peek() == '[') {
			reader.skip();

			while (reader.canRead() && reader.peek() != ']') {
				reader.skipWhitespace();
				boolean remove = reader.canRead() && reader.peek() == '!';

				if (remove) {
					reader.skipWhitespace();
				}

				var dataComponentType = readComponentType(reader);

				if (remove) {
					reader.skipWhitespace();

					if (reader.canRead() && reader.peek() != ']') {
						reader.expect(',');
						reader.skipWhitespace();
					}

					if (builder == null) {
						builder = DataComponentPatch.builder();
					}

					builder.remove(dataComponentType);
					continue;
				}

				reader.skipWhitespace();
				reader.expect('=');
				reader.skipWhitespace();
				int i = reader.getCursor();
				var dataResult = dataComponentType.codecOrThrow().parse(registryOps == null ? NbtOps.INSTANCE : registryOps, new TagParser(reader).readValue());

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

	static DataComponentPredicate readPredicate(@Nullable DynamicOps<Tag> registryOps, StringReader reader) throws CommandSyntaxException {
		var map = reader.canRead() ? readMap(registryOps, reader) : DataComponentMap.EMPTY;
		return map.isEmpty() ? DataComponentPredicate.EMPTY : DataComponentPredicate.allOf(map);
	}

	static boolean filter(Object from, TypeInfo target) {
		return from == null || from instanceof DataComponentMap || from instanceof DataComponentPatch || from instanceof Map || from instanceof NativeJavaMap || from instanceof String s && (s.isEmpty() || s.charAt(0) == '[');
	}

	static DataComponentMap mapOf(@Nullable DynamicOps<Tag> ops, Object o) {
		try {
			return readMap(ops, new StringReader(o.toString()));
		} catch (CommandSyntaxException ex) {
			throw new RuntimeException("Error parsing DataComponentMap from " + o, ex);
		}
	}

	static DataComponentMap mapOrEmptyOf(@Nullable DynamicOps<Tag> ops, Object o) {
		try {
			return readMap(ops, new StringReader(o.toString()));
		} catch (CommandSyntaxException ex) {
			return DataComponentMap.EMPTY;
		}
	}

	static DataComponentPatch patchOf(@Nullable DynamicOps<Tag> ops, Object o) {
		try {
			return readPatch(ops, new StringReader(o.toString()));
		} catch (CommandSyntaxException ex) {
			throw new RuntimeException("Error parsing DataComponentPatch from " + o, ex);
		}
	}

	static DataComponentPatch patchOrEmptyOf(@Nullable DynamicOps<Tag> ops, Object o) {
		try {
			return readPatch(ops, new StringReader(o.toString()));
		} catch (CommandSyntaxException ex) {
			return DataComponentPatch.EMPTY;
		}
	}

	static StringBuilder mapToString(StringBuilder builder, @Nullable DynamicOps<Tag> ops, DataComponentMap map) {
		builder.append('[');

		boolean first = true;

		for (var comp : map) {
			var id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(comp.type());
			var codec = comp.type().codec();

			if (id == null || codec == null) {
				continue;
			}

			if (first) {
				first = false;
			} else {
				builder.append(',');
			}

			builder.append(ID.reduce(id)).append('=');
			try {
				var value = codec == Codec.BOOL ? comp.value() : codec.encodeStart(ops == null ? NbtOps.INSTANCE : ops, Cast.to(comp.value())).getOrThrow();
				builder.append(value);
			} catch (Throwable ex) {
				builder.append("ERROR[").append(ex.getMessage()).append("]");
			}
		}

		builder.append(']');
		return builder;
	}

	static StringBuilder patchToString(StringBuilder builder, @Nullable DynamicOps<Tag> ops, DataComponentPatch patch) {
		builder.append('[');

		boolean first = true;

		for (var comp : patch.entrySet()) {
			var id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(comp.getKey());
			var codec = comp.getKey().codec();

			if (id == null || codec == null) {
				continue;
			}

			if (first) {
				first = false;
			} else {
				builder.append(',');
			}

			if (comp.getValue().isPresent()) {
				builder.append(ID.reduce(id)).append('=');

				try {
					var value = codec == Codec.BOOL ? comp.getValue().get() : codec.encodeStart(ops == null ? NbtOps.INSTANCE : ops, Cast.to(comp.getValue().get())).getOrThrow();
					builder.append(value);
				} catch (Throwable ex) {
					builder.append("ERROR[").append(ex.getMessage()).append("]");
				}
			} else {
				builder.append('!').append(ID.reduce(id));
			}
		}

		builder.append(']');
		return builder;
	}

	static DataComponentPatch visualPatch(DataComponentPatch patch) {
		if (patch.isEmpty()) {
			return DataComponentPatch.EMPTY;
		}

		var builder = DataComponentPatch.builder();

		for (var entry : patch.entrySet()) {
			if (VISUAL_DIFFERENCE.get().contains(entry.getKey())) {
				if (entry.getValue().isPresent()) {
					builder.set(entry.getKey(), Cast.to(entry.getValue().get()));
				} else {
					builder.remove(entry.getKey());
				}
			}
		}

		return builder.build();
	}
}
