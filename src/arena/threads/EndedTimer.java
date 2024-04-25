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
import arena.event.ArenaBEnded;

public class EndedTimer extends BukkitRunnable {

	private int counter;
	private int max;
	private Arena arena;
	private CommandSender sender;
	private String msg;

	public EndedTimer(CommandSender sender, Arena arena, String msg, int max) {
		this.max = max;
		this.arena = arena;
		this.sender = sender;
		msg.replaceAll("\\{TIME\\}", PropertiesAPI.getProperty("endedTimer", "3",
				ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
		this.msg = Chati.translate(msg);
	}

	@Override
	public void run() {
		Bukkit.dispatchCommand(sender, "title " + sender.getName() + " title {\"text\":\""
				+ msg.replaceAll("\\{TIME\\}", String.valueOf(counter)) + " \",\"fadeIn\":20,\"stay\":60,\"fadeOut\":20}");
		while (counter <= max) {
			if (counter == max) {
				arena.getPlayersNames().stream().forEach((x) -> {
					ArenaManager.removePlayer(x, arena);
				});
				arena.setStatus(STATES.BEFOREENDED);
				Bukkit.getPluginManager().callEvent(new ArenaBEnded(((Player) sender), arena));
				this.cancel();
			}
			counter++;
		}
	}

	public int getCounter() {
		return counter;
	}

	public int getMax() {
		return max;
	}
}
