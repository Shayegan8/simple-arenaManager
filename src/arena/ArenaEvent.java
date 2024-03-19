package arena;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaEvent extends Event implements Cancellable {

	public HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Arena arena;
	private Player player;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	public ArenaEvent(Arena arena) {
		this.arena = arena;
	}

	public ArenaEvent(Player player) {
		this.player = player;
	}

	public ArenaEvent(Arena arena, Player player) {
		this.player = player;
		this.arena = arena;
	}

	public Arena getArena() {
		return arena;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
}
