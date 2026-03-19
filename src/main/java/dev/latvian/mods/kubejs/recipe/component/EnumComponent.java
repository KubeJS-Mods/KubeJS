package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.ScriptRuntime;
import dev.latvian.mods.rhino.type.EnumTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.RemappedEnumConstant;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.Nullable;

// TODO: add enum component variant with custom serialisation that doesn't need the StringRepresentable bound
public record EnumComponent<T extends Enum<T> & StringRepresentable>(@Nullable ResourceKey<RecipeComponentType<?>> typeOverride, EnumTypeInfo typeInfo, Codec<T> codec) implements RecipeComponent<T> {
	public static final ResourceKey<RecipeComponentType<?>> TYPE = RecipeComponentType.builtin("enum");

	public static final MapCodec<EnumComponent<?>> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		KubeJSCodecs.ENUM_TYPE_INFO.fieldOf("enum").validate(type -> {
			if (StringRepresentable.class.isAssignableFrom(type.asClass())) {
				return DataResult.success(type);
			} else {
				return DataResult.error(() -> "Enum class " + type + " is not StringRepresentable!");
			}
		}).forGetter(EnumComponent::typeInfo)
	).apply(instance, EnumComponent::new));

	@SuppressWarnings({"unchecked", "rawtypes"})
	private EnumComponent(EnumTypeInfo typeInfo) {
		this(null, typeInfo, (Codec) Codec.STRING.flatXmap(s -> {
			for (var c : typeInfo.enumConstants()) {
				if (c instanceof RemappedEnumConstant r && r.getRemappedEnumConstantName().equalsIgnoreCase(s)) {
					return DataResult.success(c);
				} else if (c instanceof Enum<?> e && e.name().equalsIgnoreCase(s)) {
					return DataResult.success(c);
				}
			}

			return DataResult.error(() -> "Enum value '" + s + "' of " + typeInfo.asClass().getName() + " not found");
		}, o -> DataResult.success(EnumTypeInfo.getName(o))));
	}

	public static <T extends Enum<T> & StringRepresentable> EnumComponent<T> create(ResourceKey<RecipeComponentType<?>> type, Class<T> enumClass, Codec<T> codec) {
		return new EnumComponent<>(type, (EnumTypeInfo) TypeInfo.of(enumClass), codec);
	}

	public static <T extends Enum<T> & StringRepresentable> EnumComponent<T> create(ResourceKey<RecipeComponentType<?>> type, Class<T> enumClass) {
		return create(type, enumClass, StringRepresentable.fromEnum(enumClass::getEnumConstants));
	}

	@Override
	public ResourceKey<RecipeComponentType<?>> type() {
		return typeOverride == null ? TYPE : typeOverride;
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, T value) {
		if (value instanceof RemappedEnumConstant r) {
			builder.append(r.getRemappedEnumConstantName());
		} else if (value instanceof Enum<?> e) {
			builder.append(e.name());
		} else {
			builder.append(value.toString());
		}
	}

	@Override
	public String toString() {
		if (typeOverride != null) {
			return typeOverride.toString();
		} else {
			return "enum<" + typeInfo.asClass().getName() + ">";
		}
	}

	@Override
	public String toString(OpsContainer ops, T value) {
		return ScriptRuntime.escapeAndWrapString(value.getSerializedName());
	}
}
