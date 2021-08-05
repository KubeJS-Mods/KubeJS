package dev.latvian.kubejs.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSRegistries;
import dev.latvian.kubejs.block.BlockStatePredicate;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LootTableEventJS extends EventJS {
	private static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");
	private static final String TYPE_BLOCK = "minecraft:block";
	private static final String TYPE_ENTITY = "minecraft:entity";
	private static final String TYPE_CHEST = "minecraft:chest";

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
		testSelfBuild(Objects.requireNonNull(builder), resourceLocation);
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
		JsonObject jsonObject = (JsonObject) lootTables.get(tryGetResourceLocation(id));
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

	public LootTableBuilder create(String type) {
		return new LootTableBuilder(type);
	}

	@Nullable
	public LootTableBuilder get(ResourceLocation id) {
		JsonObject jsonObject = (JsonObject) lootTables.get(tryGetResourceLocation(id));
		return jsonObject != null
				? new LootTableBuilder(jsonObject)
				: null;
	}

	public void modify(ResourceLocation id, Consumer<LootTableBuilder> consumer) {
		LootTableBuilder builder = get(id);
		if (builder == null) {
			throw new IllegalArgumentException(String.format("No loot table for '%s' found.", id));
		}

		consumer.accept(builder);

		addIfContains(id, builder.toJson());
	}

	public void merge(ResourceLocation id, Consumer<LootTableBuilder> consumer) {
		LootTableBuilder builder = new LootTableBuilder("");
		consumer.accept(builder);

		modify(id, originBuilder -> {
			originBuilder.merge(builder);
		});
	}

	public void merge(ResourceLocation id, LootTableBuilder builder) {
		merge(id, lootTableBuilder -> {
			lootTableBuilder.merge(builder);
		});
	}

	public void addBlock(Object blocks, Consumer<LootTableBuilder> consumer) {
		LootTableBuilder builder = new LootTableBuilder(TYPE_BLOCK);
		consumer.accept(builder);

		getValidBlockLocations(blocks).forEach(resourceLocation -> {
			if (addIfContains(getBlockResourceLocation(resourceLocation), builder.toJson())) {
				ScriptType.SERVER.console.info(String.format("'%s' was replaced with the new loot table.", resourceLocation));
			}
		});
	}

	public void addSimpleBlock(Object blocks, IngredientJS ingredient) {
		addBlock(blocks, lootTableBuilder -> {
			lootTableBuilder.pool(lootPool -> {
				lootPool.addEntry(ingredient);
			});
		});
	}

	public void addSimpleBlock(Object blocks) {
		getValidBlockLocations(blocks).forEach(resourceLocation -> {
			Item item = KubeJSRegistries.items().get(resourceLocation);
			if (item != null) {
				addSimpleBlock(resourceLocation, IngredientJS.of(resourceLocation));
			}
		});
	}

	public void mergeBlock(Object blocks, Consumer<LootTableBuilder> consumer) {
		LootTableBuilder builder = new LootTableBuilder(TYPE_BLOCK);
		consumer.accept(builder);

		getValidBlockLocations(blocks).forEach(resourceLocation -> {
			modify(getBlockResourceLocation(resourceLocation), originBuilder -> {
				originBuilder.merge(builder);
			});
		});
	}

	public void mergeBlock(Object blocks, LootTableBuilder builder) {
		mergeBlock(blocks, lootTableBuilder -> {
			lootTableBuilder.merge(builder);
		});
	}

	public void addEntity(Object entities, Consumer<LootTableBuilder> consumer) {
		LootTableBuilder builder = new LootTableBuilder(TYPE_ENTITY);
		consumer.accept(builder);

		getValidEntityTypes(entities).forEach(entityType -> {
			lootTables.put(entityType.getDefaultLootTable(), builder.toJson());
		});
	}

	public void mergeEntity(Object entities, Consumer<LootTableBuilder> consumer) {
		LootTableBuilder builder = new LootTableBuilder(TYPE_ENTITY);
		consumer.accept(builder);

		getValidEntityTypes(entities).forEach(entityType -> {
			modify(entityType.getDefaultLootTable(), originBuilder -> {
				originBuilder.merge(builder);
			});
		});
	}

	public void mergeEntity(Object entities, LootTableBuilder builder) {
		mergeEntity(entities, lootTableBuilder -> {
			lootTableBuilder.merge(builder);
		});
	}

	public void addChest(Object chests, Consumer<LootTableBuilder> consumer) {
		Set<ResourceLocation> rlSet = getValidChestLocations(chests);

		LootTableBuilder builder = new LootTableBuilder(TYPE_CHEST);
		consumer.accept(builder);

		rlSet.forEach(resourceLocation -> {
			addIfContains(resourceLocation, builder.toJson());
		});
	}

	public void mergeChest(Object chests, Consumer<LootTableBuilder> consumer) {
		LootTableBuilder builder = new LootTableBuilder(TYPE_CHEST);
		consumer.accept(builder);

		Set<ResourceLocation> rlSet = getValidChestLocations(chests);
		rlSet.forEach(resourceLocation -> {
			modify(resourceLocation, originBuilder -> {
				originBuilder.merge(builder);
			});
		});
	}

	public void mergeChest(Object chests, LootTableBuilder builder) {
		mergeChest(chests, lootTableBuilder -> {
			lootTableBuilder.merge(builder);
		});
	}

	private ResourceLocation tryGetResourceLocation(ResourceLocation rl) {
		if (lootTables.containsKey(rl)) {
			return rl;
		}

		Set<ResourceLocation> locations = new HashSet<>();

		locations.add(rl);
		locations.add(getBlockResourceLocation(rl));
		locations.add(getEntityResourceLocation(rl));
		locations.add(getChestResourceLocation(rl));

		locations = locations.stream().filter(lootTables::containsKey).collect(Collectors.toSet());
		if (locations.size() > 1) {
			throw new RuntimeException(String.format("Multiple tables found for '%s'. Try to be more specific. Found tables with given id: [%s]", rl, locations));
		}

		return locations.stream().findFirst().orElse(AIR_ID);
	}

	private ResourceLocation getBlockResourceLocation(ResourceLocation block) {
		return new ResourceLocation(block.getNamespace(), "blocks/" + block.getPath());
	}

	private ResourceLocation getEntityResourceLocation(ResourceLocation entity) {
		return new ResourceLocation(entity.getNamespace(), "entities/" + entity.getPath());
	}

	private ResourceLocation getChestResourceLocation(ResourceLocation chest) {
		if(chest.getPath().startsWith("chests/") && lootTables.containsKey(chest)) {
			return chest;
		}

		if (chest.getPath().startsWith("village")) {
			return new ResourceLocation(chest.getNamespace(), "chests/village/" + chest.getPath());
		}

		return new ResourceLocation(chest.getNamespace(), "chests/" + chest.getPath());
	}

	private List<EntityType<?>> getValidEntityTypes(Object filters) {
		ArrayList<EntityType<?>> result = new ArrayList<>();
		KubeJSRegistries.entityTypes().entrySet().forEach(entry -> {
			boolean matchedFull = UtilsJS.matchRegex(filters, entry.getKey().location().toString());
			if (matchedFull) {
				result.add(entry.getValue());
			}
		});
		return result;
	}

	private Set<ResourceLocation> getValidChestLocations(Object chests) {
		return ListJS.orSelf(chests)
				.stream()
				.filter(o -> o instanceof CharSequence)
				.map(o -> new ResourceLocation(o.toString()))
				.map(this::getChestResourceLocation)
				.collect(Collectors.toSet());
	}

	private List<ResourceLocation> getValidBlockLocations(Object blocks) {
		return BlockStatePredicate.of(blocks).getBlocks()
				.stream()
				.map(block -> KubeJSRegistries.blocks().getId(block))
				.filter(rl -> rl != null && !rl.equals(AIR_ID))
				.collect(Collectors.toList());
	}
}
