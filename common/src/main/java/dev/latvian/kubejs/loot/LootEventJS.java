package dev.latvian.kubejs.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.event.EventJS;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public abstract class LootEventJS extends EventJS {
	// static final Gson GSON_CONDITIONS = Deserializers.createConditionSerializer().create();
	// static final Gson GSON_FUNCTIONS = Deserializers.createFunctionSerializer().create();
	// static final Gson GSON_LOOT_TABLES = Deserializers.createLootTableSerializer().create();

	private final Map<ResourceLocation, JsonElement> lootMap;

	public LootEventJS(Map<ResourceLocation, JsonElement> c) {
		lootMap = c;
	}

	public void addJson(ResourceLocation id, JsonObject json) {
		lootMap.put(getDirectory().isEmpty() ? id : new ResourceLocation(id.getNamespace(), getDirectory() + "/" + id.getPath()), json);
	}

	public abstract String getType();

	public abstract String getDirectory();

	LootBuilder createLootBuilder(@Nullable ResourceLocation id, Consumer<LootBuilder> consumer) {
		LootBuilder builder = new LootBuilder(id == null ? null : lootMap.get(id));
		builder.type = getType();
		consumer.accept(builder);
		return builder;
	}

	public void loadExisting(ResourceLocation id, Consumer<LootBuilder> b) {
		LootBuilder builder = createLootBuilder(getDirectory().isEmpty() ? id : new ResourceLocation(id.getNamespace(), getDirectory() + "/" + id.getPath()), b);
		addJson(id, builder.toJson());
	}
}
