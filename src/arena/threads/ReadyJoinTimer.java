package arena.threads;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import arena.Arena;
import arena.ArenaManager;
import arena.Chati;
import arena.STATES;

public class ReadyJoinTimer extends BukkitRunnable {

	private int counter;
	private int max;
	private Plugin instance;
	private STATES status;
	private Arena arena;
	private CommandSender sender;
	private String msg;

	public ReadyJoinTimer(Plugin instance, CommandSender sender, Arena arena, STATES status, String msg, int max) {
		this.instance = instance;
		this.max = max;
		this.status = status;
		this.arena = arena;
		this.sender = sender;
		this.msg = msg;
	}

	@Override
	public void run() {
		if (!ArenaManager.isArenaFull(null)) {
			this.cancel();
		} else {
			Bukkit.dispatchCommand(sender,
					"title " + sender.getName() + " title {\"text\":\""
							+ msg.replaceAll("{TIME}", String.valueOf(counter))
							+ " \",\"fadeIn\":20,\"stay\":40,\"fadeOut\":20}");
			if (counter == max) {
				ArenaManager.setArenaStatus(sender, instance, arena, status);
				this.cancel();
			}
		}
		counter++;
	}

	public int getCounter() {
		return counter;
	}

	public int getMax() {
		return max;
	}

	public Plugin getInstance() {
		return instance;
	}

}
