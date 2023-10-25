package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.regex.Pattern;

public class LangEventJS extends EventJS {
	public static final Pattern PATTERN = Pattern.compile("[a-z_]+");

	public record Key(String namespace, String lang, String key) {
	}

	public final String lang;
	public final Map<Key, String> map;

	public LangEventJS(String lang, Map<Key, String> map) {
		this.lang = lang;
		this.map = map;
	}

	public void add(String namespace, String key, String value) {
		if (namespace == null || key == null || value == null || namespace.isEmpty() || key.isEmpty() || value.isEmpty()) {
			throw new IllegalArgumentException("Invalid namespace, key or value: [" + namespace + ", " + key + ", " + value + "]");
		}

		map.put(new Key(namespace, lang, key), value);
	}

	public void addAll(String namespace, Map<String, String> map) {
		for (var e : map.entrySet()) {
			add(namespace, e.getKey(), e.getValue());
		}
	}

	public void add(String key, String value) {
		add("minecraft", key, value);
	}

	public void addAll(Map<String, String> map) {
		addAll("minecraft", map);
	}

	public void renameItem(ItemStack item, String name) {
		if (item != null && !item.isEmpty()) {
			var d = item.getDescriptionId();

			if (d != null && !d.isEmpty()) {
				add(item.kjs$getMod(), d, name);
			}
		}
	}

	public void renameBlock(Block block, String name) {
		if (block != null && block != Blocks.AIR) {
			var d = block.getDescriptionId();

			if (d != null && !d.isEmpty()) {
				add(block.kjs$getMod(), d, name);
			}
		}
	}

	public void renameEntity(EntityType<?> entityType, String name) {
		if (entityType != null) {
			var d = entityType.getDescriptionId();

			if (d != null && !d.isEmpty()) {
				var id = RegistryInfo.ENTITY_TYPE.getId(entityType);

				if (id != null) {
					add(id.getNamespace(), d, name);
				}
			}
		}
	}
}
