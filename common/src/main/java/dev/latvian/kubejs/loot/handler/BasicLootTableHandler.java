package dev.latvian.kubejs.loot.handler;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BasicLootTableHandler extends LootTableHandler {
	private final String type;
	private final String pathToContain;

	public BasicLootTableHandler(Map<ResourceLocation, JsonElement> tables, String type, String pathToContain) {
		super(tables);
		this.type = type;
		this.pathToContain = pathToContain;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Set<ResourceLocation> getLocations(Object objects) {
		return lootTables.keySet()
				.stream()
				.filter(id -> id.getPath().contains(pathToContain) && matches(id, objects) && hasCorrectType(id))
				.collect(Collectors.toSet());
	}

	protected boolean matches(ResourceLocation id, Object objects) {
		for (Object o : ListJS.orSelf(objects)) {
			ResourceLocation idWithoutPath = extractPath(id);
			Pattern pattern = UtilsJS.parseRegex(o);
			if (pattern != null) {
				return pattern.matcher(id.toString()).matches()
						|| pattern.matcher(idWithoutPath.toString()).matches();
			}

			ResourceLocation resourceLocation = ResourceLocation.tryParse(o.toString());
			if (resourceLocation != null) {
				return resourceLocation.equals(id)
						|| resourceLocation.equals(idWithoutPath);
			}
		}

		return false;
	}

	protected ResourceLocation extractPath(ResourceLocation id) {
		String[] splitPath = id.getPath().split("/");
		return new ResourceLocation(id.getNamespace(), splitPath[splitPath.length - 1]);
	}
}
