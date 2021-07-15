package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.kubejs.util.CountingMap;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import me.shedaniel.architectury.registry.ToolType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stat;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class UtilsWrapper {
	public static ServerJS getServer() {
		return ServerJS.instance;
	}

	public static void queueIO(Runnable runnable) {
		UtilsJS.queueIO(runnable);
	}

	public static Random getRandom() {
		return UtilsJS.RANDOM;
	}

	public static Random newRandom(long seed) {
		return new Random(seed);
	}

	public static <T> List<T> emptyList() {
		return Collections.emptyList();
	}

	public static <K, V> Map<K, V> emptyMap() {
		return Collections.emptyMap();
	}

	public static ListJS newList() {
		return new ListJS();
	}

	public static MapJS newMap() {
		return new MapJS();
	}

	public static CountingMap newCountingMap() {
		return new CountingMap();
	}

	public static ResourceLocation id(String namespace, String path) {
		return new ResourceLocation(namespace, path);
	}

	public static ResourceLocation id(String id) {
		return UtilsJS.getMCID(id);
	}

	public static ResourceLocation id(ResourceLocation id) {
		return id;
	}

	public static ConsoleJS createConsole(String name) {
		return new ConsoleJS(ScriptType.STARTUP, LogManager.getLogger(name));
	}

	public static Pattern regex(Object s) {
		Pattern pattern = UtilsJS.parseRegex(s);
		return pattern == null ? Pattern.compile(s.toString()) : pattern;
	}

	public static Pattern regex(String pattern, int flags) {
		return Pattern.compile(pattern, flags);
	}

	public static int parseInt(@Nullable Object object, int def) {
		return UtilsJS.parseInt(object, def);
	}

	public static double parseDouble(@Nullable Object object, double def) {
		return UtilsJS.parseDouble(object, def);
	}

	public static Stat<ResourceLocation> getStat(ResourceLocation id) {
		return UtilsJS.getStat(id);
	}

	public static ToolType getToolType(String id) {
		return UtilsJS.getToolType(id);
	}

	public static WorldJS getWorld(Level world) {
		if (world.isClientSide()) {
			return getClientWorld();
		} else {
			return ServerJS.instance.getWorld(world);
		}
	}

	public static WorldJS getClientWorld() {
		return ClientWorldJS.getInstance();
	}

	@Nullable
	public static SoundEvent getSound(ResourceLocation id) {
		return KubeJSRegistries.soundEvents().get(id);
	}

	public static Object randomOf(Random random, Collection<Object> objects) {
		if (objects.isEmpty()) {
			return null;
		}

		if (objects instanceof List) {
			return ((List) objects).get(random.nextInt(objects.size()));
		} else {
			return new ArrayList<>(objects).get(random.nextInt(objects.size()));
		}
	}

	public static long getSystemTime() {
		return System.currentTimeMillis();
	}

	public static Overlay overlay(String id, Object[] text) {
		Overlay o = new Overlay(id);

		for (Object o1 : text) {
			o.add(o1);
		}

		return o;
	}

	@Nullable
	public static MobEffect getPotion(ResourceLocation id) {
		return UtilsJS.getPotion(id);
	}

	public static ListJS rollChestLoot(ResourceLocation id) {
		return rollChestLoot(id, null);
	}

	public static ListJS rollChestLoot(ResourceLocation id, @Nullable EntityJS entity) {
		return UtilsJS.rollChestLoot(id, entity);
	}

	@Nullable
	public static ListJS listOf(@Nullable Object o) {
		return ListJS.of(o);
	}

	public static ListJS listOrSelf(@Nullable Object o) {
		return ListJS.orSelf(o);
	}

	@Nullable
	public static MapJS mapOf(@Nullable Object o) {
		return MapJS.of(o);
	}

	@Nullable
	public static Object copy(@Nullable Object o) {
		return UtilsJS.copy(o);
	}
}