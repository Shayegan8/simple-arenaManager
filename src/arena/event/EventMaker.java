package arena.event;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import arena.Arena;
import arena.ArenaManager;
import arena.PropertiesAPI;
import arena.STATES;
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

	/**
	 * @apiNote state is better to be BEFOREWAITING
	 * @param makerClass
	 * @param pluginName
	 * @param arenaName
	 * @param status
	 */
	public static void registerWait(Class<?> makerClass, String pluginName, STATES status) {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		ConcurrentLinkedQueue<Object> lnk = new ConcurrentLinkedQueue<>();
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof ArenaWait) {
					ArenaWait ourEvent = (ArenaWait) event;
					Player player = ourEvent.getPlayer();
					lnk.add(player);
					Arena arena = ourEvent.getArena();
					lnk.add(arena);
					new WaitingTimer(innerInstance, player, arena, status,
							PropertiesAPI.getProperty_C("waitEvent", "Arena will be started in {TIME}",
									arena.getName()),
							Integer.parseInt(PropertiesAPI.getProperty_C("joinTimer", "10",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
							.runTaskTimer(innerInstance, 0, 20);

				}
			}
		};

		Player player = null;
		if (lnk.peek() instanceof Player)
			player = (Player) lnk.peek();
		lnk.poll();

		Arena arena = null;
		if (lnk.peek() instanceof Arena)
			arena = (Arena) lnk.peek();

		Bukkit.getPluginManager().registerEvent(new ArenaWait(player, arena).getClass(), instance, EventPriority.NORMAL,
				executor, innerInstance);
	}

	public static void registerJoin(Class<?> makerClass, String pluginName, STATES status) {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		ConcurrentSkipListSet<Object> lsk = new ConcurrentSkipListSet<>();
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof ArenaJoin) {
					ArenaJoin ourEvent = (ArenaJoin) event;
					Player player = ourEvent.getPlayer();
					lsk.add(player);
					Arena arena = ourEvent.getArena();
					lsk.add(arena);
					new StartedTimer(innerInstance, player, arena, status, pluginName,
							Integer.parseInt(PropertiesAPI.getProperty_C("joinEvent", "3",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
							.runTaskTimer(innerInstance, 0, 20);
				}
			}
		};

		Player player = null;
		if (lsk.first() instanceof Player)
			player = (Player) lsk.first();

		Arena arena = null;
		if (lsk.last() instanceof Arena)
			arena = (Arena) lsk.last();
		Bukkit.getPluginManager().registerEvent(new ArenaJoin(player, arena).getClass(), instance, EventPriority.NORMAL,
				executor, innerInstance);

	}

	public static void register(Class<?> makerClass, String pluginName, STATES status) {
		registerWait(makerClass, pluginName, status);
	}

	@Deprecated
	public static void register(Class<?> makerClass, String pluginName) {
		Bukkit.getPluginManager().registerEvents(new EventListener(), Bukkit.getPluginManager().getPlugin(pluginName));

	}

}
