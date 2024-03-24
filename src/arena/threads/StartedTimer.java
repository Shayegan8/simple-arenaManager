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
import arena.event.ArenaEvent;

public class StartedTimer extends BukkitRunnable {

	private int counter;
	private int max;
	private STATES status;
	private Arena arena;
	private CommandSender sender;
	private String msg;

	public StartedTimer(CommandSender sender, Arena arena, STATES status, String msg, int max) {
		this.max = max;
		this.status = status;
		this.arena = arena;
		this.sender = sender;
		msg.replaceAll("{TIMER}", PropertiesAPI.getProperty_C("waitTimer", "10",
				ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
		this.msg = Chati.translate(msg);
	}

	@Override
	public void run() {
		if (sender != null && sender instanceof Player) {
			Bukkit.getPluginManager()
					.callEvent(new ArenaEvent(((Player) sender), ArenaManager.getPlayersTeam(sender.getName())));
			Bukkit.dispatchCommand(sender, "title " + sender.getName() + " title {\"text\":\"" + Chati.translate(msg)
					+ " \",\"fadeIn\":20,\"stay\":40,\"fadeOut\":20}");
			while (counter <= max) {
				if (counter == max) {
					arena.setStatus(status);
					this.cancel();
				}
				counter++;
			}
		}
	}

	public int getCounter() {
		return counter;
	}

	public int getMax() {
		return max;
	}
}
