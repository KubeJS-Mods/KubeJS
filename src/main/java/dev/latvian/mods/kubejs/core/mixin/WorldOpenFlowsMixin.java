package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldOpenFlows.class)
public class WorldOpenFlowsMixin {
	@Inject(method = "createLevelFromExistingSettings", at = @At(
		value = "INVOKE", target = "Lnet/minecraft/server/WorldLoader$PackConfig;createResourceManager()Lcom/mojang/datafixers/util/Pair;",
		shift = At.Shift.AFTER
	))
	private void printServerLogWarning(CallbackInfo ci) {
		ScriptType.SERVER.console.warn("Due to the way Minecraft resource loading works, KubeJS' server.log may not contain everything that happened in your server scripts on initial world creation.");
		ScriptType.SERVER.console.warn("You can still see the full log (including past reloads) in your latest.log file.");
	}
}
