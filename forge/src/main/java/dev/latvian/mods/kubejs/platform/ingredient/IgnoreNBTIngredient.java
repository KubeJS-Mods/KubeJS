package dev.latvian.mods.kubejs.platform.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class IgnoreNBTIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<IgnoreNBTIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(IgnoreNBTIngredient::new, IgnoreNBTIngredient::new);

	private final Item item;

	public IgnoreNBTIngredient(Item item) {
		this.item = item;
	}

	private IgnoreNBTIngredient(JsonObject json) {
		this(ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.get("item").getAsString())));
	}

	private IgnoreNBTIngredient(FriendlyByteBuf buf) {
		this(buf.readById(Registry.ITEM));
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && !stack.isEmpty() && stack.getItem() == item;
	}

	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public JsonObject toJson() {
		var json = new JsonObject();
		json.addProperty("type", "kubejs:ignore_nbt");
		json.addProperty("item", KubeJSRegistries.items().getId(item).toString());
		return json;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeId(Registry.ITEM, item);
	}
}
