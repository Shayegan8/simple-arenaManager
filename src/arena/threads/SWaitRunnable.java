package arena.threads;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import arena.ArenaManager;
import arena.STATES;
import scoreboard.Scoresex;

public class SWaitRunnable extends BukkitRunnable {

	private Player player;

	public SWaitRunnable(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		while (ArenaManager.getPlayersArena(player.getName()).getStatus() == STATES.WAITING
				|| ArenaManager.getPlayersArena(player.getName()).getStatus() == STATES.BEFOREWAITING)
			Scoresex.waitingScores(ArenaManager.getPlayersArena(player.getName()));
	}

}
