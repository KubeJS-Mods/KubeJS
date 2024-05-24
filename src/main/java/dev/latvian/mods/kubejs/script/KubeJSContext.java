package dev.latvian.mods.kubejs.script;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.WithContext;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.util.ClassVisibilityContext;
import net.minecraft.core.HolderLookup;

import java.util.HashMap;
import java.util.Map;

public class KubeJSContext extends Context {
	public final KubeJSContextFactory kjsFactory;
	public final WithContext<?> nullWithContext;
	public final Scriptable topLevelScope;
	private Map<String, Either<NativeJavaClass, Boolean>> javaClassCache;

	public KubeJSContext(KubeJSContextFactory factory) {
		super(factory);
		this.kjsFactory = factory;
		this.nullWithContext = new WithContext<>(this, null);
		setApplicationClassLoader(KubeJS.class.getClassLoader());
		this.topLevelScope = initSafeStandardObjects();

		var bindingsEvent = new BindingsEvent(this, topLevelScope);

		for (var plugin : KubeJSPlugins.getAll()) {
			plugin.registerBindings(bindingsEvent);
		}

		KubeJSPlugins.addSidedBindings(bindingsEvent);
	}

	@Override
	public boolean visibleToScripts(String fullClassName, ClassVisibilityContext type) {
		if (type == ClassVisibilityContext.CLASS_IN_PACKAGE || type == ClassVisibilityContext.ARGUMENT) {
			return kjsFactory.manager.isClassAllowed(fullClassName);
		}

		return true;
	}

	public ScriptType getType() {
		return kjsFactory.manager.scriptType;
	}

	public ConsoleJS getConsole() {
		return kjsFactory.manager.scriptType.console;
	}

	public HolderLookup.Provider getRegistries() {
		return kjsFactory.manager.registries;
	}

	public NativeJavaClass loadJavaClass(String name, boolean error) {
		if (name == null || name.equals("null") || name.isEmpty()) {
			if (error) {
				throw Context.reportRuntimeError("Class name can't be empty!", this);
			} else {
				return null;
			}
		}

		if (javaClassCache == null) {
			javaClassCache = new HashMap<>();
		}

		var either = javaClassCache.get(name);

		if (either == null) {
			either = Either.right(false);

			if (!kjsFactory.manager.isClassAllowed(name)) {
				either = Either.right(true);
			} else {
				try {
					either = Either.left(new NativeJavaClass(this, topLevelScope, Class.forName(name)));
					getConsole().info("Loaded Java class '%s'".formatted(name));
				} catch (Exception ignored) {
				}
			}

			javaClassCache.put(name, either);
		}

		var l = either.left().orElse(null);

		if (l != null) {
			return l;
		} else if (error) {
			var found = either.right().orElse(false);
			throw Context.reportRuntimeError((found ? "Failed to load Java class '%s': Class is not allowed by class filter!" : "Failed to load Java class '%s': Class could not be found!").formatted(name), this);
		} else {
			return null;
		}
	}
}
