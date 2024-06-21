package dev.latvian.mods.kubejs.holder;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.holdersets.HolderSetType;
import net.neoforged.neoforge.registries.holdersets.ICustomHolderSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class RegExHolderSet<T> extends HolderSet.ListBacked<T> implements ICustomHolderSet<T> {
	public static <T> MapCodec<RegExHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			RegistryOps.retrieveRegistryLookup(registryKey).fieldOf("registry").forGetter(s -> s.registryLookup),
			RegExpKJS.CODEC.fieldOf("pattern").forGetter(s -> s.pattern)
		).apply(instance, RegExHolderSet::new));
	}

	public final HolderLookup.RegistryLookup<T> registryLookup;
	public final Pattern pattern;

	@Nullable
	private Set<Holder<T>> set = null;

	@Nullable
	private List<Holder<T>> list = null;

	public RegExHolderSet(HolderLookup.RegistryLookup<T> registryLookup, Pattern pattern) {
		this.registryLookup = registryLookup;
		this.pattern = pattern;
	}

	@Override
	public HolderSetType type() {
		return KubeJSHolderSets.REGEX.value();
	}

	@Override
	protected List<Holder<T>> contents() {
		if (list == null) {
			list = List.copyOf(registryLookup.listElements().filter(ref -> pattern.matcher(ref.key().location().toString()).find()).toList());
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
		return "KubeJSRegExHolderSet[" + RegExpKJS.toRegExpString(pattern) + ']';
	}
}
