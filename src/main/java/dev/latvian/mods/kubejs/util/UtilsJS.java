package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.block.BlockModificationKubeEvent;
import dev.latvian.mods.kubejs.item.ItemModificationKubeEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.BlockEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.ItemEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.type.TypeUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class UtilsJS {
	public static final RandomSource RANDOM = RandomSource.create();

	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	public static final Predicate<Object> ALWAYS_TRUE = o -> true;

	public static <T> Predicate<T> onMatchDo(Predicate<T> predicate, Consumer<T> onMatch) {
		return t -> {
			var match = predicate.test(t);
			if (match) {
				onMatch.accept(t);
			}
			return match;
		};
	}

	public static void postItemModificationEvents(ModifyDefaultComponentsEvent event) {
		ItemEvents.MODIFICATION.post(ScriptType.STARTUP, new ItemModificationKubeEvent(event));
	}

	public static void postBlockModificationEvents() {
		BlockEvents.MODIFICATION.post(ScriptType.STARTUP, new BlockModificationKubeEvent());
	}

	public static String toMappedTypeString(Type type) {
		if (type instanceof Class<?> clz) {
			var mapped = clz.getName();
			int ld = mapped.lastIndexOf('.');
			return ld == -1 ? mapped : mapped.substring(ld + 1);
		} else if (type instanceof ParameterizedType paramType) {
			var sb = new StringBuilder();

			var owner = paramType.getOwnerType();
			if (owner != null) {
				sb.append(toMappedTypeString(owner));
				sb.append('.');
			}

			sb.append(toMappedTypeString(TypeUtils.getRawType(paramType)));

			var args = paramType.getActualTypeArguments();
			if (args.length > 0) {
				sb.append('<');
				for (var i = 0; i < args.length; i++) {
					if (i > 0) {
						sb.append(", ");
					}
					sb.append(toMappedTypeString(args[i]));
				}
				sb.append('>');
			}

			return sb.toString();
		} else if (type instanceof GenericArrayType arrType) {
			return toMappedTypeString(arrType.getGenericComponentType()) + "[]";
		} else if (type instanceof TypeVariable<?> typeVar) {
			var sb = new StringBuilder(typeVar.getName());
			var bounds = typeVar.getBounds();

			if (bounds.length > 0 && !(bounds.length == 1 && Object.class.equals(bounds[0]))) {
				sb.append(" extends ");
				for (var i = 0; i < bounds.length; i++) {
					if (i > 0) {
						sb.append(" & ");
					}
					sb.append(toMappedTypeString(bounds[i]));
				}
			}

			return sb.toString();
		} else if (type instanceof WildcardType wildcard) {
			var sb = new StringBuilder().append("?");
			var lowerBounds = wildcard.getLowerBounds();
			var upperBounds = wildcard.getUpperBounds();

			if (lowerBounds.length > 1 || lowerBounds.length == 1 && lowerBounds[0] != null) {
				sb.append(" super ");
				for (var i = 0; i < lowerBounds.length; i++) {
					if (i > 0) {
						sb.append(" & ");
					}
					sb.append(toMappedTypeString(lowerBounds[i]));
				}
			} else if (upperBounds.length > 1 || upperBounds.length == 1 && !Object.class.equals(upperBounds[0])) {
				sb.append(" extends ");
				for (var i = 0; i < upperBounds.length; i++) {
					if (i > 0) {
						sb.append(" & ");
					}
					sb.append(toMappedTypeString(upperBounds[i]));
				}
			}

			return sb.toString();
		}

		var className = type == null ? "null" : type.getClass().getName();
		throw new IllegalArgumentException("Expected a Class, ParameterizedType, GenericArrayType, TypeVariable or WildcardType, but <" + type + "> is of type " + className);
	}

	@Nullable
	public static CreativeModeTab findCreativeTab(Identifier id) {
		return BuiltInRegistries.CREATIVE_MODE_TAB.getValue(id);
	}
}