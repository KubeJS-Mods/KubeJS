package dev.latvian.mods.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface FunctionContainer {
	FunctionContainer addFunction(JsonObject o);

	default FunctionContainer addConditionalFunction(Consumer<ConditionalFunction> func) {
		var conditionalFunction = new ConditionalFunction();
		func.accept(conditionalFunction);

		if (conditionalFunction.function != null) {
			conditionalFunction.function.add("conditions", conditionalFunction.conditions);
			return addFunction(conditionalFunction.function);
		}

		return this;
	}

	default FunctionContainer count(NumberProvider count) {
		var o = new JsonObject();
		o.addProperty("function", "minecraft:set_count");
		o.add("count", UtilsJS.numberProviderJson(count));
		return addFunction(o);
	}

	default FunctionContainer enchantWithLevels(NumberProvider levels, boolean treasure) {
		var o = new JsonObject();
		o.addProperty("function", "minecraft:enchant_with_levels");
		o.add("levels", UtilsJS.numberProviderJson(levels));
		o.addProperty("treasure", treasure);
		return addFunction(o);
	}

	default FunctionContainer enchantRandomly(ResourceLocation[] enchantments) {
		var o = new JsonObject();
		o.addProperty("function", "minecraft:enchant_randomly");

		var a = new JsonArray();

		for (var r : enchantments) {
			a.add(r.toString());
		}

		o.add("enchantments", a);
		return addFunction(o);
	}

	default FunctionContainer nbt(CompoundTag tag) {
		var o = new JsonObject();
		o.addProperty("function", "minecraft:set_nbt");
		o.addProperty("tag", tag.toString());
		return addFunction(o);
	}

	default FunctionContainer furnaceSmelt() {
		var o = new JsonObject();
		o.addProperty("function", "minecraft:furnace_smelt");
		return addFunction(o);
	}

	default FunctionContainer lootingEnchant(NumberProvider count, int limit) {
		var o = new JsonObject();
		o.addProperty("function", "minecraft:looting_enchant");
		o.add("count", UtilsJS.numberProviderJson(count));
		o.addProperty("limit", limit);
		return addFunction(o);
	}

	default FunctionContainer damage(NumberProvider damage) {
		var o = new JsonObject();
		o.addProperty("function", "minecraft:set_damage");
		o.add("damage", UtilsJS.numberProviderJson(damage));
		return addFunction(o);
	}

	// set_attributes

	default FunctionContainer name(Component name, @Nullable LootContext.EntityTarget entity) {
		var o = new JsonObject();
		o.addProperty("function", "minecraft:set_name");
		o.add("name", Component.Serializer.toJsonTree(name));

		if (entity != null) {
			o.addProperty("entity", entity.name);
		}

		return addFunction(o);
	}

	default FunctionContainer name(Component name) {
		return name(name, null);
	}

	// exploration_map
	// set_stew_effect

	default FunctionContainer copyName(CopyNameFunction.NameSource source) {
		var o = new JsonObject();
		o.addProperty("function", "minecraft:copy_name");
		o.addProperty("source", source.name);
		return addFunction(o);
	}

	// set_contents
	// limit_count
	// apply_bonus

	default FunctionContainer lootTable(ResourceLocation table, long seed) {
		var o = new JsonObject();
		o.addProperty("function", "minecraft:set_loot_table");
		o.addProperty("name", table.toString());

		if (seed != 0L) {
			o.addProperty("seed", seed);
		}

		return addFunction(o);
	}

	// explosion_decay
	// set_lore
	// fill_player_head
	// copy_nbt
	// copy_state
}
