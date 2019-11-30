package dev.latvian.kubejs.util.nbt;

import dev.latvian.kubejs.KubeJS;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class NBTCompoundJS extends NBTBaseJS
{
	public static final NBTCompoundJS NULL = new NBTCompoundJS(0)
	{
		@Override
		public int hashCode()
		{
			return 0;
		}

		@Override
		public String toString()
		{
			return "null";
		}

		@Override
		public boolean equals(Object o)
		{
			return o == this;
		}

		@Override
		public boolean isNull()
		{
			return true;
		}

		@Nullable
		@Override
		public CompoundNBT createNBT()
		{
			return null;
		}

		@Override
		public int getSize()
		{
			return 0;
		}

		@Override
		public boolean isEmpty()
		{
			return true;
		}

		@Override
		public NBTCompoundJS getCopy()
		{
			return this;
		}

		@Override
		public NBTBaseJS get(String key)
		{
			return NBTNullJS.INSTANCE;
		}

		@Override
		public NBTBaseJS set(String key, Object value)
		{
			return NBTNullJS.INSTANCE;
		}

		@Override
		public NBTBaseJS remove(String key)
		{
			return NBTNullJS.INSTANCE;
		}

		@Override
		public NBTCompoundJS compoundOrNew(String key)
		{
			return this;
		}

		@Override
		public NBTListJS listOrNew(String key)
		{
			return NBTListJS.NULL;
		}

		@Override
		public NBTCompoundJS set(Map<String, Object> objects)
		{
			return this;
		}
	};

	public static NBTCompoundJS read(File file) throws IOException
	{
		KubeJS.verifyFilePath(file);
		return NBTBaseJS.of(CompressedStreamTools.readCompressed(new FileInputStream(file))).asCompound();
	}

	public static void write(File file, Object nbt) throws IOException
	{
		KubeJS.verifyFilePath(file);
		CompoundNBT n = NBTBaseJS.of(nbt).asCompound().createNBT();

		if (n == null)
		{
			n = new CompoundNBT();
		}

		CompressedStreamTools.writeCompressed(n, new FileOutputStream(file));
	}

	public static Object read(String file) throws IOException
	{
		return read(KubeJS.getGameDirectory().resolve(file).toFile());
	}

	public static void write(String file, Object json) throws IOException
	{
		write(KubeJS.getGameDirectory().resolve(file).toFile(), json);
	}

	private final Map<String, NBTBaseJS> map;

	public NBTCompoundJS(int size)
	{
		map = new LinkedHashMap<>(size);
	}

	public NBTCompoundJS()
	{
		this(3);
	}

	public NBTCompoundJS(CompoundNBT c)
	{
		this(c.size());

		for (String s : c.keySet())
		{
			NBTBaseJS nbt = NBTBaseJS.of(c.get(s));

			if (!nbt.isNull())
			{
				map.put(s, nbt);
			}
		}
	}

	@Override
	public NBTCompoundJS asCompound()
	{
		return this;
	}

	@Override
	@Nullable
	public CompoundNBT createNBT()
	{
		CompoundNBT tagCompound = new CompoundNBT();

		for (Map.Entry<String, NBTBaseJS> entry : map.entrySet())
		{
			INBT base = entry.getValue().createNBT();

			if (base != null)
			{
				tagCompound.put(entry.getKey(), base);
			}
		}

		return tagCompound;
	}

	@Override
	public int hashCode()
	{
		return map.hashCode();
	}

	@Override
	public String toString()
	{
		return map.toString();
	}

	@Override
	public boolean equals(Object o)
	{
		return o == this || o instanceof NBTCompoundJS && map.equals(((NBTCompoundJS) o).map);
	}

	public int getSize()
	{
		return map.size();
	}

	@Override
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	@Override
	public byte getId()
	{
		return Constants.NBT.TAG_COMPOUND;
	}

	@Override
	public NBTCompoundJS getCopy()
	{
		NBTCompoundJS nbt = new NBTCompoundJS(map.size());

		for (Map.Entry<String, NBTBaseJS> entry : map.entrySet())
		{
			nbt.set(entry.getKey(), entry.getValue().getCopy());
		}

		return nbt;
	}

	public NBTBaseJS get(String key)
	{
		NBTBaseJS baseJS = map.get(key);
		return baseJS == null ? NBTNullJS.INSTANCE : baseJS;
	}

	public NBTBaseJS get(String key, int type)
	{
		NBTBaseJS baseJS = get(key);
		return baseJS.getId() != type ? NBTNullJS.INSTANCE : baseJS;
	}

	public NBTBaseJS set(String key, Object value)
	{
		NBTBaseJS prev = map.put(key, NBTBaseJS.of(value));
		return prev == null ? NBTNullJS.INSTANCE : prev;
	}

	public NBTBaseJS remove(String key)
	{
		NBTBaseJS prev = map.remove(key);
		return prev == null ? NBTNullJS.INSTANCE : prev;
	}

	public NBTCompoundJS compoundOrNew(String key)
	{
		NBTCompoundJS nbt = get(key).asCompound();

		if (nbt.isNull())
		{
			nbt = new NBTCompoundJS();
			set(key, nbt);
		}

		return nbt;
	}

	public NBTListJS listOrNew(String key)
	{
		NBTListJS nbt = get(key).asList();

		if (nbt.isNull())
		{
			nbt = new NBTListJS();
			set(key, nbt);
		}

		return nbt;
	}

	public NBTCompoundJS set(Map<String, Object> objects)
	{
		for (Map.Entry<String, Object> entry : objects.entrySet())
		{
			set(entry.getKey(), entry.getValue());
		}

		return this;
	}
}