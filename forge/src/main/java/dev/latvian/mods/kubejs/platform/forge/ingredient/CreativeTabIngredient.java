package dev.latvian.mods.kubejs.platform.forge.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.registries.BuiltInRegistries;
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
		this(buf.readById(BuiltInRegistries.CREATIVE_MODE_TAB));
	}

	public CreativeTabIngredient(JsonObject json) {
		this(UtilsJS.findCreativeTab(json.get("tab").getAsString()));
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && tab.contains(stack);
	}

	public void toJson(JsonObject json) {
		json.addProperty("tab", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab).toString());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeId(BuiltInRegistries.CREATIVE_MODE_TAB, tab);
	}
}
