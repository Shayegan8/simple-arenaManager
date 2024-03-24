package arena.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import arena.Arena;
import arena.ArenaManager;
import arena.PropertiesAPI;
import arena.STATES;
import arena.threads.EndedTimer;
import arena.threads.StartedTimer;
import arena.threads.WaitingTimer;

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

	public static void registerEnd(String pluginName) {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof ArenaEnded) {
					ArenaEnded e = (ArenaEnded) event;
					Arena arena = e.getArena();
					new EndedTimer(e.getPlayer(), arena, STATES.WAITING, pluginName,
							Integer.parseInt(PropertiesAPI.getProperty_C("endedTimer", "3",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
							.runTaskAsynchronously(innerInstance);
					ArenaManager.ARENAS.entries().stream().filter((x) -> x.getKey().equals(arena)).skip(0)
							.forEach((x) -> {
								ArenaManager.removePlayer(x.getValue(), arena);
							});
				}
			}
		};

		try {
			Bukkit.getPluginManager().registerEvent(ArenaEnded.class.newInstance().getClass(), instance,
					EventPriority.NORMAL, executor, innerInstance);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @apiNote state is better to be BEFOREWAITING
	 * @param makerClass
	 * @param pluginName
	 * @param arenaName
	 * @param status
	 */
	public static void registerWait(String pluginName, STATES status) {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof ArenaWait) {
					ArenaWait e = (ArenaWait) event;
					Arena arena = e.getArena();
					new WaitingTimer(e.getPlayer(), arena, status,
							PropertiesAPI.getProperty_C("waitEvent", "Arena will be started in {TIME}",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"),
							Integer.parseInt(PropertiesAPI.getProperty_C("waitCounterTimer", "10",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")),
							Integer.parseInt(PropertiesAPI.getProperty_C("waitTimer", "60",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
							.runTaskTimer(innerInstance, 0, 20);
					ArenaManager.getPlayersArena(pluginName);
				}
			}
		};

		try {
			Bukkit.getPluginManager().registerEvent(ArenaWait.class.newInstance().getClass(), instance,
					EventPriority.NORMAL, executor, innerInstance);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void registerJoin(String pluginName, STATES status) {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof ArenaJoin) {
					ArenaJoin e = (ArenaJoin) event;
					Player player = e.getPlayer();
					Arena arena = e.getArena();
					new StartedTimer(player, arena, status,
							PropertiesAPI.getProperty_C("joinEvent", "&cSTARTED",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"),
							Integer.parseInt(PropertiesAPI.getProperty_C("joinTImer", "3",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
							.runTaskTimer(innerInstance, 0, 20);
				}
			}
		};

		try {
			Bukkit.getPluginManager().registerEvent(ArenaJoin.class.newInstance().getClass(), instance,
					EventPriority.NORMAL, executor, innerInstance);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	public static void register(String pluginName, STATES status) {
		registerWait(pluginName, status);
	}

	@Deprecated
	public static void register(String pluginName) {
		Bukkit.getPluginManager().registerEvents(new EventListener(), Bukkit.getPluginManager().getPlugin(pluginName));

	}

}
