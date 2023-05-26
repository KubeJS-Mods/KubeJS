package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;

/**
 * TODO: make a common JEI plugin if the need arises
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin {
	public static final ResourceLocation ID = new ResourceLocation(KubeJS.MOD_ID, "jei");
	public IJeiRuntime runtime;

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime r) {
		runtime = r;
		BuiltinKubeJSPlugin.GLOBAL.put("jeiRuntime", runtime);

		if (JEIEvents.HIDE_ITEMS.hasListeners()) {
			JEIEvents.HIDE_ITEMS.post(ScriptType.CLIENT, new HideJEIEventJS<>(runtime, VanillaTypes.ITEM_STACK, IngredientJS::of, stack -> !stack.isEmpty()));
		}

		if (JEIEvents.HIDE_FLUIDS.hasListeners()) {
			JEIEvents.HIDE_FLUIDS.post(ScriptType.CLIENT, new HideJEIEventJS<>(runtime, ForgeTypes.FLUID_STACK, object -> {
				var fs = FluidStackJS.of(object);
				return fluidStack -> fluidStack.getFluid().isSame(fs.getFluid()) && Objects.equals(fluidStack.getTag(), fs.getNbt());
			}, stack -> !stack.isEmpty()));
		}

		if (JEIEvents.HIDE_CUSTOM.hasListeners()) {
			JEIEvents.HIDE_CUSTOM.post(ScriptType.CLIENT, new HideCustomJEIEventJS(runtime));
		}

		if (JEIEvents.REMOVE_CATEGORIES.hasListeners()) {
			JEIEvents.REMOVE_CATEGORIES.post(ScriptType.CLIENT, new RemoveJEICategoriesEvent(runtime));
		}

		if (JEIEvents.REMOVE_RECIPES.hasListeners()) {
			JEIEvents.REMOVE_RECIPES.post(ScriptType.CLIENT, new RemoveJEIRecipesEvent(runtime));
		}

		if (JEIEvents.ADD_ITEMS.hasListeners()) {
			JEIEvents.ADD_ITEMS.post(ScriptType.CLIENT, new AddJEIEventJS<>(runtime, VanillaTypes.ITEM_STACK, ItemStackJS::of, stack -> !stack.isEmpty()));
		}

		if (JEIEvents.ADD_FLUIDS.hasListeners()) {
			JEIEvents.ADD_FLUIDS.post(ScriptType.CLIENT, new AddJEIEventJS<>(runtime, ForgeTypes.FLUID_STACK, object -> fromArchitectury(FluidStackJS.of(object).getFluidStack()), stack -> !stack.isEmpty()));
		}
	}

	public static FluidStack fromArchitectury(dev.architectury.fluid.FluidStack stack) {
		return new FluidStack(stack.getFluid(), (int) stack.getAmount(), stack.getTag());
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		if (JEIEvents.SUBTYPES.hasListeners()) {
			JEIEvents.SUBTYPES.post(ScriptType.CLIENT, new JEISubtypesEventJS(registration));
		}
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (JEIEvents.INFORMATION.hasListeners()) {
			JEIEvents.INFORMATION.post(ScriptType.CLIENT, new InformationJEIEventJS(registration));
		}
	}
}