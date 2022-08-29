package dev.latvian.mods.kubejs.platform.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

public class CreativeTabIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<CreativeTabIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(CreativeTabIngredient::new, CreativeTabIngredient::new);

	public final CreativeModeTab tab;

	public CreativeTabIngredient(CreativeModeTab tab) {
		this.tab = tab;
	}

	public CreativeTabIngredient(FriendlyByteBuf buf) {
		this(ItemStackJS.findCreativeTab(buf.readUtf()));
	}

	public CreativeTabIngredient(JsonObject json) {
		this(ItemStackJS.findCreativeTab(json.get("tab").getAsString()));
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && stack.getItem().getItemCategory() == tab;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:creative_tab");
		json.addProperty("tab", tab.getRecipeFolderName());
		return json;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(tab.getRecipeFolderName());
	}
}
