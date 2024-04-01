package arena.event;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import arena.Arena;
import arena.ArenaManager;
import arena.ArenaTeam;
import arena.Chati;
import arena.PlayerData;
import arena.PropertiesAPI;
import arena.STATES;
import arena.threads.ArenaTimer;
import arena.threads.DeathTimer;
import arena.threads.EndedTimer;
import arena.threads.StartedTimer;
import arena.threads.WaitingTimer;
import inventory.Armory;
import inventory.EMaterial;
import npc.NPC;
import scoreboard.Scoresex;

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

	@EventHandler
	public void registerNPC(ArenaEvent e, EntityDamageEvent e1, PlayerInteractEntityEvent e2, InventoryClickEvent e3) {
		Player player = e.getPlayer();
		ArenaTeam team = ArenaManager.getPlayersTeam(player.getName());
		Arena arena = team.getArena();
		Entity npcE = e1.getEntity();

		Optional<Entry<ArenaTeam, NPC>> opt = ArenaManager.SNPCS.entrySet().stream()
				.filter((x) -> x.getKey().equals(ArenaManager.getPlayersTeam(player.getName()))
						&& x.getValue().getName().equals(npcE.getCustomName())
						&& npcE.getWorld().equals(Bukkit.getWorld(arena.getWorld())))
				.findFirst();

		if (opt.isPresent()) {
			e1.setCancelled(true);
			if (e2.getRightClicked().getType().equals(opt.get().getValue().getType())) {
				Player nPlayer = e2.getPlayer();
				Optional<Inventory> op = ArenaManager.INVS.stream().filter((x) -> x.getName().equals(arena.getName()))
						.findFirst();
				if (opt.isPresent())
					nPlayer.openInventory(op.get());
				Optional<EMaterial> mot = ArenaManager.ITEMS.stream().filter((x) -> x.getIndex() == e3.getSlot())
						.findFirst();
				if (mot.isPresent()) {
					switch (mot.get().getPage()) {

					case "armory.dcnf":
						nPlayer.openInventory(new Armory(arena.getName()).getInv());
						break;

					case "potions.dcnf":
						break;

					default:
						Bukkit.getLogger().severe(mot.get().getPage() + " not found in " + arena.getName());
						nPlayer.getOpenInventory().close();
					}
				}
			}
		}
	}

	public static void registerBreak() {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof BlockBreakEvent) {
					BlockBreakEvent e = (BlockBreakEvent) event;
					Player player = e.getPlayer();
					Arena arena = ArenaManager.getPlayersArena(player.getName());
					if (arena.getStatus() == STATES.RUNNING)
						arena.getBreakedBlocks().add(e.getBlock());
				}
			}
		};

		Bukkit.getPluginManager().registerEvent(BlockBreakEvent.class, instance, EventPriority.NORMAL, executor,
				innerInstance);
	}

	public static void registerPlaced() {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof BlockPlaceEvent) {
					BlockPlaceEvent e = (BlockPlaceEvent) event;
					Player player = e.getPlayer();
					Arena arena = ArenaManager.getPlayersArena(player.getName());
					if (arena.getStatus() == STATES.RUNNING)
						arena.getPlacedBlocks().add(e.getBlock());
				}
			}
		};

		Bukkit.getPluginManager().registerEvent(BlockBreakEvent.class, instance, EventPriority.NORMAL, executor,
				innerInstance);
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
					Scoresex.gameScores(arena);
					new ArenaTimer(player,
							PropertiesAPI.getProperty("endedEventHalf", "&cGAME ENDED",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"),
							Integer.parseInt(PropertiesAPI.getProperty("arenaTime", "1800",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")));
					if (!ArenaManager.isEntityOnRegion(arena, player.getLocation())) {
						Location location = player.getLocation();
						player.teleport(
								location.clone().subtract(player.getLocation().getDirection().normalize().multiply(5)));
					}
					if (player.getLocation().getBlockY() < 0) {
						if (!ArenaManager.isBlockGone(ArenaManager.getPlayersTeam(player.getName()))) {
							new DeathTimer(player, arena, STATES.DEAD,
									PropertiesAPI.getProperty("deathEvent", "Respawn in {TIME}",
											ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"),
									Integer.parseInt(PropertiesAPI.getProperty("deathTimer", "5",
											ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")));
						} else {
							player.setGameMode(GameMode.SPECTATOR);
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
									"title " + player.getName() + " title {\"text\":\"" + Chati.translate("&cYou lost")
											+ " \",\"fadeIn\":20,\"stay\":60,\"fadeOut\":20}");
						}
					}
					Bukkit.getPluginManager().callEvent(e); // well...
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
					Player player = e.getPlayer();
					e.getArena().getPlayersNames().stream().forEach((x) -> {
						Bukkit.getPlayer(x)
								.sendMessage(Chati
										.translate(PropertiesAPI.getProperty("joinEvent",
												"{PLAYER} joined to the arena", ArenaManager.DIR + "messages.dcnf"))
										.replaceAll("{PLAYER}", e.getPlayer().getName()));
						Arena arena = e.getArena();
						if (arena.getStatus() == STATES.WAITING) {
							if (PropertiesAPI
									.getProperty("joinitem", "true",
											ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")
									.equals("true")) {
								ItemStack item = new ItemStack(
										Material.valueOf(PropertiesAPI.getProperty("item", "CHEST",
												ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")),
										1);
								ItemMeta meta = item.getItemMeta();
								meta.setDisplayName(
										Chati.translate(PropertiesAPI.getProperty("itemname", "&c&LSELECT TEAM",
												ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")));
								meta.setLore(Arrays.asList(""));
								player.getInventory().setItem(
										Integer.parseInt(PropertiesAPI.getProperty("itemslot", "40",
												ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")),
										item);
							}
							ArenaManager.setPlayerStatus(x, STATES.WAITING);
						}
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
												.translate(PropertiesAPI.getProperty("leftEvent",
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
					new EndedTimer(e.getPlayer(), arena, STATES.BEFOREENDED,
							PropertiesAPI.getProperty("endedEvent", "&cGAME ENDED",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"),
							Integer.parseInt(PropertiesAPI.getProperty("endedTimer", "3",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
							.runTaskAsynchronously(innerInstance);
				}
			}
		};

		Bukkit.getPluginManager().registerEvent(ArenaEnded.class, instance, EventPriority.NORMAL, executor,
				innerInstance);
	}

	public static void registerBEnd() {
		Plugin innerInstance = Bukkit.getPluginManager().getPlugin(pluginName);
		EventExecutor executor = new EventExecutor() {

			@Override
			public void execute(Listener listener, Event event) throws EventException {
				if (event instanceof ArenaBEnded) {
					ArenaBEnded e = (ArenaBEnded) event;
					Arena arena = e.getArena();
					ArenaManager.regenerateBreakedBlocks(arena);
					ArenaManager.deletePlacedBlocks(arena);
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
							PropertiesAPI.getProperty("waitEvent", "Arena will be started in {TIME}",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"),
							Integer.parseInt(PropertiesAPI.getProperty("waitCounterTimer", "10",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")),
							Integer.parseInt(PropertiesAPI.getProperty("waitTimer", "60",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")))
							.runTaskTimerAsynchronously(innerInstance, 0, 20);
					ArenaManager.getPlayersArena(e.getPlayer().getName());
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
				if (event instanceof ArenaStarted) {
					ArenaStarted e = (ArenaStarted) event;
					Player player = e.getPlayer();
					Arena arena = e.getArena();

					StartedTimer timer = new StartedTimer(player, arena, status,
							PropertiesAPI.getProperty("joinEvent", "&cSTARTED",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"),
							Integer.parseInt(PropertiesAPI.getProperty("joinTImer", "3",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")));
					timer.runTaskTimerAsynchronously(innerInstance, 0, 20);

					PlayerData data = ArenaManager.getPlayersData(player);
					data.setStartedTimer(timer);
					ArenaManager.putInPLAYERS(player.getName(), data);
					if (PropertiesAPI
							.getProperty("startitems", "true",
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf")
							.equals("true")) {
						List<EMaterial> items = ArenaManager.ITEMS.stream()
								.filter((x) -> x.isStartItem() && x.getArenaName().equals(arena.getName()))
								.collect(Collectors.toList());
						items.forEach((x) -> {
							ItemStack item = new ItemStack(x.getMaterial(), x.getAmount());
							ItemMeta meta = item.getItemMeta();
							meta.setDisplayName(x.getName());
							player.getInventory().setItem(x.getIndex(), item);
						});
					}
				}
			}
		};

		Bukkit.getPluginManager().registerEvent(ArenaJoin.class, instance, EventPriority.NORMAL, executor,
				innerInstance);
	}

	public static void register(String pluginName, STATES status1, STATES status2) {
		registerArena();
		registerBEnd();
		registerBreak();
		registerEnd();
		registerJoin();
		registerLeft();
		registerPlaced();
		registerStarted(status1);
		registerWait(status2);
		Bukkit.getPluginManager().registerEvents(instance, Bukkit.getPluginManager().getPlugin(pluginName));
	}

	@Deprecated
	public static void register(String pluginName) {
		Bukkit.getPluginManager().registerEvents(new EventListener(), Bukkit.getPluginManager().getPlugin(pluginName));

	}

}
