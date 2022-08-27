package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class CreativeTabIngredient extends Ingredient {
	public final CreativeModeTab tab;

	public CreativeTabIngredient(CreativeModeTab tab) {
		super(Stream.empty());
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
		return stack != null && stack.kjs$getCreativeTab().equals(tab.getRecipeFolderName());
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:tab");
		json.addProperty("tab", tab.getRecipeFolderName());
		return json;
	}
}
