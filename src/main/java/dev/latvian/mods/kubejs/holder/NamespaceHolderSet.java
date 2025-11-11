package dev.latvian.mods.kubejs.holder;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.CommonProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.holdersets.HolderSetType;
import net.neoforged.neoforge.registries.holdersets.ICustomHolderSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class NamespaceHolderSet<T> extends HolderSet.ListBacked<T> implements ICustomHolderSet<T> {
	public static <T> MapCodec<NamespaceHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			RegistryOps.retrieveRegistryLookup(registryKey).forGetter(s -> s.registryLookup),
			Codec.STRING.fieldOf("namespace").forGetter(s -> s.namespace)
		).apply(instance, NamespaceHolderSet::new));
	}

	public static <T> StreamCodec<RegistryFriendlyByteBuf, NamespaceHolderSet<T>> streamCodec(ResourceKey<? extends Registry<T>> registryKey) {
		return null;
	}

	public final HolderLookup.RegistryLookup<T> registryLookup;
	public final String namespace;

	@Nullable
	private Set<Holder<T>> set = null;

	@Nullable
	private List<Holder<T>> list = null;

	private NamespaceHolderSet(HolderLookup.RegistryLookup<T> registryLookup, String namespace) {
		this.registryLookup = registryLookup;
		this.namespace = namespace;
	}

	public static <T> HolderSet<T> of(HolderLookup.RegistryLookup<T> registryLookup, String namespace) {
		var set = new NamespaceHolderSet<>(registryLookup, namespace);
		if (CommonProperties.get().serverOnly) {
			return HolderSet.direct(set.contents());
		}
		return set;
	}

	@Override
	public HolderSetType type() {
		return KubeJSHolderSets.NAMESPACE.value();
	}

	@Override
	protected List<Holder<T>> contents() {
		if (list == null) {
			list = List.copyOf(registryLookup.listElements().filter(ref -> ref.key().location().getNamespace().equals(namespace)).toList());
		}

		return list;
	}

	@Override
	public Either<TagKey<T>, List<Holder<T>>> unwrap() {
		return Either.right(contents());
	}

	@Override
	public boolean contains(Holder<T> holder) {
		if (set == null) {
			set = Set.copyOf(contents());
		}

		return set.contains(holder);
	}

	@Override
	public Optional<TagKey<T>> unwrapKey() {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return "KubeJSNamespaceHolderSet[" + namespace + ']';
	}
}
