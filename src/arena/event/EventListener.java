package arena.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import arena.ArenaManager;
import arena.PropertiesAPI;
import arena.threads.WaitingTimer;

@Deprecated
public class EventListener implements Listener {

	@EventHandler
	public void onWait(ArenaWait event) {
		ArenaWait ourEvent = (ArenaWait) event;
		String arenaName = event.getArena().getName();
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(event.getPluginName());
		Player player = ourEvent.getPlayer();
		if (ArenaManager.isArenaFull(ArenaManager.getArenaByName(arenaName))) {
			ArenaManager.ARENALIST.stream()
					.filter((x) -> x.equals(ArenaManager.getPlayersArena(player, innerInstance, player.getName())));
			ArenaManager.setArenaStatus(ourEvent.getPlayer(), innerInstance, null, null);
		}
		new WaitingTimer(Bukkit.getPluginManager().getPlugin(event.getPluginName()), player,
				ArenaManager.getPlayersArena(player, innerInstance, player.getName()), event.getStatus(),
				PropertiesAPI.getProperty_C("joinEvent", "Arena will be started in {TIME}", arenaName),
				Integer.parseInt(PropertiesAPI.getProperty_C("joinTimer", "10",
						ArenaManager.DIR + arenaName + "/" + arenaName + ".dcnf")))
				.runTaskTimer(innerInstance, 0, 20);

	}

}
