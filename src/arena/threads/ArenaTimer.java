package arena.threads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import arena.ArenaManager;
import arena.Chati;
import arena.event.ArenaEnded;

public class ArenaTimer extends BukkitRunnable {

	private int counter;
	private int time;
	private Player player;
	private String msg;

	public ArenaTimer(Player player, String msg, int time) {
		this.player = player;
		msg.replaceAll("{TIME}", String.valueOf(counter));
		this.msg = Chati.translate(msg);
	}

	@Override
	public void run() {
		time = counter;
		while (counter <= time) {
			if (counter == (int) (counter / 0.5))
				player.sendMessage(msg);
			if (counter == 0)
				Bukkit.getPluginManager()
						.callEvent(new ArenaEnded(player, ArenaManager.getPlayersArena(player.getName())));
			counter--;
		}

	}

	public int getCounter() {
		return counter;
	}

	public int getTime() {
		return time;
	}

}
