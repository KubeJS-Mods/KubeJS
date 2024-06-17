package dev.latvian.mods.kubejs.script;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.ClassVisibilityContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KubeJSContext extends Context {
	public final KubeJSContextFactory kjsFactory;
	public final Scriptable topLevelScope;
	private Map<String, Either<NativeJavaClass, Boolean>> javaClassCache;

	public KubeJSContext(KubeJSContextFactory factory) {
		super(factory);
		this.kjsFactory = factory;
		setApplicationClassLoader(KubeJS.class.getClassLoader());
		this.topLevelScope = initSafeStandardObjects();

		var bindingsEvent = new BindingRegistry(this, topLevelScope);

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

	public RegistryAccessContainer getRegistries() {
		return kjsFactory.manager.getRegistries();
	}

	public RegistryAccess.Frozen getRegistryAccess() {
		return getRegistries().access();
	}

	public RegistryOps<Tag> getNbtOps() {
		return getRegistries().nbt();
	}

	public RegistryOps<JsonElement> getJsonOps() {
		return getRegistries().json();
	}

	public RegistryOps<Object> getJavaOps() {
		return getRegistries().java();
	}

	@Override
	public int internalConversionWeightLast(Object fromObj, TypeInfo target) {
		var c = target.asClass();

		if (c == Optional.class || c == ResourceKey.class || c == Holder.class || c == HolderSet.class || c == TagKey.class) {
			return CONVERSION_TRIVIAL;
		} else if (c != Object.class) {
			var reg = RegistryType.allOfClass(target.asClass());

			if (!reg.isEmpty()) {
				return CONVERSION_TRIVIAL;
			}
		}

		return super.internalConversionWeightLast(fromObj, target);
	}

	private RegistryType<?> lookupRegistryType(TypeInfo type, Object from) {
		var registryType = RegistryType.lookup(type);

		if (registryType == null) {
			throw reportRuntimeError("Can't interpret '" + from + "': no registries for type '" + type + "' found", this);
		}

		return registryType;
	}

	private Registry<?> lookupRegistry(TypeInfo type, Object from) {
		var registryType = lookupRegistryType(type, from);

		var registry = getRegistries().access().registry(registryType.key()).orElse(null);

		if (registry == null) {
			throw reportRuntimeError("Can't interpret '" + from + "' as '" + registryType.key().location() + "': registry not found", this);
		}

		return registry;
	}

	@Override
	protected Object internalJsToJavaLast(Object from, TypeInfo target) {
		var c = target.asClass();

		if (c == Optional.class) {
			if (from instanceof Optional<?> o) {
				return o;
			}

			return Optional.ofNullable(jsToJava(from, target.param(0)));
		} else if (c == ResourceKey.class) {
			if (from instanceof ResourceKey<?> k) {
				return k;
			}

			var registry = lookupRegistry(target.param(0), from);
			var id = ID.mc(from);

			return ResourceKey.create(registry.key(), id);
		} else if (c == Holder.class) {
			if (from instanceof Holder<?> h) {
				return h;
			}

			var registry = lookupRegistry(target.param(0), from);
			var id = ID.mc(from);

			var holder = registry.getHolder(id);

			if (holder.isEmpty()) {
				throw reportRuntimeError("Can't interpret '" + from + "' as Holder: entry not found", this);
			}

			return holder.get();
		} else if (c == HolderSet.class) {
			if (from instanceof HolderSet<?> h) {
				return h;
			}

			var registry = lookupRegistry(target.param(0), from);

			throw reportRuntimeError("Can't interpret '" + from + "' as HolderSet: not supported yet", this);
		} else if (c == TagKey.class) {
			if (from instanceof TagKey<?> k) {
				return k;
			}

			var registryType = lookupRegistryType(target.param(0), from);
			var id = ID.mc(from);
			return TagKey.create(registryType.key(), id);
		} else if (from instanceof Holder<?> holder && c.isInstance(holder.value())) {
			return holder.value();
		} else {
			var reg = RegistryType.lookup(target);

			if (reg != null) {
				var registry = getRegistries().access().registry(reg.key()).orElse(null);

				if (registry == null) {
					throw reportRuntimeError("Can't interpret '" + from + "' as '" + reg.key().location() + "': registry not found", this);
				}

				var value = registry.get(ID.mc(from));

				if (value != null) {
					return value;
				} else {
					throw reportRuntimeError("Can't interpret '" + from + "' as '" + reg.key().location() + "': entry not found", this);
				}
			}
		}

		return super.internalJsToJavaLast(from, target);
	}

	public NativeJavaClass loadJavaClass(String name, boolean error) {
		if (name == null || name.equals("null") || name.isEmpty()) {
			if (error) {
				throw reportRuntimeError("Class name can't be empty!", this);
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
			throw reportRuntimeError((found ? "Failed to load Java class '%s': Class is not allowed by class filter!" : "Failed to load Java class '%s': Class could not be found!").formatted(name), this);
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
