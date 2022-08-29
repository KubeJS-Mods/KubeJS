package dev.latvian.mods.kubejs.platform.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CustomPredicateIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<CustomPredicateIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(CustomPredicateIngredient::new, CustomPredicateIngredient::new);

	private final Ingredient parent;
	private final UUID uuid;
	private final boolean isServer;

	public CustomPredicateIngredient(Ingredient parent, UUID uuid) {
		this.parent = parent;
		this.uuid = uuid;
		this.isServer = true;
	}

	private CustomPredicateIngredient(JsonObject json) {
		parent = CraftingHelper.getIngredient(json.get("parent"));
		uuid = UUID.fromString(json.get("uuid").getAsString());
		isServer = false;
	}

	private CustomPredicateIngredient(FriendlyByteBuf buf) {
		parent = Ingredient.fromNetwork(buf);
		uuid = buf.readUUID();
		isServer = false;
	}

	@Override
	public ItemStack[] getItems() {
		return parent.getItems();
	}

	@Override
	@NotNull
	public IntList getStackingIds() {
		return parent.getStackingIds();
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
	public boolean isSimple() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return parent.isEmpty();
	}

	@Override
	public IIngredientSerializer<CustomPredicateIngredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public JsonElement toJson() {
		var json = new JsonObject();
		json.addProperty("type", "kubejs:custom_predicate");
		json.add("parent", parent.toJson());
		json.addProperty("uuid", uuid.toString());
		return json;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		parent.toNetwork(buf);
		buf.writeUUID(uuid);
	}
}
