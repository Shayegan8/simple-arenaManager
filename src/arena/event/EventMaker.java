package arena.event;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import arena.Arena;
import arena.ArenaManager;
import arena.Chati;
import arena.PropertiesAPI;
import arena.STATES;
import arena.threads.DeathTimer;
import arena.threads.EndedTimer;
import arena.threads.StartedTimer;
import arena.threads.WaitingTimer;

public class EventMaker implements Listener {

	private static EventMaker instance;

	{
		Bukkit.getPluginManager().registerEvents(instance, Bukkit.getPluginManager().getPlugin(pluginName));
		if (instance == null) {
			try {
				instance = EventMaker.class.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private static String pluginName;

	/**
	 * @apiNote YOU HAVE TO USE THIS FUNCTION FIRST
	 * @param pluginName
	 */
	public static void setPlugin(String pluginName) {
		EventMaker.pluginName = pluginName;
	}

	public static void registerArena() {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof ArenaEvent) {
					ArenaEvent e = (ArenaEvent) event;
					Player player = e.getPlayer();
					Arena arena = e.getArena();
					if (!ArenaManager.isEntityOnRegion(arena, player.getLocation())) {
						Location location = player.getLocation();
						player.teleport(
								location.clone().subtract(player.getLocation().getDirection().normalize().multiply(5)));
					}
					if (player.getLocation().getBlockY() < 0) {
						if (!ArenaManager.isBlockGone(ArenaManager.getPlayersTeam(player.getName()))) {
							new DeathTimer(player, arena, STATES.DEAD,
									PropertiesAPI.getProperty_C("deathEvent", "Respawn in {TIME}",
											ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"),
									Integer.parseInt(PropertiesAPI.getProperty_C("deathTimer", "5",
											ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")));
						} else {
							player.setGameMode(GameMode.SPECTATOR);
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
									"title " + player.getName() + " title {\"text\":\"" + Chati.translate("&cYou lost")
											+ " \",\"fadeIn\":20,\"stay\":60,\"fadeOut\":20}");
						}
					}
				}
			}
		};

		Bukkit.getPluginManager().registerEvent(ArenaEvent.class, instance, EventPriority.NORMAL, executor,
				innerInstance);

	}

	public static void registerJoin() {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof ArenaJoin) {
					ArenaJoin e = (ArenaJoin) event;
					e.getArena().getPlayersNames().stream().forEach((x) -> {
						e.getPlayer()
								.sendMessage(Chati
										.translate(PropertiesAPI.getProperty_C("joinEvent",
												"{PLAYER} joined to the arena", ArenaManager.DIR + "messages.dcnf"))
										.replaceAll("{PLAYER}", e.getPlayer().getName()));
						ArenaManager.setPlayerStatus(x, STATES.WAITING);
					});
				}
			}
		};

		Bukkit.getPluginManager().registerEvent(ArenaJoin.class, instance, EventPriority.NORMAL, executor,
				innerInstance);
	}

	public static void registerLeft() {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof ArenaJoin) {
					ArenaJoin e = (ArenaJoin) event;
					e.getArena().getPlayersNames().stream().filter((x) -> x.equals(e.getPlayer().getName()))
							.forEach((x) -> {
								e.getPlayer()
										.sendMessage(Chati
												.translate(PropertiesAPI.getProperty_C("leftEvent",
														"{PLAYER} left the arena", ArenaManager.DIR + "messages.dcnf"))
												.replaceAll("{PLAYER}", e.getPlayer().getName()));
								ArenaManager.setPlayerStatus(x, STATES.NONE);
							});
				}
			}
		};

		Bukkit.getPluginManager().registerEvent(ArenaJoin.class, instance, EventPriority.NORMAL, executor,
				innerInstance);
	}

	public static void registerEnd() {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof ArenaEnded) {
					ArenaEnded e = (ArenaEnded) event;
					Arena arena = e.getArena();
					new EndedTimer(e.getPlayer(), arena, STATES.WAITING,
							PropertiesAPI.getProperty_C("endedEvent", "&cGAME ENDED",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"),
							Integer.parseInt(PropertiesAPI.getProperty_C("endedTimer", "3",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
							.runTaskAsynchronously(innerInstance);
				}
			}
		};

		Bukkit.getPluginManager().registerEvent(ArenaEnded.class, instance, EventPriority.NORMAL, executor,
				innerInstance);
	}

	/**
	 * @apiNote state is better to be BEFOREWAITING
	 * @param status
	 */
	public static void registerWait(STATES status) {
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

		Bukkit.getPluginManager().registerEvent(ArenaWait.class, instance, EventPriority.NORMAL, executor,
				innerInstance);
	}

	/**
	 * 
	 * @param status
	 */
	public static void registerStarted(STATES status) {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof ArenaJoin) {
					ArenaStarted e = (ArenaStarted) event;
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

		Bukkit.getPluginManager().registerEvent(ArenaJoin.class, instance, EventPriority.NORMAL, executor,
				innerInstance);

	}

	public static void register(String pluginName, STATES status1, STATES status2) {
		registerWait(status1);
		registerStarted(status2);
		registerEnd();
	}

	@Deprecated
	public static void register(String pluginName) {
		Bukkit.getPluginManager().registerEvents(new EventListener(), Bukkit.getPluginManager().getPlugin(pluginName));

	}

}
