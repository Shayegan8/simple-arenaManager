package arena.threads;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
	private Arena arena;
	private CommandSender sender;
	private String msg;

	public WaitingTimer(CommandSender sender, Arena arena, String msg, int max, int time) {
		this.max = max;
		this.time = time;
		this.arena = arena;
		this.sender = sender;
		msg.replaceAll("{TIME}", PropertiesAPI.getProperty("waitCounterTimer", "10",
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
				arena.setStatus(STATES.BEFOREWAITING);
				while (counter < max) {
					operation();
				}
				if (time == max) {
					Player player = (Player) sender;
					if (ArenaManager.getPlayersTeam(player.getName()) == null)
						ArenaManager.randomSelectTeam(arena, player.getName());					
				}
			}
			i++;
		}
	}

	private void operation() {
		Bukkit.dispatchCommand(sender, "title " + sender.getName() + " title {\"text\":\""
				+ msg.replaceAll("{TIME}", String.valueOf(counter)) + " \",\"fadeIn\":20,\"stay\":20,\"fadeOut\":20}");
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
