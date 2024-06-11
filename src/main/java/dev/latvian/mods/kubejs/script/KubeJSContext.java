package dev.latvian.mods.kubejs.script;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.registry.RegistryType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.ClassVisibilityContext;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KubeJSContext extends Context {
	public final KubeJSContextFactory kjsFactory;
	public final Scriptable topLevelScope;
	private Map<String, Either<NativeJavaClass, Boolean>> javaClassCache;
	public Map<String, ItemStack> itemStackParseCache = new HashMap<>();

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

	public RegistryAccess getRegistries() {
		return kjsFactory.manager.getRegistries();
	}

	public RegistryOps<Tag> getNbtRegistryOps() {
		return kjsFactory.manager.getNbtRegistryOps();
	}

	public RegistryOps<JsonElement> getJsonRegistryOps() {
		return kjsFactory.manager.getJsonRegistryOps();
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
	public int internalConversionWeight(Object fromObj, TypeInfo target) {
		var s = super.internalConversionWeight(fromObj, target);

		if (s == CONVERSION_NONE && target.shouldConvert()) {
			var reg = RegistryType.allOfClass(target.asClass());

			if (!reg.isEmpty()) {
				return CONVERSION_TRIVIAL;
			}
		}

		return s;
	}

	@Override
	protected Object internalJsToJavaLast(Object from, TypeInfo target) {
		// handle ResourceKey, Holder, HolderSet, TagKey

		var reg = RegistryType.allOfClass(target.asClass());

		if (reg.size() == 1) {
			var regInfo = RegistryInfo.of(reg.getFirst().key());
			var value = regInfo.getValue(ID.mc(from));

			if (value != null) {
				return value;
			} else {
				throw reportRuntimeError("Can't interpret '" + from + "' as '" + regInfo + "' registry object", this);
			}
		} else if (!reg.isEmpty()) {
			for (var regType : reg) {
				if (regType.type().equals(target)) {
					var regInfo = RegistryInfo.of(reg.getFirst().key());
					var value = regInfo.getValue(ID.mc(from));

					if (value != null) {
						return value;
					} else {
						throw reportRuntimeError("Can't interpret '" + from + "' as '" + regInfo + "' registry object", this);
					}
				}

				throw reportRuntimeError("Can't find matching registry type '" + target + "' from registries " + reg, this);
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
