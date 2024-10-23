package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactory;
import dev.latvian.mods.rhino.type.EnumTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.RemappedEnumConstant;
import net.minecraft.util.StringRepresentable;

public record EnumComponent<T extends Enum<T> & StringRepresentable>(String customName, EnumTypeInfo enumTypeInfo, Codec<T> codec) implements RecipeComponent<T> {
	public static final RecipeComponentFactory FACTORY = (registries, storage, reader) -> {
		reader.skipWhitespace();
		reader.expect('<');
		reader.skipWhitespace();
		var cname = reader.readStringUntil('>').trim();

		try {
			if (cname == null) {
				throw new NullPointerException();
			}

			var clazz = Class.forName(cname);
			var typeInfo = TypeInfo.of(clazz);

			if (!(typeInfo instanceof EnumTypeInfo enumTypeInfo)) {
				throw new KubeRuntimeException("Class " + clazz.getTypeName() + " is not an enum!");
			}

			return new EnumComponent<>(enumTypeInfo);
		} catch (Exception ex) {
			throw new KubeRuntimeException("Error loading class " + cname + " for EnumComponent", ex);
		}
	};

	public static <T extends Enum<T> & StringRepresentable> EnumComponent<T> of(String customName, Class<T> enumClass, Codec<T> codec) {
		return new EnumComponent<>(customName, (EnumTypeInfo) TypeInfo.of(enumClass), codec);
	}

	public EnumComponent(EnumTypeInfo typeInfo) {
		this("", typeInfo, (Codec) Codec.STRING.flatXmap(s -> {
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
	public Codec<T> codec() {
		return codec;
	}

	@Override
	public TypeInfo typeInfo() {
		return enumTypeInfo;
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
		return customName.isEmpty() ? ("enum<" + enumTypeInfo.asClass().getName() + ">") : customName;
	}
}
