package dev.latvian.kubejs.util;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.item.IIngredientJS;
import dev.latvian.kubejs.item.IngredientListJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.OreDictionaryIngredientJS;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import jdk.nashorn.api.scripting.JSObject;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public enum UtilsJS
{
	INSTANCE;

	public final Random random = new Random();
	public final HashSet<String> internalMethods = new HashSet<>(Arrays.asList("wait", "equals", "toString", "hashCode", "getClass", "notify", "notifyAll"));

	private Map<ID, StatBase> statMap;
	private EntityEquipmentSlot[] equipmentSlots;

	public void init()
	{
		statMap = new HashMap<>(StatList.ALL_STATS.size());

		for (StatBase stat : StatList.ALL_STATS)
		{
			statMap.put(id(stat.statId), stat);
		}

		equipmentSlots = EntityEquipmentSlot.values();
	}

	@SuppressWarnings("unchecked")
	public <T> T cast(Object o)
	{
		return (T) o;
	}

	public <T> List<T> emptyList()
	{
		return Collections.emptyList();
	}

	public <K, V> Map<K, V> emptyMap()
	{
		return Collections.emptyMap();
	}

	public ID id(String namespace, String path)
	{
		return new ID(namespace, path);
	}

	public ID id(Object id)
	{
		return id instanceof ID ? (ID) id : new ID(String.valueOf(id));
	}

	public ResourceLocation idMC(ID id)
	{
		return new ResourceLocation(id.namespace, id.path);
	}

	public LoggerWrapperJS createLogger(String name)
	{
		return new LoggerWrapperJS(LogManager.getLogger(name));
	}

	public String simpleClassName(Class c)
	{
		String s = c.getSimpleName();
		return s.isEmpty() ? c.getName().substring(c.getName().lastIndexOf('.') + 1) : s;
	}

	public FieldJS field(String className, String fieldName)
	{
		try
		{
			return new FieldJS(ReflectionHelper.findField(Class.forName(className), fieldName));
		}
		catch (Throwable ex)
		{
			return new FieldJS(null);
		}
	}

	public int parseInt(@Nullable Object object, int def)
	{
		if (object instanceof Number)
		{
			return ((Number) object).intValue();
		}

		try
		{
			return Integer.parseInt(String.valueOf(object));
		}
		catch (Exception ex)
		{
			return def;
		}
	}

	public double parseDouble(@Nullable Object object, double def)
	{
		if (object instanceof Number)
		{
			return ((Number) object).doubleValue();
		}

		try
		{
			return Double.parseDouble(String.valueOf(object));
		}
		catch (Exception ex)
		{
			return def;
		}
	}

	public Pattern regex(String pattern)
	{
		return Pattern.compile(pattern);
	}

	public Pattern regex(String pattern, int flags)
	{
		return Pattern.compile(pattern, flags);
	}

	@Nullable
	public SoundEvent sound(Object id)
	{
		return ForgeRegistries.SOUND_EVENTS.getValue(idMC(id(id)));
	}

	public List<String> listFieldsAndMethods(Class clazz, int flags, String... exclude)
	{
		List<String> list = new ObjectArrayList<>();
		StringBuilder builder = new StringBuilder();
		Set<String> excludeSet = new ObjectOpenHashSet<>(Arrays.asList(exclude));

		for (Field field : clazz.getFields())
		{
			if (excludeSet.contains(field.getName()))
			{
				continue;
			}

			if ((field.getModifiers() & Modifier.PUBLIC) != 0)
			{
				if ((flags & 1) == 0)
				{
					String m = Modifier.toString(field.getModifiers() & ~Modifier.PUBLIC);
					builder.append(m);

					if (!m.isEmpty())
					{
						builder.append(' ');
					}
				}

				if ((flags & 2) == 0)
				{
					builder.append(simpleClassName(field.getType()));
					builder.append(' ');
				}

				builder.append(field.getName());

				list.add(builder.toString());
				builder.setLength(0);
			}
		}

		for (Method method : clazz.getMethods())
		{
			if (internalMethods.contains(method.getName()) || excludeSet.contains(method.getName() + "()"))
			{
				continue;
			}

			if ((method.getModifiers() & Modifier.PUBLIC) != 0)
			{
				if ((flags & 1) == 0)
				{
					String m = Modifier.toString(method.getModifiers() & ~Modifier.PUBLIC);
					builder.append(m);

					if (!m.isEmpty())
					{
						builder.append(' ');
					}
				}

				if ((flags & 2) == 0)
				{
					builder.append(simpleClassName(method.getReturnType()));
					builder.append(' ');
				}

				builder.append(method.getName());
				builder.append('(');

				boolean first = true;

				for (Class c : method.getParameterTypes())
				{
					if (first)
					{
						first = false;
					}
					else
					{
						builder.append(',');
						builder.append(' ');
					}

					builder.append(simpleClassName(c));
				}

				builder.append(')');

				list.add(builder.toString());
				builder.setLength(0);
			}
		}

		return list;
	}

	public List<String> listFieldsAndMethods(@Nullable Object object, int flags, String... exclude)
	{
		if (object == null)
		{
			return emptyList();
		}

		return listFieldsAndMethods(object.getClass(), flags, exclude);
	}

	@Nullable
	public NBTTagCompound nbt(@Nullable Object o)
	{
		if (o == null)
		{
			return null;
		}
		else if (o instanceof NBTTagCompound)
		{
			return (NBTTagCompound) o;
		}
		else if (o instanceof Map)
		{
			try
			{
				return JsonToNBT.getTagFromJson(JsonUtilsJS.INSTANCE.from(o).toString());
			}
			catch (Exception ex)
			{
				return null;
			}
		}

		try
		{
			return JsonToNBT.getTagFromJson(String.valueOf(o));
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	public ItemStackJS item(@Nullable Object o)
	{
		if (o == null)
		{
			return ItemStackJS.EMPTY;
		}
		else if (o instanceof ItemStackJS)
		{
			return (ItemStackJS) o;
		}
		else if (o instanceof ItemStack)
		{
			ItemStack stack = (ItemStack) o;
			return stack.isEmpty() ? ItemStackJS.EMPTY : new ItemStackJS.Bound(stack);
		}

		String s0 = String.valueOf(o).trim();

		if (s0.isEmpty() || s0.equals("air"))
		{
			return ItemStackJS.EMPTY;
		}

		if (s0.startsWith("{") && s0.endsWith("}"))
		{
			try
			{
				ItemStack stack = new ItemStack(JsonToNBT.getTagFromJson(s0));

				if (!stack.isEmpty())
				{
					return new ItemStackJS.Bound(stack);
				}
			}
			catch (Exception ex)
			{
				return ItemStackJS.EMPTY;
			}
		}

		String[] s = s0.split("\\s", 4);
		ResourceLocation id = new ResourceLocation(KubeJS.appendModId(s[0]));

		Item item = Item.REGISTRY.getObject(id);

		if (item != null && item != Items.AIR)
		{
			ItemStackJS stack = new ItemStackJS.Unbound(item);

			if (s.length >= 2)
			{
				stack.count(Integer.parseInt(s[1]));
			}

			if (s.length >= 3)
			{
				stack.data(Integer.parseInt(s[2]));
			}

			if (s.length >= 4)
			{
				stack.nbt(s[3]);
			}

			return stack;
		}

		return ItemStackJS.EMPTY;
	}

	public IIngredientJS ingredient(@Nullable Object object)
	{
		if (object instanceof String)
		{
			if (object.toString().startsWith("ore:"))
			{
				return new OreDictionaryIngredientJS(object.toString().substring(4));
			}

			return item(KubeJS.appendModId(object.toString()));
		}
		else if (object instanceof JSObject)
		{
			JSObject js = (JSObject) object;

			if (js.isArray())
			{
				IngredientListJS list = new IngredientListJS();

				for (String key : js.keySet())
				{
					IIngredientJS ingredient = ingredient(js.getMember(key));

					if (ingredient != ItemStackJS.EMPTY)
					{
						list.ingredients.add(ingredient);
					}
				}

				return list.ingredients.isEmpty() ? ItemStackJS.EMPTY : list;
			}
		}

		return item(object);
	}

	@Nullable
	public StatBase stat(@Nullable Object id)
	{
		if (id == null)
		{
			return null;
		}
		else if (id instanceof StatBase)
		{
			return (StatBase) id;
		}

		return statMap.get(id(id));
	}

	public EntityEquipmentSlot equipmentSlot(int id)
	{
		return equipmentSlots[id];
	}
}