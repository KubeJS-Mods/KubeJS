package dev.latvian.kubejs.integration.jei;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.bindings.DefaultBindings;
import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.script.ScriptType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin
{
	public IJeiRuntime runtime;

	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation(KubeJS.MOD_ID, "jei");
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime r)
	{
		runtime = r;
		DefaultBindings.GLOBAL.put("jeiRuntime", runtime);

		new HideJEIEventJS<>(runtime, VanillaTypes.ITEM, object -> {
			List<ItemStack> list = new ArrayList<>();

			for (ItemStackJS stack : IngredientJS.of(object).getStacks())
			{
				list.add(stack.getItemStack());
			}

			return list;
		}).post(ScriptType.CLIENT, JEIIntegration.JEI_HIDE_ITEMS);

		new HideJEIEventJS<>(runtime, VanillaTypes.FLUID, object -> {
			FluidStackJS fs = FluidStackJS.of(object);
			return fs.isEmpty() ? Collections.emptyList() : Collections.singletonList(fs.getFluidStack());
		}).post(ScriptType.CLIENT, JEIIntegration.JEI_HIDE_FLUIDS);

		new AddJEIEventJS<>(runtime, VanillaTypes.ITEM, object -> ItemStackJS.of(object).getItemStack()).post(ScriptType.CLIENT, JEIIntegration.JEI_ADD_ITEMS);
		new AddJEIEventJS<>(runtime, VanillaTypes.FLUID, object -> FluidStackJS.of(object).getFluidStack()).post(ScriptType.CLIENT, JEIIntegration.JEI_ADD_FLUIDS);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration)
	{
		new AddJEISubtypesEventJS(registration).post(ScriptType.CLIENT, JEIIntegration.JEI_SUBTYPES);
	}
}