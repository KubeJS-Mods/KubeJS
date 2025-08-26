package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonElement;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;

public class RegistryOpsContainer extends OpsContainer {
	private final RegistryOps<Tag> regNbt;
	private final RegistryOps<JsonElement> regJson;
	private final RegistryOps<Object> regJava;

	public RegistryOpsContainer(RegistryOps<Tag> nbt, RegistryOps<JsonElement> json, RegistryOps<Object> java) {
		super(nbt, json, java);
		this.regNbt = nbt;
		this.regJson = json;
		this.regJava = java;
	}

	@Override
	public RegistryOps<Tag> nbt() {
		return regNbt;
	}

	@Override
	public RegistryOps<JsonElement> json() {
		return regJson;
	}

	@Override
	public RegistryOps<Object> java() {
		return regJava;
	}
}
