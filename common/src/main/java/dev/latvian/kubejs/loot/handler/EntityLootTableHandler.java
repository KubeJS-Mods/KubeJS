package dev.latvian.kubejs.loot.handler;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EntityLootTableHandler extends LootTableHandler {
	public EntityLootTableHandler(Map<ResourceLocation, JsonElement> tables) {
		super(tables);
	}

	@Override
	public String getType() {
		return "minecraft:entity";
	}

	@Override
	public Set<ResourceLocation> getLocations(Object objects) {
		return KubeJSRegistries.entityTypes()
				.entrySet()
				.stream()
				.filter(entry -> matches(entry.getKey().location(), objects))
				.map(entry -> entry.getValue().getDefaultLootTable())
				.collect(Collectors.toSet());
	}

	protected boolean matches(ResourceLocation id, Object objects) {
		for (Object o : ListJS.orSelf(objects)) {
			Pattern pattern = UtilsJS.parseRegex(o);
			if(pattern != null) {
				return pattern.matcher(id.toString()).matches();
			}

			ResourceLocation resourceLocation = ResourceLocation.tryParse(o.toString());
			if(resourceLocation != null) {
				return resourceLocation.equals(id);
			}
		}

		return false;
	}
}
