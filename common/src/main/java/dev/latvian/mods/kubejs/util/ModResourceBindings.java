package dev.latvian.mods.kubejs.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.architectury.platform.Mod;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ModResourceBindings {

	private final Multimap<String, BindingProvider> bindings = HashMultimap.create();

	public void addBindings(BindingsEvent event) {
		bindings.asMap().forEach((modName, providers) -> {
			var addedBindings = new ArrayList<>();
			providers.forEach(p -> {
				String name = p.name();
				if (!p.test(event.getType())) {
					return;
				}

				try {
					event.add(name, p.generate());
					addedBindings.add(name);
				} catch (Exception e) {
					KubeJS.LOGGER.error("Error adding binding for script type {} from mod '{}': {}", event.getType(), modName, name, e);
				}
			});
			KubeJS.LOGGER.info("Added bindings for script type {} from mod '{}': {}", event.getType(), modName, addedBindings);
		});
	}

	public void readBindings(Mod mod) throws IOException {
		var resource = mod.findResource("kubejs.bindings.txt");
		if (resource.isEmpty()) {
			return;
		}

		try (var lines = Files.lines(resource.get())) {
			List<BindingProvider> providers = lines
					.map(this::removeComment)
					.map(String::trim)
					.filter(line -> !line.isEmpty())
					.map(line -> createProvider(mod, line))
					.filter(Objects::nonNull)
					.toList();
			bindings.putAll(mod.getModId(), providers);
		}
	}

	private String removeComment(String line) {
		int index = line.indexOf('#');
		return index == -1 ? line : line.substring(0, index);
	}

	@Nullable
	private BindingProvider createProvider(Mod mod, String line) {
		// SERVER name class field/method/<init>?
		String[] split = line.split(" +");
		if (split.length < 3) {
			KubeJS.LOGGER.error("Invalid binding for '{}' in line: {}", mod.getModId(), line);
			return null;
		}

		var scriptTypeFilter = createScriptTypeFilter(split[0]);
		var name = split[1];
		var className = split[2];
		ClassBindingProvider classProvider = new ClassBindingProvider(name, scriptTypeFilter, className);
		if (split.length == 3) {
			return classProvider;
		}

		var methodFieldOrConstructor = split[3];
		if (methodFieldOrConstructor.equals("<init>")) {
			return new InstanceBindingProvider(classProvider);
		}

		return new InvokeBindingProvider(classProvider, methodFieldOrConstructor);
	}

	private Predicate<ScriptType> createScriptTypeFilter(String potentialType) {
		if (potentialType.equals("*")) {
			return type -> true;
		}

		ScriptType type = ScriptType.valueOf(potentialType.toUpperCase());
		return type::equals;
	}

	interface BindingProvider extends Predicate<ScriptType> {

		String name();

		Object generate();
	}

	record ClassBindingProvider(String name, Predicate<ScriptType> filter, String className) implements BindingProvider {

		@Override
		public Object generate() {
			try {
				return this.getClass().getClassLoader().loadClass(className);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean test(ScriptType scriptType) {
			return filter.test(scriptType);
		}
	}

	record InstanceBindingProvider(ClassBindingProvider parent) implements BindingProvider {

		@Override
		public String name() {
			return parent.name();
		}

		@Override
		public Object generate() {
			Class<?> clazz = (Class<?>) parent().generate();
			try {
				Constructor<?> constructor = clazz.getConstructor();
				return constructor.newInstance();
			} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
				throw new IllegalStateException("[Bindings] Failed to find default constructor in class '" + clazz.getName() + "'");
			}
		}

		@Override
		public boolean test(ScriptType scriptType) {
			return parent.test(scriptType);
		}
	}

	record InvokeBindingProvider(ClassBindingProvider parent, String methodOrField) implements BindingProvider {

		@Override
		public String name() {
			return parent().name();
		}

		@Override
		public Object generate() {
			Class<?> clazz = (Class<?>) parent().generate();

			Object f = byField(clazz);
			if (f != null) {
				return f;
			}

			Object m = byMethod(clazz);
			if (m != null) {
				return m;
			}

			throw new IllegalStateException("[Bindings] Failed to find static field or method '" + methodOrField + "' in class '" + clazz.getName() + "'");
		}

		@Override
		public boolean test(ScriptType scriptType) {
			return parent.test(scriptType);
		}

		@Nullable
		private Object byField(Class<?> clazz) {
			try {
				Field field = clazz.getField(methodOrField);
				if (Modifier.isStatic(field.getModifiers())) {
					return field.get(null);
				}
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new IllegalStateException("[Bindings] Failed to get static field '" + methodOrField + "' in class '" + clazz.getName() + "'", e);
			}
			return null;
		}

		@Nullable
		private Object byMethod(Class<?> clazz) {
			try {
				Method method = clazz.getMethod(methodOrField);
				if (Modifier.isStatic(method.getModifiers())) {
					return method.invoke(null);
				}
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				throw new IllegalStateException("[Bindings] Failed to invoke static method '" + methodOrField + "' in class '" + clazz.getName() + "'", e);
			}
			return null;
		}
	}
}
