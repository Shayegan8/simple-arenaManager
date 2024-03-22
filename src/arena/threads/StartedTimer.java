package arena.threads;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import arena.Arena;
import arena.ArenaManager;
import arena.Chati;
import arena.STATES;

public class StartedTimer extends BukkitRunnable {

	private int counter;
	private int max;
	private Plugin instance;
	private STATES status;
	private Arena arena;
	private CommandSender sender;
	private String msg;

	public StartedTimer(Plugin instance, @Nullable CommandSender sender, Arena arena, STATES status, String msg,
			int max) {
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
			if (counter == max) {
				ArenaManager.setArenaStatus(sender, instance, arena, status);
				if (sender != null && sender instanceof Player)
					Bukkit.dispatchCommand(sender, "title " + sender.getName() + " title {\"text\":\""
							+ Chati.translate(msg) + " \",\"fadeIn\":20,\"stay\":40,\"fadeOut\":20}");
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
