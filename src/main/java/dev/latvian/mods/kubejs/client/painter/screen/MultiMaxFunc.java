package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitVariables;

import java.util.List;

public class MultiMaxFunc extends Unit {
	public final List<Unit> units;

	public MultiMaxFunc(List<Unit> units) {
		this.units = units;
	}

	@Override
	public double get(UnitVariables variables) {
		double d = 0D;

		for (Unit unit : units) {
			d = Math.max(d, unit.get(variables));
		}

		return d;
	}
}
