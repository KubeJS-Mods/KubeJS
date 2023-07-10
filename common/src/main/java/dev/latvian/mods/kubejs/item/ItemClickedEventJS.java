package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.entity.RayTraceResultJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemClickedEventJS extends PlayerEventJS {
	private final Player player;
	private final InteractionHand hand;
	private final ItemStack item;
	private RayTraceResultJS target;

	public ItemClickedEventJS(Player player, InteractionHand hand, ItemStack item) {
		this.player = player;
		this.hand = hand;
		this.item = item;
	}

	@Override
	public Player getEntity() {
		return player;
	}

	public InteractionHand getHand() {
		return hand;
	}

	public ItemStack getItem() {
		return item;
	}

	public RayTraceResultJS getTarget() {
		if (target == null) {
			target = player.kjs$rayTrace();
		}

		return target;
	}

	@Override
	@Nullable
	protected Object defaultExitValue() {
		return item;
	}

	@Override
	@Nullable
	protected Object mapExitValue(@Nullable Object value) {
		return ItemStackJS.of(value);
	}
}