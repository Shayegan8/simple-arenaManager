package arena.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import arena.Arena;
import arena.ArenaManager;
import arena.ArenaTeam;
import arena.STATES;

public class ArenaWait extends Event implements Cancellable {

	private static HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Player player;
	private Arena arena;
	private String pluginName;
	private STATES status;

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

	public ArenaWait(Player player) {
		this.player = player;
	}

	public ArenaWait(Player player, Arena arena) {
		this.player = player;
		this.arena = arena;
		this.team = ArenaManager.getPlayersTeam(player.getName());
	}

	public ArenaWait(Player player, ArenaTeam team) {
		this.player = player;
		this.team = team;
		this.arena = ArenaManager.getPlayersArena(player.getName());
	}

	public ArenaWait(String pluginName, Player player, ArenaTeam team) {
		this.player = player;
		this.team = team;
		this.arena = team.getArena();
		this.pluginName = pluginName;
	}

	public ArenaWait(Player player, ArenaTeam team, STATES status, String pluginName) {
		this.player = player;
		this.team = team;
		this.arena = team.getArena();
		this.pluginName = pluginName;
		this.status = status;
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

	public String getPluginName() {
		return pluginName;
	}

	public STATES getStatus() {
		return status;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
