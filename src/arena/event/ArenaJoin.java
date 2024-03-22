package arena.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import arena.Arena;
import arena.ArenaTeam;
import arena.STATES;

public class ArenaJoin extends Event implements Cancellable {

	private HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Player player;
	private Arena arena;
	private STATES status;
	private String pluginName;

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

	public ArenaJoin(Player player) {
		this.player = player;
	}

	public ArenaJoin(Player player, Arena arena) {
		this.player = player;
		this.arena = arena;
	}

	public ArenaJoin(Player player, ArenaTeam team) {
		this.player = player;
		this.team = team;
		this.arena = team.getArena();
	}

	public ArenaJoin(Player player, ArenaTeam team, STATES status) {
		this.player = player;
		this.team = team;
		this.arena = team.getArena();
		this.status = status;
	}

	public ArenaJoin(Player player, ArenaTeam team, STATES status, String pluginName) {
		this.player = player;
		this.team = team;
		this.arena = team.getArena();
		this.status = status;
		this.pluginName = pluginName;
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

	public STATES getStatus() {
		return status;
	}

	public String getPluginName() {
		return pluginName;
	}

}