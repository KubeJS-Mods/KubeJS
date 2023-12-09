package dev.latvian.mods.kubejs.platform.forge.ingredient;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.platform.forge.IngredientForgeHelper;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class RegExIngredient extends KubeJSIngredient {
	public static final Codec<RegExIngredient> CODEC = ExtraCodecs.stringResolverCodec(Pattern::toString, UtilsJS::parseRegex)
		.fieldOf("regex")
		.codec()
		.xmap(RegExIngredient::new, ingredient -> ingredient.pattern);

	public final Pattern pattern;

	public RegExIngredient(Pattern pattern) {
		super(IngredientForgeHelper.REGEX);
		this.pattern = pattern;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && pattern.matcher(stack.kjs$getId()).find();
	}
}
