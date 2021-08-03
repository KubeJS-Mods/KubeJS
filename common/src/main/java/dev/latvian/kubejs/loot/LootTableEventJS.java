package dev.latvian.kubejs.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.block.BlockStatePredicate;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LootTableEventJS extends EventJS {
	private static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");
	private static final String TYPE_BLOCK = "minecraft:block";

	private final Map<ResourceLocation, JsonElement> lootTables;

	public LootTableEventJS(Map<ResourceLocation, JsonElement> map1) {
		super();
		this.lootTables = map1;
	}

	public void testSelfBuild(LootTableBuilder builder, ResourceLocation resourceLocation) {
		JsonObject jsonObjectB = (JsonObject) lootTables.get(resourceLocation);

		/*
		  Mojang takes numbers as numbers even if there are covert in "". And Mojang cannot
		  decide if one loot table numbers should be in "" and in another loot table numbers
		  should be numbers.
		  And MapJS covers the same logic. But GSON not.
		  This is the reason why we do the MapJS.of().toJson() thing.
		  Without it a "2" == 2 will fail. And we don't want it to fail.
		 */
		boolean equal = JsonUtilsJS.equal(MapJS.of(builder.toJson()).toJson(), MapJS.of(jsonObjectB).toJson());
		if (equal) {
			ScriptType.SERVER.console.info(String.format("Test check with builder against '%s' was successful.", resourceLocation));
		} else {
			ScriptType.SERVER.console.error(String.format("ERROR: Test check with builder against and '%s' FAILED.", resourceLocation));
		}
	}

	public void testSelfBuild(ResourceLocation resourceLocation) {
		LootTableBuilder builder = get(resourceLocation);
		testSelfBuild(builder, resourceLocation);
	}

	public void forEachTables(Consumer<ResourceLocation> consumer) {
		lootTables.forEach((resourceLocation, element) -> {
			consumer.accept(resourceLocation);
		});
	}

	public int countTables() {
		return lootTables.size();
	}

	public void raw(ResourceLocation id, Consumer<MapJS> consumer) {
		JsonObject jsonObject = (JsonObject) lootTables.get(id);
		if (jsonObject == null) {
			throw new IllegalArgumentException(String.format("No loot table for resource '%s'.", id));
		}

		MapJS tableAsMap = MapJS.of(jsonObject);
		if (tableAsMap == null) {
			throw new IllegalStateException(String.format("Converting internal loot table '%s' to MapJS went wrong. Please report this to the devs", id));
		}

		consumer.accept(tableAsMap);
		addIfContains(id, tableAsMap.toJson());
	}

	private boolean addIfContains(ResourceLocation key, JsonObject json) {
		if (lootTables.containsKey(key)) {
			lootTables.put(key, json);
			return true;
		}

		return false;
	}

	public void clear(ResourceLocation id) {
		modify(id, LootTableBuilder::clear);
	}

	public LootTableBuilder get(ResourceLocation id) {
		JsonObject jsonObject = (JsonObject) lootTables.get(id);
		if (jsonObject == null) {
			throw new IllegalArgumentException(String.format("No loot table for resource '%s'.", id));
		}

		return new LootTableBuilder(jsonObject);
	}

	public void modify(ResourceLocation id, Consumer<LootTableBuilder> consumer) {
		LootTableBuilder builder = get(id);
		if (builder == null) {
			throw new IllegalArgumentException(String.format("No loot table for '%s' found.", id));
		}

		consumer.accept(builder);

		addIfContains(id, builder.toJson());
	}

	public void modifyBlock(ResourceLocation block, Consumer<LootTableBuilder> consumer) {
		ResourceLocation tableLocation = getBlockResourceLocation(block);
		modify(tableLocation, consumer);
	}

	public void replaceBlocks(Object blocks, Consumer<LootTableBuilder> consumer) {
		LootTableBuilder builder = new LootTableBuilder(TYPE_BLOCK);
		consumer.accept(builder);

		getValidBlocks(blocks).forEach(resourceLocation -> {
			if(addIfContains(getBlockResourceLocation(resourceLocation), builder.toJson())) {
				ScriptType.SERVER.console.info(String.format("'%s' was replaced with the new loot table.", resourceLocation));
			}
		});
	}

	public void addBlock() {
	}

	public void modifyEntity(ResourceLocation entity, Consumer<LootTableBuilder> consumer) {
		ResourceLocation tableLocation = getEntityResourceLocation(entity);
		modify(tableLocation, consumer);
	}

	public void modifyChest(ResourceLocation chest, Consumer<LootTableBuilder> consumer) {
		ResourceLocation tableLocation = getChestResourceLocation(chest);
		modify(tableLocation, consumer);
	}

	private List<ResourceLocation> getValidBlocks(Object blocks) {
		List<ResourceLocation> collect = BlockStatePredicate.of(blocks).getBlocks()
				.stream()
				.map(block -> KubeJSRegistries.blocks().getId(block))
				.filter(rl -> rl != null && !rl.equals(AIR_ID))
				.collect(Collectors.toList());

		return collect;
	}

	/*
	public void modifyBlock(ResourceLocation block, Consumer<LootTableBuilder> consumer) {
		modify(getBlockResourceLocation(block), consumer);
	}

	public void buildBlock(Object blocks, Consumer<LootTableBuilder> consumer) {
		LootTableBuilder builder = new LootTableBuilder("minecraft:block");
		consumer.accept(builder);

		for (Block block : BlockStatePredicate.of(blocks).getBlocks()) {
			ResourceLocation blockId = KubeJSRegistries.blocks().getId(block);
			if (blockId != null && !blockId.equals(AIR_ID)) {
				lootTables.put(getBlockResourceLocation(blockId), builder.toJson());
			}
		}
	}

	public void simpleBlockDrop(Object blocks) {
		for (Block block : BlockStatePredicate.of(blocks).getBlocks()) {
			ResourceLocation itemId = KubeJSRegistries.items().getId(block.asItem());
			ResourceLocation blockId = KubeJSRegistries.blocks().getId(block);
			if (blockId != null && itemId != null && !itemId.equals(AIR_ID) && !blockId.equals(AIR_ID)) {
				simpleBlockDrop(block, itemId);
			}
		}
	}

	public void simpleBlockDrop(Object blocks, Object ingredient) {
		IngredientJS ingredientJS = IngredientJS.of(ingredient);

		buildBlock(blocks, table -> {
			table.pool(pool -> {
				pool.addEntry(ingredientJS);
				pool.conditions.survivesExplosion();
			});
		});
	}

	public void modifyEntity(ResourceLocation entity, Consumer<LootTableBuilder> consumer) {
		modify(getEntityResourceLocation(entity), consumer);
	}

	public void buildEntity(Object entities, Consumer<LootTableBuilder> consumer) {
		LootTableBuilder builder = new LootTableBuilder("minecraft:entity");
		consumer.accept(builder);

		ArrayList<EntityType<?>> entityTypes = getFilteredEntityTypes(entities);
		entityTypes.forEach(entityType -> {
			lootTables.put(entityType.getDefaultLootTable(), builder.toJson());
		});
	}
*/
	private ResourceLocation getBlockResourceLocation(ResourceLocation block) {
		return new ResourceLocation(block.getNamespace(), "blocks/" + block.getPath());
	}

	private ResourceLocation getEntityResourceLocation(ResourceLocation entity) {
		return new ResourceLocation(entity.getNamespace(), "entities/" + entity.getPath());
	}

	private ResourceLocation getChestResourceLocation(ResourceLocation chest) {
		return new ResourceLocation(chest.getNamespace(), "chests/" + chest.getPath());
	}

//	private ArrayList<EntityType<?>> getFilteredEntityTypes(Object filters) {
//		ArrayList<EntityType<?>> result = new ArrayList<>();
//		KubeJSRegistries.entityTypes().entrySet().forEach(entry -> {
//			boolean matchedFull = UtilsJS.matchRegex(filters, entry.getKey().location().toString());
//			if (matchedFull) {
//				result.add(entry.getValue());
//			}
//		});
//		return result;
//	}
}
