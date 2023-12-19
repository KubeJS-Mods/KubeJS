package dev.latvian.mods.kubejs.platform.forge.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class RegExIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<RegExIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(RegExIngredient::new, RegExIngredient::new);

	public final Pattern pattern;

	public RegExIngredient(Pattern pattern) {
		if (pattern == null) throw new IllegalArgumentException("Pattern for a RegExIngredient cannot be null! Check your pattern format");
		this.pattern = pattern;
	}

	public RegExIngredient(FriendlyByteBuf buf) {
		this(Pattern.compile(buf.readUtf(), buf.readVarInt()));
	}

	public RegExIngredient(JsonObject json) {
		this(UtilsJS.parseRegex(json.get("pattern").getAsString()));
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && pattern.matcher(stack.kjs$getId()).find();
	}

	@Override
	public void toJson(JsonObject json) {
		json.addProperty("pattern", UtilsJS.toRegexString(pattern));
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(pattern.toString());
		buf.writeVarInt(pattern.flags());
	}
}
