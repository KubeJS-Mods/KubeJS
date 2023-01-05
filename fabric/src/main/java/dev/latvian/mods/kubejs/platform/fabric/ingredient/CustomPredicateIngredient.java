package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CustomPredicateIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<CustomPredicateIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(KubeJS.id("custom_predicate"), CustomPredicateIngredient::new, CustomPredicateIngredient::new);

	private final Ingredient parent;
	private final UUID uuid;
	private final boolean isServer;

	public CustomPredicateIngredient(Ingredient parent, UUID uuid) {
		this.parent = parent;
		this.uuid = uuid;
		this.isServer = true;
	}

	private CustomPredicateIngredient(JsonObject json) {
		parent = IngredientJS.ofJson(json.get("parent"));
		uuid = UUID.fromString(json.get("uuid").getAsString());
		isServer = false;
	}

	private CustomPredicateIngredient(FriendlyByteBuf buf) {
		parent = IngredientJS.ofNetwork(buf);
		uuid = buf.readUUID();
		isServer = false;
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		return Arrays.asList(parent.getItems());
	}

	@Override
	public boolean test(@Nullable ItemStack target) {
		if (isServer && target != null && parent.test(target) && RecipesEventJS.customIngredientMap != null) {
			var i = RecipesEventJS.customIngredientMap.get(uuid);
			return i != null && i.predicate.test(target);
		}

		return false;
	}

	@Override
	public KubeJSIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void toJson(JsonObject json) {
		json.add("parent", parent.toJson());
		json.addProperty("uuid", uuid.toString());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		parent.toNetwork(buf);
		buf.writeUUID(uuid);
	}
}
