package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class RegistryInfo<T> implements Iterable<BuilderBase<? extends T>>, TypeWrapperFactory<T> {
	private static final Object LOCK = new Object();
	private static final Map<ResourceKey<? extends Registry<?>>, RegistryInfo<?>> MAP = new IdentityHashMap<>();
	private static final Map<Class<?>, List<RegistryInfo<?>>> CLASS_MAP = new IdentityHashMap<>();
	public static final List<BuilderBase<?>> ALL_BUILDERS = new LinkedList<>();

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static <T> RegistryInfo<T> of(ResourceKey<Registry<T>> key, Class<? extends T> type) {
		synchronized (LOCK) {
			var r = MAP.get(key);

			if (r == null) {
				var reg = new RegistryInfo(key, type);
				MAP.put(key, reg);
				CLASS_MAP.computeIfAbsent(type, k -> new ArrayList<>(1)).add(reg);
				return reg;
			}

			return (RegistryInfo<T>) r;
		}
	}

	public static <T> RegistryInfo<T> of(ResourceKey<Registry<T>> key) {
		synchronized (LOCK) {
			return Cast.to(Objects.requireNonNull(MAP.get(key)));
		}
	}

	@Nullable
	public static RegistryInfo<?> ofClass(Class<?> type) {
		if (type == Object.class) {
			return null;
		} else if (type == Block.class) {
			return BLOCK;
		} else if (type == Item.class) {
			return ITEM;
		} else {
			var list = CLASS_MAP.get(type);
			return list == null || list.size() != 1 ? null : list.getFirst();
		}
	}

	public static List<RegistryInfo<?>> allOfClass(Class<?> type) {
		return CLASS_MAP.getOrDefault(type, List.of());
	}

	static {
		// FIXME: Reflection cursedness

		try {
			for (var field : Registries.class.getDeclaredFields()) {
				if (field.getType() == ResourceKey.class && field.getGenericType() instanceof ParameterizedType t1 && t1.getActualTypeArguments()[0] instanceof ParameterizedType t2) {
					var type = t2.getActualTypeArguments()[0];
					var typeInfo = TypeInfo.of(type);
					KubeJS.LOGGER.info(typeInfo + ": " + field);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static final RegistryInfo<SoundEvent> SOUND_EVENT = of(Registries.SOUND_EVENT);
	public static final RegistryInfo<Fluid> FLUID = of(Registries.FLUID);
	public static final RegistryInfo<MobEffect> MOB_EFFECT = of(Registries.MOB_EFFECT).languageKeyPrefix("effect");
	public static final RegistryInfo<Block> BLOCK = of(Registries.BLOCK);
	public static final RegistryInfo<Enchantment> ENCHANTMENT = of(Registries.ENCHANTMENT);
	public static final RegistryInfo<EntityType<?>> ENTITY_TYPE = of(Registries.ENTITY_TYPE);
	public static final RegistryInfo<Item> ITEM = of(Registries.ITEM).noAutoWrap();
	public static final RegistryInfo<Potion> POTION = of(Registries.POTION);
	public static final RegistryInfo<ParticleType<?>> PARTICLE_TYPE = of(Registries.PARTICLE_TYPE);
	public static final RegistryInfo<BlockEntityType<?>> BLOCK_ENTITY_TYPE = of(Registries.BLOCK_ENTITY_TYPE);
	public static final RegistryInfo<PaintingVariant> PAINTING_VARIANT = of(Registries.PAINTING_VARIANT);
	public static final RegistryInfo<ResourceLocation> CUSTOM_STAT = of(Registries.CUSTOM_STAT);
	public static final RegistryInfo<MenuType<?>> MENU = of(Registries.MENU);
	public static final RegistryInfo<RecipeSerializer<?>> RECIPE_SERIALIZER = of(Registries.RECIPE_SERIALIZER);
	public static final RegistryInfo<Attribute> ATTRIBUTE = of(Registries.ATTRIBUTE);
	public static final RegistryInfo<VillagerProfession> VILLAGER_PROFESSION = of(Registries.VILLAGER_PROFESSION);
	public static final RegistryInfo<VillagerType> VILLAGER_TYPE = of(Registries.VILLAGER_TYPE);
	public static final RegistryInfo<ArmorMaterial> ARMOR_MATERIAL = of(Registries.ARMOR_MATERIAL);
	public static final RegistryInfo<PoiType> POINT_OF_INTEREST_TYPE = of(Registries.POINT_OF_INTEREST_TYPE);
	public static final RegistryInfo<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPE = of(Registries.COMMAND_ARGUMENT_TYPE);
	public static final RegistryInfo<CreativeModeTab> CREATIVE_MODE_TAB = of(Registries.CREATIVE_MODE_TAB);

	public final ResourceKey<Registry<T>> key;
	public final Class<?> objectBaseClass;
	public final Map<String, BuilderType<T>> types;
	public final Map<ResourceLocation, BuilderBase<? extends T>> objects;
	public final ResourceKey<T> unknownKey;
	public boolean hasDefaultTags = false;
	private BuilderType<T> defaultType;
	public boolean bypassServerOnly;
	public boolean autoWrap;
	public String languageKeyPrefix;
	private WeakReference<Registry<T>> vanillaRegistry;

	private RegistryInfo(ResourceKey<Registry<T>> key, Class<T> objectBaseClass) {
		this.key = key;
		this.objectBaseClass = objectBaseClass;
		this.types = new LinkedHashMap<>();
		this.objects = new LinkedHashMap<>();
		this.unknownKey = ResourceKey.create(key, ID.UNKNOWN);
		this.bypassServerOnly = false;
		this.autoWrap = objectBaseClass != Codec.class && objectBaseClass != ResourceLocation.class && objectBaseClass != String.class;
		this.languageKeyPrefix = key.location().getPath().replace('/', '.');
	}

	public RegistryInfo<T> bypassServerOnly() {
		this.bypassServerOnly = true;
		return this;
	}

	public RegistryInfo<T> noAutoWrap() {
		this.autoWrap = false;
		return this;
	}

	public RegistryInfo<T> languageKeyPrefix(String prefix) {
		this.languageKeyPrefix = prefix;
		return this;
	}

	public void addType(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory, boolean isDefault) {
		var b = new BuilderType<>(type, builderType, factory);
		types.put(type, b);

		if (isDefault) {
			if (defaultType != null) {
				ConsoleJS.STARTUP.warn("Previous default type '" + defaultType.type() + "' for registry '" + key.location() + "' replaced with '" + type + "'!");
			}

			defaultType = b;
		}
	}

	public void addType(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
		addType(type, builderType, factory, type.equals("basic"));
	}

	public void addBuilder(BuilderBase<? extends T> builder) {
		if (builder == null) {
			throw new IllegalArgumentException("Can't add null builder in registry '" + key.location() + "'!");
		}

		if (DevProperties.get().debugInfo) {
			ConsoleJS.STARTUP.info("~ " + key.location() + " | " + builder.id);
		}

		if (objects.containsKey(builder.id)) {
			throw new IllegalArgumentException("Duplicate key '" + builder.id + "' in registry '" + key.location() + "'!");
		}

		objects.put(builder.id, builder);
		ALL_BUILDERS.add(builder);
	}

	@Nullable
	public BuilderType getDefaultType() {
		if (types.isEmpty()) {
			return null;
		} else if (defaultType == null) {
			defaultType = types.values().iterator().next();
		}

		return defaultType;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof RegistryInfo ri && key.equals(ri.key);
	}

	@Override
	public String toString() {
		return key.location().toString();
	}

	public int registerObjects(RegistryCallback<T> function) {
		if (DevProperties.get().debugInfo) {
			if (objects.isEmpty()) {
				KubeJS.LOGGER.info("Skipping " + this + " registry");
			} else {
				KubeJS.LOGGER.info("Building " + objects.size() + " objects of " + this + " registry");
			}
		}

		if (objects.isEmpty()) {
			return 0;
		}

		int added = 0;

		for (var builder : this) {
			if (!builder.dummyBuilder && (builder.getRegistryType().bypassServerOnly || !CommonProperties.get().serverOnly)) {
				function.accept(builder.id, builder::createTransformedObject);

				if (DevProperties.get().debugInfo) {
					ConsoleJS.STARTUP.info("+ " + this + " | " + builder.id);
				}

				added++;
			}
		}

		if (!objects.isEmpty() && DevProperties.get().debugInfo) {
			KubeJS.LOGGER.info("Registered " + added + "/" + objects.size() + " objects of " + this);
		}

		return added;
	}

	@NotNull
	@Override
	public Iterator<BuilderBase<? extends T>> iterator() {
		return objects.values().iterator();
	}

	public Registry<T> getVanillaRegistry() {
		var reg = vanillaRegistry == null ? null : vanillaRegistry.get();

		if (reg == null) {
			reg = BuiltInRegistries.REGISTRY.get((ResourceKey) key);

			if (reg != null) {
				vanillaRegistry = new WeakReference<>(reg);
			}
		}

		return reg;
	}

	public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
		return getVanillaRegistry().entrySet();
	}

	public ResourceLocation getId(T value) {
		return getVanillaRegistry().getKey(value);
	}

	public T getValue(ResourceLocation id) {
		return getVanillaRegistry().get(id);
	}

	public Holder.Reference<T> getHolder(ResourceLocation id) {
		return getVanillaRegistry().getHolder(id).orElseThrow();
	}

	public Holder.Reference<T> getHolder(ResourceKey<T> key) {
		return getVanillaRegistry().getHolderOrThrow(key);
	}

	public Holder<T> getHolderOf(T value) {
		return getVanillaRegistry().wrapAsHolder(value);
	}

	public boolean hasValue(ResourceLocation id) {
		return getVanillaRegistry().containsKey(id);
	}

	public ResourceKey<T> getKeyOf(T value) {
		return getVanillaRegistry().getResourceKey(value).orElse(unknownKey);
	}

	@Override
	public T wrap(Context cx, Object o, TypeInfo target) {
		if (o == null) {
			return null;
		} else if (objectBaseClass.isInstance(o)) {
			return (T) o;
		}

		var id = ID.mc(o);
		var value = getValue(id);

		if (value == null) {
			var npe = new NullPointerException("No such element with id %s in registry %s!".formatted(id, this));
			ConsoleJS.getCurrent(cx).error("Error while wrapping registry element type!", npe);
			throw npe;
		}

		return value;
	}

	public void fireRegistryEvent() {
		var event = new RegistryKubeEvent<>(this);
		StartupEvents.REGISTRY.post(event, (ResourceKey) key);
		event.created.forEach(BuilderBase::createAdditionalObjects);
	}
}
