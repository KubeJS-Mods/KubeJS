package dev.latvian.mods.kubejs;

import dev.architectury.registry.registries.DeferredRegister;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RegistryObjectBuilderTypes<T> {
	public interface BuilderFactory<T> {
		BuilderBase<? extends T> createBuilder(ResourceLocation id);
	}

	public record BuilderType<T>(String type, Class<? extends BuilderBase<? extends T>> builderClass, BuilderFactory<T> factory) {
	}

	public static class RegistryEventJS<T> extends StartupEventJS {
		private final RegistryObjectBuilderTypes<T> registry;

		private RegistryEventJS(RegistryObjectBuilderTypes<T> r) {
			registry = r;
		}

		public BuilderBase<? extends T> create(String id, String type) {
			var t = registry.types.get(type);

			if (t == null) {
				throw new IllegalArgumentException("Unknown type '" + type + "' for object '" + id + "'!");
			}

			var b = t.factory.createBuilder(UtilsJS.getMCID(KubeJS.appendModId(id)));

			if (b == null) {
				throw new IllegalArgumentException("Unknown type '" + type + "' for object '" + id + "'!");
			} else {
				registry.addBuilder(b);
			}

			return b;
		}

		public BuilderBase<? extends T> create(String id) {
			var t = registry.getDefaultType();

			if (t == null) {
				throw new IllegalArgumentException("Registry for type '" + registry.registryKey.location() + "' doesn't have any builders registered!");
			}

			var b = t.factory.createBuilder(UtilsJS.getMCID(KubeJS.appendModId(id)));

			if (b == null) {
				throw new IllegalArgumentException("Unknown type '" + t.type + "' for object '" + id + "'!");
			} else {
				registry.addBuilder(b);
			}

			return b;
		}
	}

	public static final Map<ResourceKey<?>, RegistryObjectBuilderTypes<?>> MAP = new LinkedHashMap<>();
	public static final List<BuilderBase<?>> ALL_BUILDERS = new ArrayList<>();

	public static <T> RegistryObjectBuilderTypes<T> add(ResourceKey<Registry<T>> key, Class<?> baseClass) {
		var types = new RegistryObjectBuilderTypes<>(key, UtilsJS.cast(baseClass));

		if (MAP.put(key, types) != null) {
			throw new IllegalStateException("Registry with id '" + key + "' already exists!");
		}

		return types;
	}

	public static final RegistryObjectBuilderTypes<SoundEvent> SOUND_EVENT = add(Registry.SOUND_EVENT_REGISTRY, SoundEvent.class);
	// before blocks because FluidBlock needs the fluid to exist first on Fabric
	public static final RegistryObjectBuilderTypes<Fluid> FLUID = add(Registry.FLUID_REGISTRY, Fluid.class);
	public static final RegistryObjectBuilderTypes<Block> BLOCK = add(Registry.BLOCK_REGISTRY, Block.class);
	public static final RegistryObjectBuilderTypes<Item> ITEM = add(Registry.ITEM_REGISTRY, Item.class);
	public static final RegistryObjectBuilderTypes<Enchantment> ENCHANTMENT = add(Registry.ENCHANTMENT_REGISTRY, Enchantment.class);
	public static final RegistryObjectBuilderTypes<MobEffect> MOB_EFFECT = add(Registry.MOB_EFFECT_REGISTRY, MobEffect.class);
	public static final RegistryObjectBuilderTypes<EntityType<?>> ENTITY_TYPE = add(Registry.ENTITY_TYPE_REGISTRY, EntityType.class);
	public static final RegistryObjectBuilderTypes<BlockEntityType<?>> BLOCK_ENTITY_TYPE = add(Registry.BLOCK_ENTITY_TYPE_REGISTRY, BlockEntityType.class);
	public static final RegistryObjectBuilderTypes<Potion> POTION = add(Registry.POTION_REGISTRY, Potion.class);
	public static final RegistryObjectBuilderTypes<ParticleType<?>> PARTICLE_TYPE = add(Registry.PARTICLE_TYPE_REGISTRY, ParticleType.class);
	public static final RegistryObjectBuilderTypes<PaintingVariant> PAINTING_VARIANT = add(Registry.PAINTING_VARIANT_REGISTRY, PaintingVariant.class);
	public static final RegistryObjectBuilderTypes<ResourceLocation> CUSTOM_STAT = add(Registry.CUSTOM_STAT_REGISTRY, ResourceLocation.class);
	public static final RegistryObjectBuilderTypes<PoiType> POINT_OF_INTEREST_TYPE = add(Registry.POINT_OF_INTEREST_TYPE_REGISTRY, PoiType.class);
	public static final RegistryObjectBuilderTypes<VillagerType> VILLAGER_TYPE = add(Registry.VILLAGER_TYPE_REGISTRY, VillagerType.class);
	public static final RegistryObjectBuilderTypes<VillagerProfession> VILLAGER_PROFESSION = add(Registry.VILLAGER_PROFESSION_REGISTRY, VillagerProfession.class);

	public final ResourceKey<Registry<T>> registryKey;
	public final Class<T> objectBaseClass;
	public final DeferredRegister<T> deferredRegister;
	public final Map<String, BuilderType<T>> types;
	public final Map<ResourceLocation, BuilderBase<? extends T>> objects;
	private BuilderType<T> defaultType;
	public BuilderBase<? extends T> current;
	public boolean bypassServerOnly;

	private RegistryObjectBuilderTypes(ResourceKey<Registry<T>> key, Class<T> baseClass) {
		registryKey = key;
		objectBaseClass = baseClass;
		deferredRegister = DeferredRegister.create(KubeJS.MOD_ID, registryKey);
		types = new LinkedHashMap<>();
		objects = new LinkedHashMap<>();
		current = null;
		bypassServerOnly = false;
	}

	public RegistryObjectBuilderTypes<T> bypassServerOnly() {
		bypassServerOnly = true;
		return this;
	}

	public void addType(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory<T> factory, boolean isDefault) {
		var b = new BuilderType<>(type, builderType, factory);
		types.put(type, b);

		if (isDefault) {
			if (defaultType != null) {
				ConsoleJS.STARTUP.warn("Previous default type '" + defaultType.type + "' for registry '" + registryKey.location() + "' replaced with '" + type + "'!");
			}

			defaultType = b;
		}
	}

	public void addType(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory<T> factory) {
		addType(type, builderType, factory, type.equals("basic"));
	}

	public void addBuilder(BuilderBase<? extends T> builder) {
		if (builder == null) {
			throw new IllegalArgumentException("Can't add null builder in registry '" + registryKey.location() + "'!");
		}

		if (CommonProperties.get().debugInfo) {
			ConsoleJS.STARTUP.setLineNumber(true);
			ConsoleJS.STARTUP.info("~ " + registryKey.location() + " | " + builder.id);
			ConsoleJS.STARTUP.setLineNumber(false);
		}

		if (objects.containsKey(builder.id)) {
			throw new IllegalArgumentException("Duplicate key '" + builder.id + "' in registry '" + registryKey.location() + "'!");
		}

		objects.put(builder.id, builder);
		ALL_BUILDERS.add(builder);
	}

	@Nullable
	public BuilderType<T> getDefaultType() {
		if (types.isEmpty()) {
			return null;
		} else if (defaultType == null) {
			defaultType = types.values().iterator().next();
		}

		return defaultType;
	}

	void postEvent(String id) {
		if (!types.isEmpty()) {
			new RegistryEventJS<>(this).post(id);
		}
	}

	static void registerAll(boolean all) {
		for (var builder : new ArrayList<>(ALL_BUILDERS)) {
			builder.createAdditionalObjects();
		}

		for (var type : MAP.values()) {
			boolean any = false;

			for (var builder : type.objects.values()) {
				if (builder.registerObject(all)) {
					any = true;
				}
			}

			if (any) {
				type.deferredRegister.register();
			}
		}
	}

	@Nullable
	public BuilderBase<? extends T> getCurrent() {
		return current;
	}
}
