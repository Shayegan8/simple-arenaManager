package arena.threads;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import arena.Arena;
import arena.ArenaManager;
import arena.Chati;
import arena.PropertiesAPI;
import arena.STATES;

public class DeathTimer extends BukkitRunnable {

	private int counter;
	private int max;
	private STATES status;
	private Arena arena;
	private CommandSender sender;
	private String msg;

	public DeathTimer(CommandSender sender, Arena arena, STATES status, String msg, int max) {
		this.max = max;
		this.status = status;
		this.arena = arena;
		this.sender = sender;
		msg.replaceAll("{TIME}", PropertiesAPI.getProperty_C("deathTimer", "5",
				ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
		this.msg = Chati.translate(msg);
	}

	@Override
	public void run() {
		if (sender != null && sender instanceof Player) {
			((Player) sender).setGameMode(GameMode.SPECTATOR);
			Bukkit.dispatchCommand(sender, "title " + sender.getName() + " title {\"text\":\"" + Chati.translate(msg)
					+ " \",\"fadeIn\":20,\"stay\":20,\"fadeOut\":20}");
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
