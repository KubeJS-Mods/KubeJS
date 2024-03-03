package dev.latvian.mods.kubejs.neoforge;

import com.google.common.base.CaseFormat;
import dev.latvian.mods.kubejs.KubeJS;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.moddiscovery.ModAnnotation;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NeoForgeEventsLookup {

	public static final NeoForgeEventsLookup INSTANCE = new NeoForgeEventsLookup();
	@Nullable
	private Map<String, Class<? extends Event>> eventMap;

	@Nullable
	public Class<? extends Event> get(String name) {
		if (eventMap != null) {
			return eventMap.get(name);
		}

		reloadEventMap();
		return eventMap.get(name);
	}

	public void reloadEventMap() {
		eventMap = new HashMap<>();
		for (var data : ModList.get().getAllScanData()) {
			if (!data.getTargets().containsKey("neoforge")) {
				continue;
			}

			var invalidEventClasses = getInvalidClasses(data.getAnnotations());
			for (var cd : data.getClasses()) {
				String className = cd.clazz().getClassName();

				if (isInvalidClassName(className) || isInvalidClass(invalidEventClasses, className)) {
					continue;
				}

				var eventClass = getClass(className);
				if (eventClass == null) {
					continue;
				}

				var formattedName = formatEventName(className.substring(className.lastIndexOf('.') + 1));
				if (eventMap.containsKey(formattedName)) {
					KubeJS.LOGGER.warn("[NativeEvents] Event class " + className + " for name '" + formattedName + "' is already registered as: " + eventMap.get(formattedName));
					continue;
				}

				eventMap.put(formattedName, eventClass);
			}
		}

		KubeJS.LOGGER.info("[NativeEvents] Loaded " + eventMap.size() + " events");
	}

	/**
	 * Get all invalid classes which should not be loaded.
	 * This includes all classes which are annotated with {@link Mixin} or with {@link OnlyIn}, when they current dist does not match.
	 *
	 * @param annotations all annotations
	 * @return set of invalid classes
	 */
	private Set<String> getInvalidClasses(Set<ModFileScanData.AnnotationData> annotations) {
		Type onlyIn = Type.getType(OnlyIn.class);
		Type mixin = Type.getType(Mixin.class);

		Set<String> invalid = new HashSet<>();
		for (ModFileScanData.AnnotationData annotation : annotations) {
			if (!annotation.targetType().equals(ElementType.TYPE)) {
				continue;
			}

			if (isInvalidDist(annotation, onlyIn) || isMixin(annotation, mixin)) {
				invalid.add(annotation.clazz().getClassName());
			}
		}

		return invalid;
	}

	/**
	 * Checks if given className is part of invalid classes. It also checks if the super class is part of invalid classes.
	 *
	 * @param invalidClasses set of invalid classes
	 * @param className      class name
	 * @return true if invalid
	 */
	private boolean isInvalidClass(Set<String> invalidClasses, String className) {
		if (invalidClasses.contains(className)) {
			return true;
		}

		String[] parts = className.split("\\$", 2);
		return invalidClasses.contains(parts[0]);
	}

	private boolean isMixin(ModFileScanData.AnnotationData annotation, Type mixinType) {
		return annotation.annotationType().equals(mixinType);
	}

	private boolean isInvalidDist(ModFileScanData.AnnotationData annotation, Type distType) {
		Type type = annotation.annotationType();
		if (!type.equals(distType)) {
			return false;
		}

		Object unknownValue = annotation.annotationData().get("value");
		if (unknownValue instanceof ModAnnotation.EnumHolder enumHolder) {
			String invalidValue = FMLEnvironment.dist == Dist.CLIENT ? Dist.DEDICATED_SERVER.name() : Dist.CLIENT.name();
			return enumHolder.getValue().equals(invalidValue);
		}

		return false;
	}

	/**
	 * Checks if given class name is invalid. Only classes which end with "Event" are valid, but also subtypes of it.
	 *
	 * @param fullClassName class name
	 * @return true if class name is invalid
	 */
	private boolean isInvalidClassName(String fullClassName) {
		String[] parts = fullClassName.split("\\$");
		for (String part : parts) {
			if (part.endsWith("Event")) {
				return false;
			}
		}

		return true;
	}

	public String formatEventName(String className) {
		var mainParts = className.split("\\$");
		int index = 0;
		for (int i = mainParts.length - 1; i >= 0; i--) {
			if (mainParts[i].endsWith("Event")) {
				index = i;
				break;
			}
		}

		mainParts[index] = mainParts[index].replace("Event", "");

		StringBuilder name = new StringBuilder();
		String lastPart = "$";
		for (int i = index; i < mainParts.length; i++) {
			String[] parts = StringUtils.splitByCharacterTypeCamelCase(mainParts[i]);
			for (String part : parts) {
				if (part.equals(lastPart)) {
					continue;
				}

				name.append(part);
				lastPart = part;
			}
		}

		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name.toString());
	}

	public Map<String, Class<? extends Event>> getEvents() {
		if (eventMap == null) {
			reloadEventMap();
		}

		return Collections.unmodifiableMap(eventMap);
	}

	@Nullable
	private Class<? extends Event> getClass(String className) {
		try {
			Class<?> aClass = Class.forName(className, false, this.getClass().getClassLoader());
			if (aClass.isInterface() || Modifier.isAbstract(aClass.getModifiers())) {
				return null;
			}

			if (Event.class.isAssignableFrom(aClass)) {
				//noinspection unchecked
				return (Class<? extends Event>) aClass;
			}
		} catch (Throwable ignored) {
			String s = "";
		}

		return null;
	}
}
