package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactory;
import dev.latvian.mods.rhino.type.EnumTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.RemappedEnumConstant;
import net.minecraft.util.StringRepresentable;

public record EnumComponent<T extends Enum<T> & StringRepresentable>(EnumTypeInfo enumTypeInfo, Codec<T> codec) implements RecipeComponent<T> {
	public static final RecipeComponentFactory FACTORY = (storage, reader) -> {
		reader.skipWhitespace();
		reader.expect('<');
		reader.skipWhitespace();
		var cname = reader.readStringUntil('>').trim();
		reader.expect('>');

		try {
			if (cname == null) {
				throw new NullPointerException();
			}

			var clazz = Class.forName(cname);

			var typeInfo = TypeInfo.of(clazz);

			if (!(typeInfo instanceof EnumTypeInfo enumTypeInfo)) {
				throw new KubeRuntimeException("Class " + clazz.getTypeName() + " is not an enum!");
			}

			return new EnumComponent(enumTypeInfo, Codec.STRING.xmap(s -> {
				for (var c : enumTypeInfo.enumConstants()) {
					if (c instanceof RemappedEnumConstant r && r.getRemappedEnumConstantName().equalsIgnoreCase(s)) {
						return c;
					} else if (c instanceof Enum<?> e && e.name().equalsIgnoreCase(s)) {
						return c;
					}
				}

				throw new KubeRuntimeException("Enum value '" + s + "' of " + clazz.getName() + " not found");
			}, EnumTypeInfo::getName));
		} catch (Exception ex) {
			throw new KubeRuntimeException("Error loading class " + cname + " for EnumComponent", ex);
		}
	};

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
		return "enum<" + enumTypeInfo.asClass().getName() + ">";
	}
}
