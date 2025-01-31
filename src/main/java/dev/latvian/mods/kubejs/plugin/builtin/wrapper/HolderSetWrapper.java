package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.google.common.collect.Iterators;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record HolderSetWrapper<T>(Registry<T> registry, HolderSet<T> holders) implements Iterable<T> {

	public int size() {
		return holders.size();
	}

	public boolean isEmpty() {
		return holders.size() == 0;
	}

	public boolean contains(ResourceLocation id) {
		return registry.getHolder(id).filter(holders::contains).isPresent();
	}

	public boolean containsValue(T value) {
		return holders.contains(registry.wrapAsHolder(value));
	}

	public List<T> getValues() {
		return holders.stream().map(Holder::value).toList();
	}

	public Set<ResourceLocation> getKeys() {
		return holders.stream().map(holder -> {
			var key = holder.getKey();
			if (key == null) {
				return null;
			}

			return key.location();
		}).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	@Nullable
	public T getRandom() {
		return getRandom(UtilsJS.RANDOM);
	}

	@Nullable
	public T getRandom(RandomSource random) {
		return holders.getRandomElement(random).map(Holder::value).orElse(null);
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return Iterators.transform(holders.iterator(), Holder::value);
	}
}
