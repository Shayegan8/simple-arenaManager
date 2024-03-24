package arena.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import arena.Arena;
import arena.ArenaManager;
import arena.PropertiesAPI;
import arena.STATES;
import arena.threads.EndedTimer;
import arena.threads.StartedTimer;
import arena.threads.WaitingTimer;

@Deprecated
public class EventListener implements Listener {

	@EventHandler
	public void onWait(ArenaWait event) {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(event.getPluginName());
		Player player = event.getPlayer();
		Arena arena = event.getArena();
		new WaitingTimer(player, arena, event.getStatus(),
				PropertiesAPI.getProperty_C("waitEvent", "Arena will be started in {TIME}",
						ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"),
				Integer.parseInt(PropertiesAPI.getProperty_C("waitCounterTimer", "10",
						ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")),
				Integer.parseInt(PropertiesAPI.getProperty_C("waitTimer", "60",
						ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
				.runTaskTimer(innerInstance, 0, 20);

	}

	@EventHandler
	public void onEnd(ArenaEnded event) {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(event.getPluginName());
		ArenaEnded e = (ArenaEnded) event;
		Arena arena = e.getArena();
		new EndedTimer(e.getPlayer(), arena, STATES.WAITING, event.getPluginName(),
				Integer.parseInt(PropertiesAPI.getProperty_C("endedTimer", "3",
						ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
				.runTaskAsynchronously(innerInstance);
	}

	@EventHandler
	public void onJoin(ArenaJoin event) {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(event.getPluginName());
		Player player = event.getPlayer();
		Arena arena = event.getArena();
		new StartedTimer(player, arena, event.getStatus(),
				PropertiesAPI.getProperty_C("joinEvent", "Something im tired to set it :) {TIME}",
						ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"),
				Integer.parseInt(PropertiesAPI.getProperty_C("joinTimer", "3",
						ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
				.runTaskTimer(innerInstance, 0, 20);
	}

}
