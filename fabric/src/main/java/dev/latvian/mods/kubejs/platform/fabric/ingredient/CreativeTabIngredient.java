package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreativeTabIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<CreativeTabIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(KubeJS.id("creative_tab"), CreativeTabIngredient::new, CreativeTabIngredient::new);

	public final CreativeModeTab tab;

	public CreativeTabIngredient(CreativeModeTab tab) {
		this.tab = tab;
	}

	public CreativeTabIngredient(FriendlyByteBuf buf) {
		this(UtilsJS.findCreativeTab(buf.readUtf()));
	}

	public CreativeTabIngredient(JsonObject json) {
		this(UtilsJS.findCreativeTab(json.get("tab").getAsString()));
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && stack.getItem().getItemCategory() == tab;
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		NonNullList<ItemStack> list = NonNullList.create();
		tab.fillItemList(list);
		return list;
	}

	@Override
	public KubeJSIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void toJson(JsonObject json) {
		json.addProperty("tab", tab.getRecipeFolderName());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(tab.getRecipeFolderName());
	}
}
