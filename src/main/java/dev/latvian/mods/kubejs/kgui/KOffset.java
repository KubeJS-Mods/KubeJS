package dev.latvian.mods.kubejs.kgui;

public final class KOffset {
	public int l;
	public int r;
	public int t;
	public int b;

	public void setAll(int p) {
		l = p;
		r = p;
		t = p;
		b = p;
	}

	public void setH(int h) {
		l = h;
		r = h;
	}

	public void setV(int v) {
		t = v;
		b = v;
	}
}
