package dev.latvian.mods.kubejs.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.kubejs.core.DiggerItemKJS;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DiggerItem.class)
public class DiggerItemMixin implements DiggerItemKJS {
	@Unique
	private TagKey<Block> kjs$mineableTag;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void init(CallbackInfo ci, @Local(argsOnly = true) TagKey<Block> blocks) {
		this.kjs$mineableTag = blocks;
	}

	@Override
	public TagKey<Block> kjs$getMineableTag() {
		return kjs$mineableTag;
	}
}
