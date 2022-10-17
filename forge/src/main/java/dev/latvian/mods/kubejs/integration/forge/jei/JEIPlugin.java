package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
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
 * @author LatvianModder
 *
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

		JEIEvents.HIDE_ITEMS.post(new HideJEIEventJS<>(runtime, VanillaTypes.ITEM_STACK, IngredientJS::of, stack -> !stack.isEmpty()));

		JEIEvents.HIDE_FLUIDS.post(new HideJEIEventJS<>(runtime, ForgeTypes.FLUID_STACK, object -> {
			var fs = FluidStackJS.of(object);
			return fluidStack -> fluidStack.getFluid().isSame(fs.getFluid()) && Objects.equals(fluidStack.getTag(), fs.getNbt());
		}, stack -> !stack.isEmpty()));

		JEIEvents.HIDE_CUSTOM.post(new HideCustomJEIEventJS(runtime));

		JEIEvents.REMOVE_CATEGORIES.post(new RemoveJEICategoriesEvent(runtime));
		JEIEvents.REMOVE_RECIPES.post(new RemoveJEIRecipesEvent(runtime));

		JEIEvents.ADD_ITEMS.post(new AddJEIEventJS<>(runtime, VanillaTypes.ITEM_STACK, ItemStackJS::of, stack -> !stack.isEmpty()));
		JEIEvents.ADD_FLUIDS.post(new AddJEIEventJS<>(runtime, ForgeTypes.FLUID_STACK, object -> fromArchitectury(FluidStackJS.of(object).getFluidStack()), stack -> !stack.isEmpty()));
	}

	public static FluidStack fromArchitectury(dev.architectury.fluid.FluidStack stack) {
		return new FluidStack(stack.getFluid(), (int) stack.getAmount(), stack.getTag());
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		JEIEvents.SUBTYPES.post(new JEISubtypesEventJS(registration));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		JEIEvents.INFORMATION.post(new InformationJEIEventJS(registration));
	}
}