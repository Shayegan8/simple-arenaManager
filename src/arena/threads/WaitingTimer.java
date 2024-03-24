package arena.threads;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import arena.Arena;
import arena.ArenaManager;
import arena.Chati;
import arena.PropertiesAPI;
import arena.STATES;

public class WaitingTimer extends BukkitRunnable {

	private int counter;
	private int max;
	private int time;
	private STATES status;
	private Arena arena;
	private CommandSender sender;
	private String msg;

	public WaitingTimer(CommandSender sender, Arena arena, STATES status, String msg, int max, int time) {
		this.max = max;
		this.time = time;
		this.status = status;
		this.arena = arena;
		this.sender = sender;
		msg.replaceAll("{TIME}", PropertiesAPI.getProperty_C("waitCounterTimer", "10",
				ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
		this.msg = Chati.translate(msg);
	}

	@Override
	public void run() {
		if (arena.getMaxPlayer() == arena.getPlayersNames().size())
			operation();

		int i = 0;
		while (i <= time) {
			if (ArenaManager.getArenasPlayers(arena).size() < arena.getMinPlayer()) {
				arena.setStatus(STATES.WAITING);
				this.cancel();
			} else {
				if (time == max) {
					while (counter < max) {
						operation();
					}
				}
			}
			i++;
		}
	}

	private void operation() {
		Bukkit.dispatchCommand(sender, "title " + sender.getName() + " title {\"text\":\""
				+ msg.replaceAll("{TIME}", String.valueOf(counter)) + " \",\"fadeIn\":20,\"stay\":20,\"fadeOut\":20}");
		arena.setStatus(status);
		if (counter == max)
			this.cancel();

		counter++;
	}

	public int getCounter() {
		return counter;
	}

	public int getMax() {
		return max;
	}

}
