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
	public Object cancel() {
		return super.cancel(item);
	}

	@Override
	public Object success() {
		return super.success(item);
	}

	@Override
	public Object exit() {
		return super.exit(item);
	}

	@Override
	public Object cancel(@Nullable Object value) {
		return super.cancel(ItemStackJS.of(value));
	}

	@Override
	public Object success(@Nullable Object value) {
		return super.success(ItemStackJS.of(value));
	}

	@Override
	public Object exit(@Nullable Object value) {
		return super.exit(ItemStackJS.of(value));
	}
}