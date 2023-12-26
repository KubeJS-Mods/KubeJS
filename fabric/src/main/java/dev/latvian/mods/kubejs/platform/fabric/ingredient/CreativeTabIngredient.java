package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CreativeTabIngredient extends KubeJSIngredient {

	public static final Codec<CreativeTabIngredient> CODEC = BuiltInRegistries.CREATIVE_MODE_TAB.byNameCodec()
		.fieldOf("tab")
		.codec()
		.xmap(CreativeTabIngredient::new, ingredient -> ingredient.tab);

	public static final KubeJSIngredientSerializer<CreativeTabIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(KubeJS.id("creative_tab"), CODEC, CreativeTabIngredient::new);

	public final CreativeModeTab tab;

	public CreativeTabIngredient(CreativeModeTab tab) {
		this.tab = tab;
	}

	public CreativeTabIngredient(FriendlyByteBuf buf) {
		this(buf.readById(BuiltInRegistries.CREATIVE_MODE_TAB));
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && tab.contains(stack);
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		return new ArrayList<>(tab.getSearchTabDisplayItems());
	}

	@Override
	public KubeJSIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeId(BuiltInRegistries.CREATIVE_MODE_TAB, tab);
	}
}
