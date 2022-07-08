package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.mod.util.CountingMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public interface UtilsWrapper {
	static ServerJS getServer() {
		return ServerJS.instance;
	}

	static void queueIO(Runnable runnable) {
		UtilsJS.queueIO(runnable);
	}

	static Random getRandom() {
		return UtilsJS.RANDOM;
	}

	static Random newRandom(long seed) {
		return new Random(seed);
	}

	static <T> List<T> emptyList() {
		return Collections.emptyList();
	}

	static <K, V> Map<K, V> emptyMap() {
		return Collections.emptyMap();
	}

	static List<?> newList() {
		return new ArrayList<>();
	}

	static Map<?, ?> newMap() {
		return new LinkedHashMap<>();
	}

	static CountingMap newCountingMap() {
		return new CountingMap();
	}

	static ResourceLocation id(String namespace, String path) {
		return new ResourceLocation(namespace, path);
	}

	static ResourceLocation id(ResourceLocation id) {
		// TypeWrapper will convert any object into RL
		return id;
	}

	static ConsoleJS createConsole(String name) {
		return new ConsoleJS(ScriptType.STARTUP, LoggerFactory.getLogger(name));
	}

	static Pattern regex(Object s) {
		var pattern = UtilsJS.parseRegex(s);
		return pattern == null ? Pattern.compile(s.toString()) : pattern;
	}

	static Pattern regex(String pattern, int flags) {
		return Pattern.compile(pattern, flags);
	}

	static int parseInt(@Nullable Object object, int def) {
		return UtilsJS.parseInt(object, def);
	}

	static double parseDouble(@Nullable Object object, double def) {
		return UtilsJS.parseDouble(object, def);
	}

	static Stat<ResourceLocation> getStat(ResourceLocation id) {
		return Stats.CUSTOM.get(id);
	}

	static LevelJS getLevel(Level level) {
		return UtilsJS.getLevel(level);
	}

	static LevelJS getClientLevel() {
		return UtilsJS.getClientLevel();
	}

	@Nullable
	static SoundEvent getSound(ResourceLocation id) {
		return KubeJSRegistries.soundEvents().get(id);
	}

	static Object randomOf(Random random, Collection<Object> objects) {
		if (objects.isEmpty()) {
			return null;
		}

		if (objects instanceof List<?> list) {
			return list.get(random.nextInt(objects.size()));
		} else {
			return new ArrayList<>(objects).get(random.nextInt(objects.size()));
		}
	}

	static long getSystemTime() {
		return System.currentTimeMillis();
	}

	static List<ItemStackJS> rollChestLoot(ResourceLocation id) {
		return rollChestLoot(id, null);
	}

	static List<ItemStackJS> rollChestLoot(ResourceLocation id, @Nullable EntityJS entity) {
		return UtilsJS.rollChestLoot(id, entity);
	}

	@Nullable
	static Object copy(@Nullable Object o) {
		return UtilsJS.copy(o);
	}

	static boolean isWrapped(@Nullable Object o) {
		return o instanceof WrappedJS;
	}

	static String toTitleCase(String s) {
		return UtilsJS.toTitleCase(s);
	}
}