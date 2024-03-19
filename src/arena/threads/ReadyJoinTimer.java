package arena.threads;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import arena.Arena;
import arena.ArenaManager;
import arena.STATES;

public class ReadyJoinTimer extends BukkitRunnable {

	private int counter;
	private int max;
	private Plugin instance;
	private STATES status;
	private Arena arena;
	private CommandSender sender;

	public ReadyJoinTimer(Plugin instance, @Nullable CommandSender sender, Arena arena, STATES status, int max) {
		this.instance = instance;
		this.max = max;
		this.status = status;
		this.arena = arena;
		this.sender = sender;
	}

	@Override
	public void run() {
		if (!ArenaManager.isArenaFull(null)) {
			this.cancel();
		} else {
			if (counter == max) {
				ArenaManager.setArenaStatus(sender, instance, arena, status);
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
