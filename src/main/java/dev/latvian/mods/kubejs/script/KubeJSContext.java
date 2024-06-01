package dev.latvian.mods.kubejs.script;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.ClassVisibilityContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.damagesource.DamageSources;

import java.util.HashMap;
import java.util.Map;

public class KubeJSContext extends Context {
	public final KubeJSContextFactory kjsFactory;
	public final Scriptable topLevelScope;
	private Map<String, Either<NativeJavaClass, Boolean>> javaClassCache;

	public KubeJSContext(KubeJSContextFactory factory) {
		super(factory);
		this.kjsFactory = factory;
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
		return kjsFactory.manager.getRegistries();
	}

	public DamageSources getDamageSources() {
		return kjsFactory.manager.getDamageSources();
	}

	/*
	static Holder<?> holderOf(Context cx, Object from, TypeInfo target) {
		if (from == null) {
			return null;
		} else if (from instanceof Holder<?> h) {
			return h;
		} else if (from instanceof RegistryObjectKJS<?> w) {
			return w.kjs$asHolder();
		}

		var reg = RegistryInfo.ofClass(target.param(0).asClass());

		if (reg != null) {
			return reg.getHolder(ID.mc(from));
		}

		return new Holder.Direct<>(from);
	}

	static ResourceKey<?> resourceKeyOf(Context cx, Object from, TypeInfo target) {
		if (from == null) {
			return null;
		} else if (from instanceof ResourceKey<?> k) {
			return k;
		} else if (from instanceof RegistryObjectKJS<?> w) {
			return w.kjs$getRegistryKey();
		}

		var cl = target.param(0).asClass();

		if (cl == ResourceKey.class) {
			return ResourceKey.createRegistryKey(ID.mc(from));
		}

		var reg = RegistryInfo.ofClass(cl);

		if (reg != null) {
			return ResourceKey.create(reg.key, ID.mc(from));
		}

		throw new IllegalArgumentException("Can't parse " + from + " as ResourceKey<?>!");
	}
	 */

	@Override
	protected Object internalJsToJavaLast(Object from, TypeInfo target) {
		// handle ResourceKey, Holder, TagKey, registry object

		var reg = RegistryType.allOfClass(target.asClass());

		if (!reg.isEmpty()) {
			throw new RuntimeException("AAAAAAA");

			/*
			var id = ID.mc(o);
			var value = getValue(id);

			if (value == null) {
				var npe = new NullPointerException("No such element with id %s in registry %s!".formatted(id, this));
				ConsoleJS.getCurrent(cx).error("Error while wrapping registry element type!", npe);
				throw npe;
			}
			 */

			/*
			@Override
			@SuppressWarnings({"unchecked", "rawtypes"})
			public T wrap(Context cx, Object from, TypeInfo target) {
				if (from instanceof RegistryObjectKJS k && k.kjs$getKubeRegistry().key == key || baseClass.isInstance(from)) {
					return (T) from;
				}

				if (from instanceof CharSequence || from instanceof ResourceLocation || from instanceof ResourceKey || from instanceof RegistryObjectKJS) {
					return RegistryInfo.of(key).getValue(ID.mc(from));
				}

				return (T) from;
			}
			 */
		}

		return from;
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

	public Class<?> loadJavaClass(Object from) {
		if (from instanceof Class<?> c) {
			return c;
		} else if (from instanceof NativeJavaClass c) {
			return c.getClassObject();
		} else {
			return loadJavaClass(String.valueOf(from), true).getClassObject();
		}
	}
}
