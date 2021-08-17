package dev.latvian.kubejs.loot.handler;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CustomLootTableHandler extends LootTableHandler {
	/**
	 * invalidTypes does not contain "minecraft:block" and "minecraft:entity" because they are
	 * covered through the additional handlers. Some mods add the entity or block type to their table
	 * if they inject them but we still want to find them through the custom handler.
	 */
	private final List<String> invalidTypes = Arrays.asList("minecraft:chest", "minecraft:fishing", "minecraft:gift");
	private final BlockLootTableHandler blockHandler;
	private final EntityLootTableHandler entityHandler;

	public CustomLootTableHandler(Map<ResourceLocation, JsonElement> tables) {
		super(tables);
		blockHandler = new BlockLootTableHandler(tables);
		entityHandler = new EntityLootTableHandler(tables);
	}

	@Override
	public String getType() {
		return null;
	}

	@Override
	protected boolean hasCorrectType(ResourceLocation id) {
		return !invalidTypes.contains(getTypeFromTable(id));
	}

	@Override
	protected @Nullable ResourceLocation tryGetLootTableId(ResourceLocation id) {
		return getLocations(id).stream().findFirst().orElse(null);
	}

	@Override
	public Set<ResourceLocation> getLocations(Object objects) {
		Set<ResourceLocation> invalidTables = blockHandler.getLocations(objects);
		invalidTables.addAll(entityHandler.getLocations(objects));

		return lootTables.keySet()
				.stream()
				.filter(rl -> matches(rl, objects) && hasCorrectType(rl) && !invalidTables.contains(rl))
				.collect(Collectors.toSet());
	}

	public boolean matches(ResourceLocation id, Object objects) {
		for (Object o : ListJS.orSelf(objects)) {
			Pattern pattern = UtilsJS.parseRegex(o);
			if(pattern != null) {
				return pattern.matcher(id.toString()).matches();
			}

			ResourceLocation rl = ResourceLocation.tryParse(o.toString());
			if(rl != null) {
				return id.equals(rl);
			}
		}

		return false;
	}
}
