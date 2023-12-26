package dev.latvian.mods.kubejs.platform.fabric.ingredient;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegExIngredient extends KubeJSIngredient {

	public static final Codec<RegExIngredient> CODEC = ExtraCodecs.stringResolverCodec(UtilsJS::toRegexString, UtilsJS::parseRegex)
		.fieldOf("pattern")
		.codec()
		.xmap(RegExIngredient::new, ingredient -> ingredient.pattern);

	public static final KubeJSIngredientSerializer<RegExIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(KubeJS.id("regex"), CODEC, RegExIngredient::new);

	public final Pattern pattern;

	public RegExIngredient(Pattern pattern) {
		this.pattern = pattern;
	}

	public RegExIngredient(FriendlyByteBuf buf) {
		this(Pattern.compile(buf.readUtf(), buf.readVarInt()));
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && pattern.matcher(stack.kjs$getId()).find();
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		var list = new ArrayList<ItemStack>();

		for (var item : RegistryInfo.ITEM.getArchitecturyRegistrar()) {
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
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(pattern.toString());
		buf.writeVarInt(pattern.flags());
	}
}
