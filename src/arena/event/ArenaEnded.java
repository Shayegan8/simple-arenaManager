package arena.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import arena.Arena;
import arena.ArenaManager;
import arena.ArenaTeam;

public class ArenaEnded extends Event implements Cancellable {

	private HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private String pluginName;
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

	public ArenaEnded(Player player) {
		this.player = player;
		this.arena = ArenaManager.getPlayersArena(player.getName());
		this.team = ArenaManager.getPlayersTeam(player.getName());
	}

	public ArenaEnded(Player player, Arena arena) {
		this.player = player;
		this.arena = arena;
		this.team = ArenaManager.getPlayersTeam(player.getName());
	}

	public ArenaEnded(Player player, ArenaTeam team) {
		this.player = player;
		this.team = team;
		this.arena = team.getArena();
	}

	public ArenaEnded(String pluginName, Player player, ArenaTeam team) {
		this.player = player;
		this.team = team;
		this.arena = team.getArena();
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

	public String getPluginName() {
		return pluginName;
	}

}
