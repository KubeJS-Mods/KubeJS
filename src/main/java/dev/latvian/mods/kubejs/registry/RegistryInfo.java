package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.core.RegistryObjectKJS;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ID;
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
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RegistryInfo<T> implements Iterable<BuilderBase<? extends T>> {
	private static final Object LOCK = new Object();
	private static final Map<ResourceKey<? extends Registry<?>>, RegistryInfo<?>> MAP = new IdentityHashMap<>();
	public static final List<BuilderBase<?>> ALL_BUILDERS = new LinkedList<>();

	public static final Codec<RegistryInfo<?>> CODEC = ResourceLocation.CODEC.xmap(rl -> RegistryInfo.of(ResourceKey.createRegistryKey(rl)), ri -> ri.key.location());

	public static <T> RegistryInfo<T> of(ResourceKey<Registry<T>> key) {
		synchronized (LOCK) {
			return Cast.to(MAP.computeIfAbsent(key, RegistryInfo::new));
		}
	}

	public static RegistryInfo<?> wrap(Object from) {
		return switch (from) {
			case RegistryInfo r -> r;
			case ResourceKey k -> of(k);
			case Registry r -> of(r.key());
			case RegistryType t -> of(t.key());
			case String ignore -> of(ResourceKey.createRegistryKey(ID.mc(from)));
			case RegistryObjectKJS<?> r -> r.kjs$getKubeRegistry();
			case Holder<?> h -> of(h.unwrapKey().orElseThrow().registryKey());
			case null, default -> throw new IllegalArgumentException("Invalid registry: " + from);
		};
	}

	public static final RegistryInfo<SoundEvent> SOUND_EVENT = of(Registries.SOUND_EVENT);
	public static final RegistryInfo<FluidType> FLUID_TYPE = of(NeoForgeRegistries.Keys.FLUID_TYPES);
	public static final RegistryInfo<Fluid> FLUID = of(Registries.FLUID);
	public static final RegistryInfo<MobEffect> MOB_EFFECT = of(Registries.MOB_EFFECT).languageKeyPrefix("effect");
	public static final RegistryInfo<Block> BLOCK = of(Registries.BLOCK);
	public static final RegistryInfo<EntityType<?>> ENTITY_TYPE = of(Registries.ENTITY_TYPE);
	public static final RegistryInfo<Item> ITEM = of(Registries.ITEM);
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
	public static final RegistryInfo<JukeboxSong> JUKEBOX_SONG = of(Registries.JUKEBOX_SONG);

	public final ResourceKey<Registry<T>> key;
	BuilderType<T> defaultType;
	Map<String, BuilderType<T>> types;
	public final Map<ResourceLocation, BuilderBase<? extends T>> objects;
	public final ResourceKey<T> unknownKey;
	public boolean hasDefaultTags = false;
	public boolean bypassServerOnly;
	public String languageKeyPrefix;
	private WeakReference<Registry<T>> vanillaRegistry;
	private Codec<T> codec;

	private RegistryInfo(ResourceKey key) {
		this.key = key;
		this.objects = new LinkedHashMap<>();
		this.unknownKey = ResourceKey.create(key, ID.UNKNOWN);
		this.bypassServerOnly = false;
		this.languageKeyPrefix = key.location().getPath().replace('/', '.');
	}

	public RegistryInfo<T> bypassServerOnly() {
		this.bypassServerOnly = true;
		return this;
	}

	public RegistryInfo<T> languageKeyPrefix(String prefix) {
		this.languageKeyPrefix = prefix;
		return this;
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

	public Codec<T> valueByNameCodec() {
		if (codec == null) {
			codec = getVanillaRegistry().byNameCodec();
		}

		return codec;
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
}
