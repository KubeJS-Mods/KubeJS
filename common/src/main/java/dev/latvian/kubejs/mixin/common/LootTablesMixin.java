package dev.latvian.kubejs.mixin.common;

import com.google.gson.JsonElement;
import dev.latvian.kubejs.core.LootTablesKJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author LatvianModder
 */
@Mixin(LootTables.class)
public abstract class LootTablesMixin implements LootTablesKJS
{
	@Redirect(method = "apply", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V", ordinal = 0))
	private void applyKJS(Map<ResourceLocation, JsonElement> map, BiConsumer<ResourceLocation, JsonElement> action)
	{
		applyKJS0(map, action);
	}
}
