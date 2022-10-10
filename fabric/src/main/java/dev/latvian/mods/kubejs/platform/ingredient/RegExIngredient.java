package dev.latvian.mods.kubejs.platform.ingredient;

import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class RegExIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<RegExIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(RegExIngredient::new, RegExIngredient::new);

	public final Pattern pattern;

	public RegExIngredient(Pattern pattern) {
		this.pattern = pattern;
	}

	public RegExIngredient(FriendlyByteBuf buf) {
		this(Pattern.compile(buf.readUtf(), buf.readVarInt()));
	}

	public RegExIngredient(JsonObject json) {
		this(UtilsJS.parseRegex(json.get("pattern").getAsString()));
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && pattern.matcher(stack.kjs$getId()).find();
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void toJson(JsonObject json) {
		json.addProperty("regex", pattern.toString());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(pattern.toString());
		buf.writeVarInt(pattern.flags());
	}
}
