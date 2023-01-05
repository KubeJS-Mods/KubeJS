package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegExIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<RegExIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(KubeJS.id("regex"), RegExIngredient::new, RegExIngredient::new);

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
	public List<ItemStack> getMatchingStacks() {
		var list = new ArrayList<ItemStack>();

		for (var item : KubeJSRegistries.items()) {
			if (pattern.matcher(item.kjs$getId()).find()) {
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
		json.addProperty("regex", pattern.toString());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(pattern.toString());
		buf.writeVarInt(pattern.flags());
	}
}
