package dev.latvian.mods.kubejs.holder;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.registries.holdersets.HolderSetType;
import net.neoforged.neoforge.registries.holdersets.ICustomHolderSet;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public record RegExHolderSet<T>(HolderLookup.RegistryLookup<T> registryLookup, Pattern pattern) implements ICustomHolderSet<T> {
	public static <T> MapCodec<RegExHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			RegistryOps.retrieveRegistryLookup(registryKey).fieldOf("registry").forGetter(RegExHolderSet::registryLookup),
			RegExpKJS.CODEC.fieldOf("pattern").forGetter(RegExHolderSet::pattern)
		).apply(instance, RegExHolderSet::new));
	}

	@Override
	public HolderSetType type() {
		// return KubeJSHolderSets.REGEX.value();
		return null;
	}

	@Override
	public Stream<Holder<T>> stream() {
		return Stream.empty();
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Either<TagKey<T>, List<Holder<T>>> unwrap() {
		return null;
	}

	@Override
	public Optional<Holder<T>> getRandomElement(RandomSource random) {
		return Optional.empty();
	}

	@Override
	public Holder<T> get(int index) {
		return null;
	}

	@Override
	public boolean contains(Holder<T> holder) {
		return false;
	}

	@Override
	public boolean canSerializeIn(HolderOwner<T> owner) {
		return false;
	}

	@Override
	public Optional<TagKey<T>> unwrapKey() {
		return Optional.empty();
	}

	@NotNull
	@Override
	public Iterator<Holder<T>> iterator() {
		return null;
	}
}
