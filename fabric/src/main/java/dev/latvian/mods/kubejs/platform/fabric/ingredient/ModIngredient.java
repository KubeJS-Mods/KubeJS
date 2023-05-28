package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<ModIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(KubeJS.id("mod"), ModIngredient::ofModFromJson, ModIngredient::ofModFromNetwork);

	public static ModIngredient ofModFromNetwork(FriendlyByteBuf buf) {
		return new ModIngredient(buf.readUtf());
	}

	public static ModIngredient ofModFromJson(JsonObject json) {
		return new ModIngredient(json.get("mod").getAsString());
	}

	public final String mod;

	public ModIngredient(String mod) {
		this.mod = mod;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && stack.kjs$getMod().equals(mod);
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		var list = new ArrayList<ItemStack>();

		for (var item : KubeJSRegistries.items()) {
			if (item.kjs$getMod().equals(mod)) {
				list.add(item.getDefaultInstance());
			}
		}

		return list;
	}

	@Override
	public KubeJSIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void toJson(JsonObject json) {
		json.addProperty("mod", mod);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(mod);
	}
}
