package dev.latvian.mods.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class RegExIngredient extends Ingredient {

	public final Pattern pattern;

	public RegExIngredient(Pattern pattern) {
		super(Stream.empty());
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
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "kubejs:regex");
		json.addProperty("regex", pattern.toString());
		return json;
	}
}
