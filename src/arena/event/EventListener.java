package arena.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import arena.Arena;
import arena.ArenaManager;
import arena.Chati;
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
		new WaitingTimer(player, arena, STATES.WAITING,
				PropertiesAPI.getProperty("waitEvent", "Arena will be started in {TIME}",
						ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"),
				Integer.parseInt(PropertiesAPI.getProperty("waitCounterTimer", "10",
						ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")),
				Integer.parseInt(PropertiesAPI.getProperty("waitTimer", "60",
						ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
				.runTaskTimer(innerInstance, 0, 20);

	}

	@EventHandler
	public void onEnd(ArenaEnded event) {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(event.getPluginName());
		ArenaEnded e = (ArenaEnded) event;
		Arena arena = e.getArena();
		new EndedTimer(e.getPlayer(), arena, STATES.WAITING, event.getPluginName(),
				Integer.parseInt(PropertiesAPI.getProperty("endedTimer", "3",
						ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
				.runTaskAsynchronously(innerInstance);
	}

	@EventHandler
	public void onStart(ArenaStarted e) {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(e.getPluginName());
		Player player = e.getPlayer();
		Arena arena = e.getArena();
		new StartedTimer(player, arena, STATES.RUNNING,
				PropertiesAPI.getProperty("joinEvent", "&cSTARTED",
						ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"),
				Integer.parseInt(PropertiesAPI.getProperty("joinTImer", "3",
						ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
				.runTaskTimer(innerInstance, 0, 20);
	}

	@EventHandler
	public void onLeft(ArenaLeft e) {
		e.getArena().getPlayersNames().stream().filter((x) -> x.equals(e.getPlayer().getName())).forEach((x) -> {
			e.getPlayer()
					.sendMessage(Chati
							.translate(PropertiesAPI.getProperty("leftEvent", "{PLAYER} left the arena",
									ArenaManager.DIR + "messages.dcnf"))
							.replaceAll("{PLAYER}", e.getPlayer().getName()));
			ArenaManager.setPlayerStatus(x, STATES.NONE);
		});
	}

}
