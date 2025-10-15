package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.ScriptRuntime;
import dev.latvian.mods.rhino.type.EnumTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.RemappedEnumConstant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

// TODO: add enum component variant with custom serialisation that doesn't need the StringRepresentable bound
public record EnumComponent<T extends Enum<T> & StringRepresentable>(@Nullable RecipeComponentType<?> typeOverride, EnumTypeInfo typeInfo, Codec<T> codec) implements RecipeComponent<T> {
	public static final RecipeComponentType<?> TYPE = RecipeComponentType.dynamic(KubeJS.id("enum"), RecordCodecBuilder.<EnumComponent<?>>mapCodec(instance -> instance.group(
		KubeJSCodecs.ENUM_TYPE_INFO.fieldOf("enum").validate(type -> {
			if (type.asClass().isAssignableFrom(StringRepresentable.class)) {
				return DataResult.success(type);
			} else {
				return DataResult.error(() -> "Enum class " + type + " is not StringRepresentable!");
			}
		}).forGetter(EnumComponent::typeInfo)
	).apply(instance, EnumComponent::new)));

	public static <T extends Enum<T> & StringRepresentable> RecipeComponentType<T> of(ResourceLocation id, Class<T> enumClass, Codec<T> codec) {
		return RecipeComponentType.unit(id, type -> new EnumComponent<>(type, (EnumTypeInfo) TypeInfo.of(enumClass), codec));
	}

	public static <T extends Enum<T> & StringRepresentable> RecipeComponentType<T> of(ResourceLocation id, Class<T> enumClass) {
		return of(id, enumClass, StringRepresentable.fromEnum(enumClass::getEnumConstants));
	}

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

	@Override
	public RecipeComponentType<?> type() {
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
