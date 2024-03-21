package arena.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import arena.Arena;
import arena.ArenaTeam;

public class ArenaStarted extends Event implements Cancellable {

	private HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Player player;
	private Arena arena;

	public Player getPlayer() {
		return player;
	}

	public Arena getArena() {
		return arena;
	}

	public ArenaTeam getTeam() {
		return team;
	}

	private ArenaTeam team;

	public ArenaStarted(Player player) {
		this.player = player;
	}

	public ArenaStarted(Player player, Arena arena) {
		this.player = player;
		this.arena = arena;
	}

	public ArenaStarted(Player player, ArenaTeam team) {
		this.player = player;
		this.team = team;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
