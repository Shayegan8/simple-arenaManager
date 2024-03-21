package arena.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import arena.ArenaManager;
import arena.PropertiesAPI;
import arena.STATES;
import arena.threads.ReadyJoinTimer;

public class EventMaker implements Listener {

	private static EventMaker instance;

	{
		if (instance == null) {
			try {
				instance = EventMaker.class.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static void registerJoin(Class<?> makerClass, String pluginName, String arenaName, STATES status) {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof ArenaJoin) {
					ArenaJoin ourEvent = (ArenaJoin) event;
					Player player = ourEvent.getPlayer();
					if (ArenaManager.isArenaFull(ArenaManager.getArenaByName(arenaName))) {
						ArenaManager.ARENALIST.stream().filter(
								(x) -> x.equals(ArenaManager.getPlayersArena(player, innerInstance, player.getName())));
						ArenaManager.setArenaStatus(ourEvent.getPlayer(), innerInstance, null, null);
					}
					new ReadyJoinTimer(Bukkit.getPluginManager().getPlugin(pluginName), player,
							ArenaManager.getPlayersArena(player, innerInstance, player.getName()), status,
							Integer.parseInt(PropertiesAPI.getProperty_C("joinTimer", "10",
									ArenaManager.DIR + arenaName + "/" + arenaName + ".dcnf")))
							.runTaskTimer(innerInstance, 0, 20);

				}
			}
		};

		Bukkit.getPluginManager().registerEvent(new PlayerJoinEvent(null, null).getClass(), instance,
				EventPriority.NORMAL, executor, innerInstance);
	}

	public static void register(Class<?> makerClass, String pluginName) {
		Bukkit.getPluginManager().registerEvents(instance, Bukkit.getPluginManager().getPlugin(pluginName));

	}

}
