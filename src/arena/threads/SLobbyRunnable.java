package arena.threads;

import org.bukkit.scheduler.BukkitRunnable;

import scoreboard.Scoresex;

public class SLobbyRunnable extends BukkitRunnable {

	@Override
	public void run() {
		Scoresex.lobbyScores();
	}

}
