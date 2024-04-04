package arena.threads;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import arena.ArenaManager;
import arena.STATES;
import scoreboard.Scoresex;

public class SGameRunnable extends BukkitRunnable {

	private Player player;

	public SGameRunnable(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		while (ArenaManager.getPlayersArena(player.getName()).getStatus() != STATES.WAITING)
			Scoresex.gameScores(ArenaManager.getPlayersArena(player.getName()));
	}

}
