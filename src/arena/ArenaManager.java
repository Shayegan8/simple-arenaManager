package arena;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import net.minecraft.server.v1_8_R3.EntityPlayer;

enum STATES {
	STARTED, ENDED, WAITING, INPROCESS, RUNNING
}

enum TEAMS {
	RED, BLUE, GREEN, YELLOW, ORANGE, BLACK, PINK, PURPLE, BROWN, WHITE
}

/**
 * @author shayegan8
 */
public class ArenaManager {

	private static void ARENASException(CompletableFuture<Void> future, @Nullable CommandSender sender, Arena key,
			String value, boolean checkKey, boolean checkValue) {
		future.handle((reuslt, exp) -> {
			if (checkKey == true)
				if (key == null)
					if (sender != null)
						sender.sendMessage("Key can't be NULL");
					else
						throw new IllegalStateException("key can't be NULL " + Arrays.toString(exp.getStackTrace()));
			if (checkValue == true)
				if (value == null)
					if (sender != null)
						sender.sendMessage("Value can't be NULL");
					else
						throw new IllegalStateException("key can't be NULL " + Arrays.toString(exp.getStackTrace()));
			throw new IllegalStateException(Arrays.toString(exp.getStackTrace()));
		});
	}

	/**
	 * @apiNote this stores arenas players and status, the status is on first index
	 */
	public final static ListMultimap<Arena, String> ARENAS = ListMultimapBuilder.hashKeys().arrayListValues().build();

	public static void putInARENAS(@Nullable CommandSender sender, JavaPlugin instance, Arena key, String value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				ARENAS.put(key, value);
			});
		}, Executors.newSingleThreadExecutor());

		ARENASException(future, sender, key, value, true, true);
	}

	public static void putInARENAS(Arena key, String value) {
		ARENAS.put(key, value);
	}

	public static void setInARENAS(int index, @Nullable CommandSender sender, JavaPlugin instance, Arena key,
			String value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				ARENAS.get(key).set(index, value);
			});
		}, Executors.newSingleThreadExecutor());

		ARENASException(future, sender, key, value, true, true);
	}

	public static void setInARENAS(int index, Arena key, String value) {
		ARENAS.get(key).set(index, value);
	}

	public static void removeFromARENAS(@Nullable CommandSender sender, JavaPlugin instance, Arena key, String value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				ARENAS.remove(key, value);
			});
		});
		ARENASException(future, sender, key, value, true, true);
	}

	public static void removeFromARENAS(Arena key, String value) {
		ARENAS.remove(key, value);
	}

	public static void ARENASRemoveAll(@Nullable CommandSender sender, JavaPlugin instance, Arena key) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				ARENAS.removeAll(key);
			});
		}, Executors.newSingleThreadExecutor());

		ARENASException(future, sender, key, null, true, false);
	}

	public static void ARENASRemoveAll(Arena key) {
		ARENAS.removeAll(key);
	}

	/**
	 * @apiNote this stores players values, first is arena name, second is their
	 *          status and last one is their team
	 */
	public final static Map<String, PlayerData> PLAYERS = new MapMaker().weakKeys().weakValues().makeMap();

	private static void PLAYERSException(CompletableFuture<Void> future, @Nullable CommandSender sender, String key,
			PlayerData value, boolean checkKey, boolean checkValue) {
		future.handle((reuslt, exp) -> {
			if (checkKey == true)
				if (key == null)
					if (sender != null)
						sender.sendMessage("Key can't be NULL");
					else
						throw new IllegalStateException("key can't be NULL " + Arrays.toString(exp.getStackTrace()));
			if (checkValue == true)
				if (value == null)
					if (sender != null)
						sender.sendMessage("Value can't be NULL");
					else
						throw new IllegalStateException("key can't be NULL " + Arrays.toString(exp.getStackTrace()));
			throw new IllegalStateException(Arrays.toString(exp.getStackTrace()));
		});
	}

	public static void putInPLAYERS(@Nullable CommandSender sender, JavaPlugin instance, String key, PlayerData value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				PLAYERS.put(key, value);
			});

		}, Executors.newSingleThreadExecutor());

		PLAYERSException(future, sender, key, value, true, true);
	}

	public static void putInPLAYERS(String key, PlayerData value) {
		PLAYERS.put(key, value);
	}

	public static void removeFromPLAYERS(@Nullable CommandSender sender, JavaPlugin instance, String key,
			PlayerData value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				PLAYERS.remove(key, value);
			});
		}, Executors.newSingleThreadExecutor());
		PLAYERSException(future, sender, key, value, true, true);
	}

	public static void removeFromPLAYERS(String key, PlayerData value) {
		PLAYERS.remove(key, value);
	}

	public static void PLAYERSRemoveAll(@Nullable CommandSender sender, JavaPlugin instance, String key) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				PLAYERS.remove(key);
			});
		}, Executors.newSingleThreadExecutor());

		PLAYERSException(future, sender, key, null, true, false);
	}

	public static void PLAYERSRemoveAll(String key) {
		PLAYERS.remove(key);
	}

	/**
	 * @apiNote this stores npcs by their arenaName
	 */
	public final static ListMultimap<String, NPC> NPCS = MultimapBuilder.hashKeys().arrayListValues().build();

	private static void NPCSException(CompletableFuture<Void> future, @Nullable CommandSender sender, String key,
			NPC value, boolean checkKey, boolean checkValue) {
		future.handle((reuslt, exp) -> {
			if (checkKey == true)
				if (key == null)
					if (sender != null)
						sender.sendMessage("Key can't be NULL");
					else
						throw new IllegalStateException("key can't be NULL " + Arrays.toString(exp.getStackTrace()));
			if (checkValue == true)
				if (value == null)
					if (sender != null)
						sender.sendMessage("Value can't be NULL");
					else
						throw new IllegalStateException("key can't be NULL " + Arrays.toString(exp.getStackTrace()));
			throw new IllegalStateException(Arrays.toString(exp.getStackTrace()));
		});
	}

	public static void putInNPCS(@Nullable CommandSender sender, JavaPlugin instance, String key, NPC value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				NPCS.put(key, value);
			});
		}, Executors.newSingleThreadExecutor());

		NPCSException(future, sender, key, value, true, true);
	}

	public static void putInNPCS(String key, NPC value) {
		NPCS.put(key, value);
	}

	public static void setInNPCS(int index, @Nullable CommandSender sender, JavaPlugin instance, String key,
			NPC value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				NPCS.get(key).set(index, value);
			});

		}, Executors.newSingleThreadExecutor());

		NPCSException(future, sender, key, value, true, true);
	}

	public static void setInNPCS(int index, String key, NPC value) {
		NPCS.get(key).set(index, value);
	}

	public static void removeFromNPCS(@Nullable CommandSender sender, JavaPlugin instance, String key, NPC value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				NPCS.remove(key, value);
			});
		}, Executors.newSingleThreadExecutor());

		NPCSException(future, sender, key, value, true, true);
	}

	public static void removeFromNPCS(String key, NPC value) {
		NPCS.remove(key, value);
	}

	public static void NPCSRemoveAll(@Nullable CommandSender sender, JavaPlugin instance, String key) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				NPCS.removeAll(key);
			});
		}, Executors.newSingleThreadExecutor());

		NPCSException(future, sender, key, null, true, false);
	}

	public static void NPCSRemoveAll(String key) {
		NPCS.removeAll(key);
	}

	/**
	 * @apiNote this stores generators by their arenaName
	 */
	public final static ListMultimap<String, Location> GENERATORS = ListMultimapBuilder.hashKeys().arrayListValues()
			.build();

	private static void GENSException(CompletableFuture<Void> future, @Nullable CommandSender sender, String key,
			Location value, boolean checkKey, boolean checkValue) {
		future.handle((reuslt, exp) -> {
			if (checkKey == true)
				if (key == null)
					if (sender != null)
						sender.sendMessage("Key can't be NULL");
					else
						throw new IllegalStateException("key can't be NULL " + Arrays.toString(exp.getStackTrace()));
			if (checkValue == true)
				if (value == null)
					if (sender != null)
						sender.sendMessage("Value can't be NULL");
					else
						throw new IllegalStateException("key can't be NULL " + Arrays.toString(exp.getStackTrace()));
			throw new IllegalStateException(Arrays.toString(exp.getStackTrace()));
		});
	}

	public static void putInGENS(@Nullable CommandSender sender, JavaPlugin instance, String key, Location value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				GENERATORS.put(key, value);
			});
		}, Executors.newSingleThreadExecutor());

		GENSException(future, sender, key, value, true, true);
	}

	public static void putInGENS(String key, Location value) {
		GENERATORS.put(key, value);
	}

	public static void setInGENS(int index, @Nullable CommandSender sender, JavaPlugin instance, String key,
			Location value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				GENERATORS.get(key).set(index, value);
			});
		}, Executors.newSingleThreadExecutor());

		GENSException(future, sender, key, value, true, true);
	}

	public static void setInGENS(int index, String key, Location value) {
		GENERATORS.get(key).set(index, value);
	}

	public static void removeFromGENS(@Nullable CommandSender sender, JavaPlugin instance, String key, Location value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				NPCS.remove(key, value);
			});
		}, Executors.newSingleThreadExecutor());

		GENSException(future, sender, key, value, true, true);
	}

	public static void removeFromGENS(String key, Location value) {
		NPCS.remove(key, value);
	}

	public static void GENSRemoveAll(@Nullable CommandSender sender, JavaPlugin instance, String key) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				GENERATORS.removeAll(key);
			});
		}, Executors.newSingleThreadExecutor());

		GENSException(future, sender, key, null, true, false);
	}

	public static void GENSRemoveAll(String key) {
		GENERATORS.removeAll(key);
	}

	/**
	 * @apiNote this saves arenas
	 */
	public final static List<Arena> ARENALIST = new ArrayList<>();

	private static void ARENALISTException(CompletableFuture<Void> future, @Nullable CommandSender sender, Arena key) {
		future.handle((reuslt, exp) -> {
			if (key == null)
				if (sender != null)
					sender.sendMessage("Key can't be NULL");
				else
					throw new IllegalStateException("key can't be NULL " + Arrays.toString(exp.getStackTrace()));
			throw new IllegalStateException(Arrays.toString(exp.getStackTrace()));
		});
	}

	public static void addInARENALIST(@Nullable CommandSender sender, JavaPlugin instance, Arena key) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				ARENALIST.add(key);
			});
		}, Executors.newSingleThreadExecutor());

		ARENALISTException(future, sender, key);
	}

	public static void addInARENALIST(Arena key) {
		ARENALIST.add(key);
	}

	public static void setInARENALIST(int index, @Nullable CommandSender sender, JavaPlugin instance, Arena key) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			ARENALIST.set(index, key);
		}, Executors.newSingleThreadExecutor());

		ARENALISTException(future, sender, key);
	}

	public static void setInARENALIST(int index, Arena key) {
		ARENALIST.set(index, key);
	}

	public static void removeFromARENALIST(@Nullable CommandSender sender, JavaPlugin instance, Arena key) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				ARENALIST.remove(key);
			});
		}, Executors.newSingleThreadExecutor());

		ARENALISTException(future, sender, key);
	}

	public static void removeFromARENALIST(Arena key) {
		ARENALIST.remove(key);
	}

	/**
	 * @apiNote this is the arenas directory location
	 */
	public final static String DIR = "plugins/";

	public static Arena arena(int minPlayer, int maxPlayer, int time, Location waitingSpawn, STATES status,
			String arenaName, String worldName) {
		return new Arena(minPlayer, maxPlayer, time, waitingSpawn, status, arenaName, worldName);
	}

	public static PlayerData data(ArenaTeam team, String playerName, STATES status) {
		return new PlayerData(team, playerName, status);
	}

	public static ArenaTeam team(Arena arena, int minNumber, int maxNumber, TEAMS team, Location blockLocation,
			Location waitingSpawn) {
		return new ArenaTeam(arena, minNumber, maxNumber, team, blockLocation, waitingSpawn);
	}

	/**
	 * <p>
	 * this kinda useless if you don't work with npc's
	 * </p>
	 * 
	 * @return the entityPlayer if there is no entityPlayer there
	 */
	public static EntityPlayer getEntityPlayer(Location location, Entity entity, String arenaName) {
		for (Entity e : Bukkit
				.getWorld(PropertiesAPI.getProperty_NS("world", null, DIR + arenaName + "/" + arenaName + ".dcnf"))
				.getEntities()) {
			if (e instanceof Player) {
				Player player = (Player) e;
				if (player.getLocation().equals(location)) {
					CraftPlayer crafted = (CraftPlayer) player;
					return crafted.getHandle();
				}
			}
		}
		return null;
	}

	/**
	 * @return a npc with parameters you given
	 */
	public static NPC addNPC(String arenaName, EntityType type, Material hand, String uuid, String data, String name,
			String skinName, Location location) {
		PropertiesAPI.setProperties_NS(true, arenaName + "-" + name, DIR + "npc.dcnf",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(), type.name(),
				hand.name(), uuid, data, name, skinName);
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(type, name);
		npc.data().set(NPC.Metadata.REMOVE_FROM_PLAYERLIST, name);
		npc.getOrAddTrait(LookClose.class).lookClose(true);
		if (hand != null)
			npc.setItemProvider(() -> {
				return new ItemStack(hand, 1);
			});
		npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(skinName, uuid, data);
		npc.setSneaking(false);
		npc.setProtected(true);
		putInNPCS(arenaName, npc);
		return npc;
	}

	public static NPC addNPC(@Nullable CommandSender sender, JavaPlugin instance, String arenaName, EntityType type,
			Material hand, String uuid, String data, String name, String skinName, Location location) {
		PropertiesAPI.setProperties(instance, true, arenaName + "-" + name, DIR + "npc.dcnf",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(), type.name(),
				hand.name(), uuid, data, name, skinName);
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(type, name);
		npc.data().set(NPC.Metadata.REMOVE_FROM_PLAYERLIST, name);
		npc.getOrAddTrait(LookClose.class).lookClose(true);
		if (hand != null)
			npc.setItemProvider(() -> {
				return new ItemStack(hand, 1);
			});
		npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(skinName, uuid, data);
		npc.setSneaking(false);
		npc.setProtected(true);
		putInNPCS(sender, instance, arenaName, npc);
		return npc;
	}

	public void spawnNPCS(JavaPlugin instance, Arena game) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			for (Arena arena : ArenaManager.ARENALIST) {
				for (int i = 0; i < ArenaManager.NPCS.get(arena.getName()).size(); i++) {
					NPC npc = ArenaManager.NPCS.get(arena.getName()).get(i);
					if (game.getWorld().equals(npc.getStoredLocation().getWorld().getName())) {
						PropertiesAPI.getProperties(game.getName() + "-" + game.getName(),
								ArenaManager.DIR + "npc.dcnf", "NULL").thenAccept((x) -> {
									if (x.get(0) != "NULL") {
										String locates[] = x.get(0).split(",");
										Location location = new Location(Bukkit.getWorld(game.getWorld()),
												Double.parseDouble(locates[0]), Double.parseDouble(locates[1]),
												Double.parseDouble(locates[2]));
										npc.spawn(location);
									}
								});
					}
				}
			}
		});
	}

	public void spawnNPCS(Arena game) {
		for (Arena arena : ArenaManager.ARENALIST) {
			for (int i = 0; i < ArenaManager.NPCS.get(arena.getName()).size(); i++) {
				NPC npc = ArenaManager.NPCS.get(arena.getName()).get(i);
				if (game.getWorld().equals(npc.getStoredLocation().getWorld().getName())) {
					List<String> npcProperties = PropertiesAPI.getProperties_NS(game.getName() + "-" + game.getName(),
							ArenaManager.DIR + "npc.dcnf", null);
					String locates[] = npcProperties.get(0).split(",");
					Location location = new Location(Bukkit.getWorld(game.getWorld()), Double.parseDouble(locates[0]),
							Double.parseDouble(locates[1]), Double.parseDouble(locates[2]));
					npc.spawn(location);
				}
			}
		}
	}

	public static void loadNPCS(String arenaName) {
		try {
			for (String line : Files.readAllLines(Paths.get(DIR + "npc.dcnf"))) {
				if (line.contains(arenaName + "-")) {
					String npcName = line.split("-")[1];
					List<String> npcProperties = PropertiesAPI.getProperties_NS(arenaName + "-" + npcName,
							DIR + "npc.dcnf", null);
					EntityType type = EntityType.valueOf(npcProperties.get(1));
					Material hand = Material.valueOf(npcProperties.get(2));
					String uuid = npcProperties.get(3);
					String data = npcProperties.get(4);
					String name = npcProperties.get(5);
					String skinName = npcProperties.get(6);
					NPC npc = CitizensAPI.getNPCRegistry().createNPC(type, name);
					npc.data().set(NPC.Metadata.REMOVE_FROM_PLAYERLIST, name);
					npc.getOrAddTrait(LookClose.class).lookClose(true);
					if (hand != null)
						npc.setItemProvider(() -> {
							return new ItemStack(hand, 1);
						});
					npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(skinName, uuid, data);
					npc.setSneaking(false);
					npc.setProtected(true);
					putInNPCS(arenaName, npc);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadNPCS(JavaPlugin instance, String arenaName) {
		Bukkit.getScheduler().runTask(instance, () -> {
			try {
				for (String line : Files.readAllLines(Paths.get(DIR + "npc.dcnf"))) {
					if (line.contains(arenaName + "-")) {
						String npcName = line.split("-")[1];
						PropertiesAPI.getProperties(arenaName + "-" + npcName, DIR + "npc.dcnf", "NULL")
								.thenAccept((x) -> {
									if (x.get(0) != "NULL") {
										EntityType type = EntityType.valueOf(x.get(1));
										Material hand = Material.valueOf(x.get(2));
										String uuid = x.get(3);
										String data = x.get(4);
										String name = x.get(5);
										String skinName = x.get(6);
										NPC npc = CitizensAPI.getNPCRegistry().createNPC(type, name);
										npc.data().set(NPC.Metadata.REMOVE_FROM_PLAYERLIST, name);
										npc.getOrAddTrait(LookClose.class).lookClose(true);
										if (hand != null)
											npc.setItemProvider(() -> {
												return new ItemStack(hand, 1);
											});
										npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(skinName, uuid, data);
										npc.setSneaking(false);
										npc.setProtected(true);
										putInNPCS(arenaName, npc);
									}
								});
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static void createArena(String arenaName, Integer minPlayer, Integer maxPlayer, Integer arenaTime,
			Location waitingSpawn, String world) {
		String arenaDir = DIR + arenaName;
		String arenaFile = DIR + arenaName + "/" + arenaName + ".dcnf";
		try {
			if (Files.notExists(Paths.get(arenaDir)) || Files.notExists(Paths.get(arenaFile))) {
				Files.createDirectory(Paths.get(arenaDir));
				Files.createFile(Paths.get(arenaFile));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		int min;

		if (minPlayer != null) {
			min = minPlayer;
			PropertiesAPI.setProperty_NS("minPlayers", String.valueOf(min), arenaFile);
		} else {
			min = Integer.parseInt(PropertiesAPI.getProperty_NS("minPlayers", "2", arenaFile));
		}

		int max;

		if (maxPlayer != null) {
			max = maxPlayer;
			PropertiesAPI.setProperty_NS("maxPlayers", String.valueOf(max), arenaFile);
		} else {
			max = Integer.parseInt(PropertiesAPI.getProperty_NS("maxPlayers", "8", arenaFile));
		}

		int time;

		if (arenaTime != null) {
			time = arenaTime;
			PropertiesAPI.setProperty_NS("arenaTime", String.valueOf(min), arenaFile);
		} else {
			time = Integer.parseInt(PropertiesAPI.getProperty_NS("arenaTime", "1800", arenaFile));
		}

		Location waiting;
		if (waitingSpawn != null) {
			waiting = waitingSpawn;
			PropertiesAPI.setProperties_NS(true, "waitingSpawn", arenaFile, String.valueOf(waiting.getBlockX()),
					String.valueOf(waiting.getBlockY()), String.valueOf(waiting.getBlockZ()));
		} else {

			List<String> locationCoordinates = PropertiesAPI.getProperties_NNS("waitingSpawn", arenaFile, "0", "0",
					"0");
			waiting = new Location(Bukkit.getWorld(world), Integer.parseInt(locationCoordinates.get(0)),
					Integer.parseInt(locationCoordinates.get(1)), Integer.parseInt(locationCoordinates.get(2)));
		}

		Arena arena = new Arena(min, max, time, waiting, STATES.INPROCESS, arenaFile, world);
		addInARENALIST(arena);
	}

	/**
	 * @apiNote this is asynchronously function
	 *          <p>
	 *          it creates a arena with given parameters
	 *          </p>
	 */
	public static CompletableFuture<Void> createArena(@Nullable CommandSender sender, JavaPlugin instance,
			String arenaName, Integer minPlayer, Integer maxPlayer, Integer arenaTime, Location waitingSpawn,
			String world) {
		final String arenaDir = DIR + arenaName;
		final String arenaFile = DIR + arenaName + "/" + arenaName + ".dcnf";
		Bukkit.getScheduler().runTask(instance, () -> {
			try {
				if (Files.notExists(Paths.get(arenaDir)) || Files.notExists(Paths.get(arenaFile))) {
					Files.createDirectory(Paths.get(arenaDir));
					Files.createFile(Paths.get(arenaFile));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		final List<Object> values = new ArrayList<Object>();
		CompletableFuture<String> minC = PropertiesAPI.getProperty("minPlayers", "2", arenaFile);
		if (minPlayer != null) {
			values.add(minPlayer);
			PropertiesAPI.setProperty(instance, "minPlayers", String.valueOf(minPlayer), arenaFile);
		} else {
			minC.thenAccept((x) -> {
				Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
					values.add(x);
				});
			});
		}

		CompletableFuture<String> maxC = PropertiesAPI.getProperty("maxPlayers", "8", arenaFile);
		if (maxPlayer != null) {
			values.add(maxPlayer);
			PropertiesAPI.setProperty(instance, "maxPlayers", String.valueOf(maxPlayer), arenaFile);
		} else {
			maxC.thenAccept((x) -> {
				Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
					values.add(x);
				});
			});
		}

		CompletableFuture<String> timeC = PropertiesAPI.getProperty("arenaTime", "1800", arenaFile);
		if (arenaTime != null) {
			values.add(arenaTime);
			PropertiesAPI.setProperty(instance, "arenaTime", String.valueOf(arenaTime), arenaFile);
		} else {
			timeC.thenAccept((x) -> {
				Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
					values.add(x);
				});
			});
		}

		CompletableFuture<List<String>> waitingC = PropertiesAPI.getProperties("waitingSpawn", arenaFile, "0", "0",
				"0");
		if (waitingSpawn != null) {
			values.add(waitingSpawn);
			PropertiesAPI.setProperties(instance, true, "waitingSpawn", String.valueOf(waitingSpawn.getBlockX()),
					String.valueOf(waitingSpawn.getBlockY()), String.valueOf(waitingSpawn.getBlockZ()));
		} else {
			waitingC.thenAccept((x) -> {
				Location location = new Location(Bukkit.getWorld(world), Double.parseDouble(x.get(0)),
						Double.parseDouble(x.get(1)), Double.parseDouble(x.get(2)));
				Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
					values.add(location);
				});
			});
		}

		CompletableFuture<Void> all = CompletableFuture.allOf(minC, maxC, timeC, waitingC);
		return all.thenAccept((x) -> {
			Integer min = null;
			if (values.get(0) instanceof Integer) {
				min = (Integer) values.get(0);
			}

			Integer max = null;
			if (values.get(1) instanceof Integer) {
				max = (Integer) values.get(1);
			}

			Integer time = null;
			if (values.get(2) instanceof Integer) {
				time = (Integer) values.get(2);
			}

			Location waiting = null;
			if (values.get(3) instanceof Location) {
				waiting = (Location) values.get(3);
			}
			Arena arena = new Arena(min, max, time, waiting, STATES.INPROCESS, arenaName, world);
			addInARENALIST(sender, instance, arena);
		});
	}

	/**
	 * <p>
	 * its almost same as setPlayerTeam but it checks if the player is valid in your
	 * arena
	 * </p>
	 */
	public static void selectTeam(@Nullable CommandSender sender, JavaPlugin instance, String playerName, TEAMS team) {
		Arena arena = getPlayersArena(sender, instance, playerName);
		ArenaTeam teamm = createTeam(arena, arena.getWorld(), team);

		if (ARENAS.get(arena).contains(playerName))
			setPlayerTeam(sender, instance, playerName, teamm);
	}

	public static void selectTeam(@Nullable CommandSender sender, String playerName, TEAMS team) {
		Arena arena = getPlayersArena(sender, playerName);
		ArenaTeam teamm = createTeam(arena, arena.getWorld(), team);

		if (ARENAS.get(arena).contains(playerName))
			setPlayerTeam(playerName, teamm);
	}

	/**
	 * @return a arenaTeam by given parameters
	 */
	public static ArenaTeam createTeam(Arena arena, String world, TEAMS teamm) {

		int min = Integer.parseInt(PropertiesAPI.getProperty_NS(teamm.name() + ".min", "1",
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
		int max = Integer.parseInt(PropertiesAPI.getProperty_NS(teamm.name() + "max", "2",
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));

		String coordinates[] = PropertiesAPI
				.getProperty_NS(teamm.name() + ".block", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf")
				.split(",");
		Location bed = new Location(Bukkit.getWorld(world), Double.parseDouble(coordinates[0]),
				Double.parseDouble(coordinates[1]), Double.parseDouble(coordinates[2]));
		String ncoordinates[] = PropertiesAPI.getProperty_NS(teamm.name() + ".teamspawn", null,
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf").split(",");
		Location spawn = new Location(Bukkit.getWorld(world), Double.parseDouble(ncoordinates[0]),
				Double.parseDouble(ncoordinates[1]), Double.parseDouble(ncoordinates[2]));

		return new ArenaTeam(arena, min, max, teamm, bed, spawn);
	}

	public static Arena loadArena(String arenaName, String arenaFile) {
		int min = Integer.parseInt(PropertiesAPI.getProperty_NS("min", "2", arenaFile));
		int max = Integer.parseInt(PropertiesAPI.getProperty_NS("max", "8", arenaFile));
		int time = Integer.parseInt(PropertiesAPI.getProperty_NS("arenaTime", "1800", arenaFile));

		String locationCoordinates[] = PropertiesAPI.getProperty_NS("waitingSpawn", null, arenaFile).split(",");

		Location location = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_NS("world", null, arenaFile)),
				Double.parseDouble(locationCoordinates[0]), Double.parseDouble(locationCoordinates[1]),
				Double.parseDouble(locationCoordinates[2]));

		String nlocationCoordinates[] = PropertiesAPI.getProperty_NS("pos1", null, arenaFile).split(",");

		Location pos1 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_NS("world", arenaFile, null)),
				Double.parseDouble(nlocationCoordinates[0]), Double.parseDouble(nlocationCoordinates[1]),
				Double.parseDouble(nlocationCoordinates[2]));

		String nnlocationCoordinates[] = PropertiesAPI.getProperty_NS("pos2", null, arenaFile).split(",");

		Location pos2 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_NS("world", null, arenaFile)),
				Double.parseDouble(nnlocationCoordinates[0]), Double.parseDouble(nnlocationCoordinates[1]),
				Double.parseDouble(nnlocationCoordinates[2]));

		Arena arena = new Arena(min, max, time, location, STATES.WAITING, arenaName,
				PropertiesAPI.getProperty_NS("world", null, arenaFile), pos1, pos2);
		addInARENALIST(arena);
		return arena;
	}

	public static List<Arena> loadArenasByAnnotation(Class<?> clazz) {
		ArenaMaker maker = clazz.getAnnotation(ArenaMaker.class);
		List<Arena> arenas = new ArrayList<>();
		List<String> files = Arrays.asList(maker.arenas());
		for (String arenaName : files) {
			String arenaFile = DIR + arenaName + "/" + arenaName + ".dcnf";

			int min = Integer.parseInt(PropertiesAPI.getProperty_NS("min", "2", arenaFile));
			int max = Integer.parseInt(PropertiesAPI.getProperty_NS("max", "8", arenaFile));
			int time = Integer.parseInt(PropertiesAPI.getProperty_NS("arenaTime", "1800", arenaFile));

			String locationCoordinates[] = PropertiesAPI.getProperty_NS("waitingSpawn", null, arenaFile).split(",");

			Location location = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_NS("world", null, arenaFile)),
					Double.parseDouble(locationCoordinates[0]), Double.parseDouble(locationCoordinates[1]),
					Double.parseDouble(locationCoordinates[2]));

			String nlocationCoordinates[] = PropertiesAPI.getProperty_NS("pos1", null, arenaFile).split(",");

			Location pos1 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_NS("world", arenaFile, null)),
					Double.parseDouble(nlocationCoordinates[0]), Double.parseDouble(nlocationCoordinates[1]),
					Double.parseDouble(nlocationCoordinates[2]));

			String nnlocationCoordinates[] = PropertiesAPI.getProperty_NS("pos2", null, arenaFile).split(",");

			Location pos2 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_NS("world", null, arenaFile)),
					Double.parseDouble(nnlocationCoordinates[0]), Double.parseDouble(nnlocationCoordinates[1]),
					Double.parseDouble(nnlocationCoordinates[2]));

			Arena arena = new Arena(min, max, time, location, STATES.WAITING, arenaName,
					PropertiesAPI.getProperty_NS("world", null, arenaFile), pos1, pos2);

			addInARENALIST(arena);
		}
		return arenas;
	}

	/**
	 * <p>
	 * It blocks thread for a moment
	 * </p>
	 */
	public static void loadArenas() throws Exception {
		List<Path> dirs = null;
		List<Path> files = null;
		try {
			dirs = Files.walk(Paths.get(DIR)).filter(Files::isDirectory).collect(Collectors.toList());
			files = Files.walk(Paths.get(DIR)).filter(Files::isRegularFile).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		dirs.remove(0);

		for (Path dir : dirs) {
			for (Path file : files) {
				String arenaName = null;
				Optional<String> k = dirs.stream().filter((x) -> x.equals(dir))
						.filter((x) -> x.toString().split("//")[1].contains(".dcnf"))
						.map((x) -> x.toString().split("//")[1]).findFirst();
				if (k.isPresent()) {
					arenaName = k.get();
				}

				String arenaFile = file.toString();

				String min = PropertiesAPI.getProperty_NS("min", "2", arenaFile);
				String max = PropertiesAPI.getProperty_NS("max", "8", arenaFile);
				String time = PropertiesAPI.getProperty_NS("arenaTime", "1800", arenaFile);

				String locationCoordinates[] = PropertiesAPI.getProperty_NS("waitingSpawn", null, arenaFile).split(",");

				Location location = new Location(
						Bukkit.getWorld(PropertiesAPI.getProperty_NS("world", null, arenaFile)),
						Double.parseDouble(locationCoordinates[0]), Double.parseDouble(locationCoordinates[1]),
						Double.parseDouble(locationCoordinates[2]));

				String nlocationCoordinates[] = PropertiesAPI.getProperty_NS("pos1", null, arenaFile).split(",");

				Location pos1 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_NS("world", arenaFile, null)),
						Double.parseDouble(nlocationCoordinates[0]), Double.parseDouble(nlocationCoordinates[1]),
						Double.parseDouble(nlocationCoordinates[2]));

				String nnlocationCoordinates[] = PropertiesAPI.getProperty_NS("pos2", null, arenaFile).split(",");

				Location pos2 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_NS("world", null, arenaFile)),
						Double.parseDouble(nnlocationCoordinates[0]), Double.parseDouble(nnlocationCoordinates[1]),
						Double.parseDouble(nnlocationCoordinates[2]));

				String world = PropertiesAPI.getProperty_NS("world", null, arenaFile);

				if (pos2 == null || pos1 == null || location == null || world == null)
					throw new IllegalStateException("Some of values or null for " + arenaName);
				Arena arena = new Arena(Integer.parseInt(min), Integer.parseInt(max), Integer.parseInt(time), location,
						STATES.WAITING, arenaName, world, pos1, pos2);
				addInARENALIST(arena);
			}
		}
	}

	/**
	 * @return list of arena players
	 */
	public static List<Player> getArenasPlayers(Arena arena) {
		return ARENAS.get(arena).stream().map((x) -> Bukkit.getPlayer(x)).collect(Collectors.toList());
	}

	/**
	 * @return a arena by playerName
	 */
	public static Arena getPlayersArena(@Nullable CommandSender sender, String name) {
		LinkedList<Arena> lnk = new LinkedList<>();
		for (Arena arena : ARENALIST) {
			List<String> ls = ARENAS.get(arena);
			ls.remove(0);
			for (String playerName : ls) {
				if (playerName.equals(name)) {
					lnk.add(arena);
					break;
				}
			}
			if (lnk.peek() != null) {
				break;
			}
		}
		if (lnk.getFirst() == null) {
			if (sender != null)
				sender.sendMessage("Arena not found");
			throw new IllegalStateException("Arena not found");
		}

		return lnk.getFirst();
	}

	public static Arena getPlayersArena(@Nullable CommandSender sender, JavaPlugin instance, String playerName) {
		ConcurrentLinkedQueue<Arena> lnk = new ConcurrentLinkedQueue<>();
		Optional<Arena> arena = ARENALIST.stream().filter((x) -> ARENAS.get(x).contains(playerName)).findFirst();
		if (arena.isPresent()) {
			lnk.add(arena.get());
		} else {
			if (sender != null)
				sender.sendMessage("Arena not found");
			throw new IllegalStateException("Arena not found");
		}
		return lnk.peek();
	}

	public static ArenaTeam getPlayersTeam(@Nullable CommandSender sender, JavaPlugin instance, String playerName) {
		ConcurrentLinkedQueue<ArenaTeam> lnk = new ConcurrentLinkedQueue<>();
		Optional<String> player = PLAYERS.keySet().stream().filter((x) -> x.equals(playerName)).findFirst();
		if (player.isPresent()) {
			Optional<Entry<String, PlayerData>> pData = PLAYERS.entrySet().stream()
					.filter((x) -> x.getKey().equals(player.get()) && x.getValue().equals(PLAYERS.get(player.get())))
					.findFirst();
			PlayerData playerData = pData.get().getValue();
			Arena arena = getPlayersArena(sender, instance, playerName);
			ArenaTeam arenaTeam = createTeam(arena, arena.getWorld(), playerData.getTeam().getTeam());
			lnk.add(arenaTeam);
		}
		return lnk.peek();
	}

	public static ArenaTeam getPlayersTeam(@Nullable CommandSender sender, String playerName) {
		ConcurrentLinkedQueue<ArenaTeam> lnk = new ConcurrentLinkedQueue<>();
		Optional<String> player = PLAYERS.keySet().stream().filter((x) -> x.equals(playerName)).findFirst();
		if (player.isPresent()) {
			Optional<Entry<String, PlayerData>> pData = PLAYERS.entrySet().stream()
					.filter((x) -> x.getKey().equals(player.get()) && x.getValue().equals(PLAYERS.get(player.get())))
					.findFirst();
			PlayerData playerData = pData.get().getValue();
			Arena arena = getPlayersArena(sender, playerName);
			ArenaTeam arenaTeam = createTeam(arena, arena.getWorld(), playerData.getTeam().getTeam());
			lnk.add(arenaTeam);
		}
		return lnk.peek();
	}

	public static void addPlayer(String playerName, Arena arena, STATES status, ArenaTeam team,
			Location locationToSpawn, boolean check) {
		Player player = Bukkit.getPlayer(playerName);
		if (player != null && ARENALIST.contains(arena)) {
			if (!isPlayerSettedOnce(playerName)) {
				PlayerData data = new PlayerData(team, playerName, status);
				putInPLAYERS(playerName, data);
				putInARENAS(arena, playerName);
			} else {
				setInARENAS(getArenaIndex(arena), arena, playerName);
			}
		}
		if (locationToSpawn != null && player != null)
			player.teleport(locationToSpawn);
		if (check == true) {
			String property = PropertiesAPI.getProperty_NS("selectTeamItem", "COMPASS",
					DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
			ItemStack item = new ItemStack(Material.valueOf(property), 1);
			player.getInventory().setItem(40, item);
		}
	}

	public static void addPlayer(@Nullable CommandSender sender, JavaPlugin instance, String playerName, Arena arena,
			STATES status, ArenaTeam team, Location locationToSpawn, boolean check) {
		Player player = Bukkit.getPlayer(playerName);
		if (player != null) {
			if (!isPlayerSettedOnce(playerName)) {
				PlayerData data = new PlayerData(team, playerName, status);
				putInPLAYERS(sender, instance, playerName, data);
				putInARENAS(sender, instance, arena, playerName);
			} else {
				setInARENAS(getArenaIndex(arena), arena, playerName);
			}
			if (locationToSpawn != null && player != null)
				player.teleport(locationToSpawn);
			if (check == true) {
				String property = PropertiesAPI.getProperty_C("selectTeamItem", "COMPASS",
						DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
				ItemStack item = new ItemStack(Material.valueOf(property), 1);
				player.getInventory().setItem(40, item);
			}
		}

	}

	public static int getArenaIndex(Arena arena) {
		ConcurrentLinkedQueue<Integer> lnk = new ConcurrentLinkedQueue<>();
		Optional<Arena> cachedArena = ARENAS.keys().stream().filter((x -> x.equals(arena))).findFirst();
		if (cachedArena.isPresent()) {
			ImmutableList<Arena> keySet = ImmutableList.copyOf(ARENAS.keySet());
			lnk.add(keySet.indexOf(cachedArena.get()));
		} else {
			lnk.add(-1);
		}

		return lnk.peek();
	}

	public static Boolean isPlayerSettedOnce(String playerName) {
		Optional<Entry<String, PlayerData>> player = PLAYERS.entrySet().stream()
				.filter((x) -> x.getKey().equals(playerName)).findFirst();
		if (player.isPresent()) {
			if (player.get().getValue() != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static void randomAddPlayer(@Nullable CommandSender sender, JavaPlugin instance, String playerName,
			Arena arena, STATES status, Location locationToSpawn, boolean check) {
		Random rand = new Random();
		int random = rand.nextInt(TEAMS.values().length);
		TEAMS teamm = TEAMS.values()[random];

		String coordinates[] = PropertiesAPI
				.getProperty_NS(teamm.name() + ".block", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf")
				.split(",");
		Location bed = new Location(Bukkit.getWorld(arena.getWorld()), Double.parseDouble(coordinates[0]),
				Double.parseDouble(coordinates[1]), Double.parseDouble(coordinates[2]));
		String ncoordinates[] = PropertiesAPI.getProperty_NS(teamm.name() + ".teamspawn", null,
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf").split(",");
		Location spawn = new Location(Bukkit.getWorld(arena.getWorld()), Double.parseDouble(ncoordinates[0]),
				Double.parseDouble(ncoordinates[1]), Double.parseDouble(ncoordinates[2]));
		ArenaTeam team = new ArenaTeam(arena,
				Integer.parseInt(PropertiesAPI.getProperty_NS(teamm.name() + ".min", "1",
						DIR + "/" + arena.getName() + ".dcnf")),
				Integer.parseInt(PropertiesAPI.getProperty_NS(teamm.name() + ".max", "2",
						DIR + "/" + arena.getName() + ".dcnf")),
				teamm, bed, spawn);
		Player player = Bukkit.getPlayer(playerName);
		if (player != null && ARENALIST.contains(arena)) {
			putInARENAS(sender, instance, arena, playerName);
			PlayerData data = new PlayerData(team, playerName, status);
			putInPLAYERS(sender, instance, playerName, data);
		}
		if (locationToSpawn != null && player != null)
			player.teleport(locationToSpawn);
		if (check == true) {
			PropertiesAPI
					.getProperty("selectTeamItem", "COMPASS", DIR + arena.getName() + "/" + arena.getName() + ".dcnf")
					.thenAccept((x) -> {
						Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
							ItemStack item = new ItemStack(Material.valueOf(x), 1);
							player.getInventory().setItem(40, item);
						});
					});
		}
	}

	public static void removePlayer(@Nullable CommandSender sender, JavaPlugin instance, String playerName,
			Arena arena) {
		removeFromARENAS(sender, instance, arena, playerName);
		PLAYERSRemoveAll(sender, instance, playerName);
	}

	public static void removePlayer(String playerName, Arena arena) {
		removeFromARENAS(arena, playerName);
		PLAYERSRemoveAll(playerName);
	}

	public static STATES getArenaStatus(Arena arena) {
		Optional<Entry<Arena, String>> status = ARENAS.entries().stream()
				.filter((x) -> x.getKey().equals(arena) && STATES.valueOf(x.getValue()) != null).findFirst();
		if (status.isPresent()) {
			return (STATES.valueOf(status.get().getValue()));
		}
		return null;
	}

	public static void setArenaStatus(Arena arena, STATES status) {
		setInARENAS(0, arena, status.name());
	}

	public static void setArenaStatus(@Nullable CommandSender sender, JavaPlugin instance, Arena arena, STATES status) {
		setInARENAS(0, sender, instance, arena, status.name());
	}

	public static STATES getPlayerStatus(String playerName) {
		Optional<Entry<String, PlayerData>> value = PLAYERS.entrySet().stream()
				.filter((x) -> x.getKey().equals(playerName) && x.getValue() instanceof PlayerData).findFirst();
		if (value.isPresent()) {
			PlayerData data = value.get().getValue();
			return data.getStatus();
		}
		return null;
	}

	public static Arena getArenaByName(String arenaName) {
		Optional<Arena> arena = ARENALIST.stream().filter((x) -> x.getName().equals(arenaName)).findFirst();
		if (arena.isPresent()) {
			return arena.get();
		}
		return null;
	}

	public static ConcurrentSkipListSet<String> getTeamsPlayers(@Nullable CommandSender sender, JavaPlugin instance,
			Arena arena, TEAMS team) {
		ConcurrentSkipListSet<String> fls = new ConcurrentSkipListSet<>();
		arena.getPlayersNames().stream().filter((x) -> getPlayersTeam(sender, instance, x).getTeam() == team)
				.forEach((x) -> {
					fls.add(x);
				});
		return fls;
	}

	public static Arena getArenaByPlayerAndTeam(@Nullable CommandSender sender, JavaPlugin instance, String playerName,
			ArenaTeam team) {
		Optional<Arena> value = ARENALIST.stream()
				.filter((x) -> x.equals(team.getArena()) && x.equals(getPlayersArena(sender, instance, playerName)))
				.findFirst();
		if (value.isPresent()) {
			return value.get();
		}
		return null;
	}

	public static Integer getPlayerIndexInArena(String playerName, Arena arena) {
		Optional<Arena> arenaa = ARENALIST.stream().filter((x) -> x.equals(arena)).findFirst();
		if (arenaa.isPresent()) {
			return ARENALIST.indexOf(arenaa.get());
		}
		return null;
	}

	public static String getArenaWorld(Arena arena) {
		if (arena.getWorld() != null) {
			return arena.getWorld();
		} else {
			return PropertiesAPI.getProperty_C("world", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
		}
	}

	public static void setArenaWorld(Arena arena, String world) {
		arena.setWorld(world);
		PropertiesAPI.setProperty_NS("world", world, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static void setArenaWorld(JavaPlugin instance, Arena arena, String world) {
		arena.setWorld(world);
		PropertiesAPI.setProperty(instance, "world", world, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static void setPlayerTeam(String playerName, ArenaTeam team) {
		PlayerData data = new PlayerData(team, playerName, getPlayerStatus(playerName));
		putInPLAYERS(playerName, data);
	}

	public static void setPlayerTeam(@Nullable CommandSender sender, JavaPlugin instance, String playerName,
			ArenaTeam team) {
		PlayerData data = new PlayerData(team, playerName, getPlayerStatus(playerName));
		putInPLAYERS(sender, instance, playerName, data);
	}

	/**
	 * <p>
	 * It sets the first position in arena
	 * </p>
	 */
	public static void setPos1(Arena arena, Location location) {
		arena.setPos1(location);
		PropertiesAPI.setProperty_NS("pos1",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static void setPos1(JavaPlugin instance, Arena arena, Location location) {
		arena.setPos1(location);
		PropertiesAPI.setProperty(instance, "pos1",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	/**
	 * <p>
	 * It sets the second position in arena
	 * </p>
	 */
	public static void setPos2(Arena arena, Location location) {
		arena.setPos2(location);
		PropertiesAPI.setProperty_NS("pos2",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static void setPos2(JavaPlugin instance, Arena arena, Location location) {
		arena.setPos2(location);
		PropertiesAPI.setProperty(instance, "pos2",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	/**
	 * <p>
	 * Its just a location point that used in isEntityOnRegion function
	 * </p>
	 * 
	 * @return the first position of the arena
	 */
	public static Location getPos1(Arena arena) {
		if (arena.getPos1() != null) {
			return arena.getPos1();
		} else {
			String values[] = PropertiesAPI
					.getProperty_C("pos1", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf").split(",");
			return new Location(Bukkit.getWorld(arena.getWorld()), Integer.parseInt(values[0]),
					Integer.parseInt(values[1]), Integer.parseInt(values[2]));
		}
	}

	/**
	 * <p>
	 * Its just a location point that used in isEntityOnRegion function
	 * </p>
	 * 
	 * @return the first position of the arena
	 */
	public static Location getPos1(String arenaName, String worldName) {
		String values[] = PropertiesAPI.getProperty_C("pos1", null, DIR + arenaName + "/" + arenaName + ".dcnf")
				.split(",");
		return new Location(Bukkit.getWorld(worldName), Integer.parseInt(values[0]), Integer.parseInt(values[1]),
				Integer.parseInt(values[2]));
	}

	/**
	 * <p>
	 * Its just a location point that used in isEntityOnRegion function
	 * </p>
	 * 
	 * @return the second position of the arena
	 */
	public static Location getPos2(Arena arena) {
		if (arena.getPos1() != null) {
			return arena.getPos1();
		} else {
			String values[] = PropertiesAPI
					.getProperty_C("pos2", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf").split(",");
			return new Location(Bukkit.getWorld(arena.getWorld()), Integer.parseInt(values[0]),
					Integer.parseInt(values[1]), Integer.parseInt(values[2]));
		}
	}

	/**
	 * <p>
	 * Its just a location point that used in isEntityOnRegion function
	 * </p>
	 * 
	 * @return the second position of the arena
	 */
	public static Location getPos2(String arenaName, String worldName) {
		String values[] = PropertiesAPI.getProperty_C("pos2", null, DIR + arenaName + "/" + arenaName + ".dcnf")
				.split(",");
		return new Location(Bukkit.getWorld(worldName), Integer.parseInt(values[0]), Integer.parseInt(values[1]),
				Integer.parseInt(values[2]));
	}

	public static void setGenerator(Location location, String arenaName, String generatorName, String itemName,
			String secounds, String amount) {
		PropertiesAPI.setProperties_NS(true, generatorName + "." + itemName,
				DIR + arenaName + "/" + arenaName + ".dcnf",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(), secounds, amount);
	}

	public static Location getGeneratorLocation(JavaPlugin instance, String arenaName, String generatorName,
			String itemName) {
		String values[] = new String[3];
		PropertiesAPI
				.getProperties_C(instance, generatorName + "." + itemName, DIR + arenaName + "/" + arenaName + ".dcnf",
						"NULL")
				.stream().filter((x) -> x.contains(",")).map((x) -> x.split("-\\s*", 2)[1].split(",")).forEach((x) -> {
					values[0] = x[0];
					values[1] = x[1];
					values[2] = x[2];
				});
		return new Location(Bukkit.getWorld(itemName), Integer.parseInt(values[0]), Integer.parseInt(values[1]),
				Integer.parseInt(values[2]));
	}

	public static ArenaTeam getTeamByArenaAndPlayer(@Nullable CommandSender sender, String playerName, Arena arena) {
		Optional<Arena> aren = ARENALIST.stream().filter((x) -> x.equals(arena) && x.equals(arena)).findFirst();
		if (aren.isPresent()) {
			return getPlayersTeam(sender, playerName);
		}
		return null;
	}

	public static ArenaTeam getTeamByArenaAndPlayer(@Nullable CommandSender sender, JavaPlugin instance,
			String playerName, Arena arena) {
		Optional<Arena> aren = ARENALIST.stream().filter((x) -> x.equals(arena) && x.equals(arena)).findFirst();
		if (aren.isPresent()) {
			return getPlayersTeam(sender, instance, playerName);
		}
		return null;
	}

	public static void setTeamSpawn(ArenaTeam team, Location location) {
		team.setTeamSpawn(location);
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".teamspawn",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
	}

	public static void setTeamSpawn(JavaPlugin instance, ArenaTeam team, Location location) {
		team.setTeamSpawn(location);
		PropertiesAPI.setProperty(instance, team.getTeam().name() + ".teamspawn",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
	}

	public static Location getTeamSpawn(ArenaTeam team, String world) {
		if (team.getTeamSpawn() != null) {
			return team.getTeamSpawn();
		}
		String values[] = PropertiesAPI.getProperty_C(team.getTeam().name() + ".spawn", null,
				DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf").split(",");
		return new Location(Bukkit.getWorld(world), Double.parseDouble(values[0]), Double.parseDouble(values[1]),
				Double.parseDouble(values[2]));
	}

	/**
	 * <p>
	 * it sets a block for you in your arena
	 * </p>
	 */
	public static void setBlockSpawn(ArenaTeam team, Location location) {
		team.setBlockSpawn(location);
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".block",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
	}

	public static void setBlockSpawn(JavaPlugin instance, ArenaTeam team, Location location) {
		team.setBlockSpawn(location);
		PropertiesAPI.setProperty(instance, team.getTeam().name() + ".block",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
	}

	/**
	 * @return the location of block of the arenaTeam
	 */
	public static Location getBlockSpawn(JavaPlugin instance, ArenaTeam team) {
		if (team.getBlockSpawn() != null) {
			return team.getBlockSpawn();
		} else {
			String ls[] = new String[3];
			PropertiesAPI
					.getProperties_C(instance, team.getTeam().name() + ".block",
							DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf", "NULL")
					.stream().map((x) -> x.split("-\\s*", 2)[1].split(",")).forEach((x) -> {
						ls[0] = x[0];
						ls[1] = x[1];
						ls[2] = x[2];
					});
			return new Location(Bukkit.getWorld(team.getArena().getWorld()), Integer.parseInt(ls[0]),
					Integer.parseInt(ls[1]), Integer.parseInt(ls[2]));
		}
	}

	public static Location getBlockSpawn(ArenaTeam team) {
		if (team.getBlockSpawn() != null) {
			return team.getBlockSpawn();
		} else {
			String ls[] = new String[3];
			PropertiesAPI
					.getProperties_NS(team.getTeam().name() + ".block",
							DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf", null)
					.stream().map((x) -> x.split("-\\s*", 2)[1].split(",")).forEach((x) -> {
						ls[0] = x[0];
						ls[1] = x[1];
						ls[2] = x[2];
					});
			return new Location(Bukkit.getWorld(team.getArena().getWorld()), Integer.parseInt(ls[0]),
					Integer.parseInt(ls[1]), Integer.parseInt(ls[2]));
		}
	}

	public static Location getWaitingSpawn(Arena arena) {
		if (arena.getWaitingSpawn() != null) {
			return arena.getWaitingSpawn();
		} else {
			String ls[] = new String[3];
			PropertiesAPI.getProperties_NS("waiting", DIR + arena.getName() + "/" + arena.getName() + ".dcnf", null)
					.stream().map((x) -> x.split("-\\s*", 2)[1].split(",")).forEach((x) -> {
						ls[0] = x[0];
						ls[1] = x[1];
						ls[2] = x[2];
					});
			return new Location(Bukkit.getWorld(arena.getWorld()), Integer.parseInt(ls[0]), Integer.parseInt(ls[1]),
					Integer.parseInt(ls[2]));
		}
	}

	public static Location getWaitingSpawn(JavaPlugin instance, String arenaName, String worldName) {

		String ls[] = new String[3];
		PropertiesAPI.getProperties_C(instance, "waiting", DIR + arenaName + "/" + arenaName + ".dcnf", "NULL").stream()
				.map((x) -> x.split("-\\s*", 2)[1].split(",")).forEach((x) -> {
					ls[0] = x[0];
					ls[1] = x[1];
					ls[2] = x[2];
				});
		return new Location(Bukkit.getWorld(worldName), Integer.parseInt(ls[0]), Integer.parseInt(ls[1]),
				Integer.parseInt(ls[2]));
	}

	public static void setMaxArena(Arena arena, String number) {
		arena.setMaxPlayers(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS("max", number, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static void setMaxArena(JavaPlugin instance, Arena arena, String number) {
		arena.setMaxPlayers(Integer.parseInt(number));
		PropertiesAPI.setProperty(instance, "max", number, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static Integer getMaxArena(Arena arena) {
		return arena.getMaxPlayer() != null ? arena.getMaxPlayer()
				: Integer.parseInt(PropertiesAPI.getProperty_C("max", null,
						DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
	}

	public static void setMinArena(Arena arena, String number) {
		arena.setMinPlayers(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS("min", number, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static void setMinArena(JavaPlugin instance, Arena arena, String number) {
		arena.setMinPlayers(Integer.parseInt(number));
		PropertiesAPI.setProperty(instance, "min", number, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static Integer getMinArena(Arena arena) {
		return arena.getMinPlayer() != null ? arena.getMinPlayer()
				: Integer.parseInt(PropertiesAPI.getProperty_C("min", null,
						DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
	}

	public static void setTeamMax(ArenaTeam team, String number) {
		team.setMaxNumber(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".min", number,
				DIR + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
	}

	public static void setTeamMax(JavaPlugin instance, ArenaTeam team, String number) {
		team.setMaxNumber(Integer.parseInt(number));
		PropertiesAPI.setProperty(instance, team.getTeam().name() + ".min", number,
				DIR + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
	}

	public static Integer getTeamMax(ArenaTeam team) {
		return team.getMaxNumber() != null ? team.getMaxNumber()
				: Integer.parseInt(PropertiesAPI.getProperty_C(team.getTeam().name() + ".max", null,
						DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf"));
	}

	public static void setTeamMin(ArenaTeam team, String number) {
		team.setMinNumber(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".max", number,
				DIR + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
	}

	public static void setTeamMin(JavaPlugin instance, ArenaTeam team, String number) {
		team.setMinNumber(Integer.parseInt(number));
		PropertiesAPI.setProperty(instance, team.getTeam().name() + ".max", number,
				DIR + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
	}

	public static Integer getTeamMin(ArenaTeam team) {
		return team.getMinNumber() != null ? team.getMinNumber()
				: Integer.parseInt(PropertiesAPI.getProperty_C(team.getTeam().name() + ".min", null,
						DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf"));
	}

	public static boolean isEntityOnRegion(Arena arena, Location entityLocation) {
		Location pos1 = getPos1(arena);
		Location pos2 = getPos2(arena);
		return (entityLocation.getX() >= Math.min(pos1.getX(), pos2.getX())
				&& entityLocation.getY() >= Math.min(pos1.getY(), pos2.getY())
				&& entityLocation.getZ() >= Math.min(pos1.getZ(), pos2.getZ())
				&& entityLocation.getX() <= Math.max(pos1.getX(), pos2.getX())
				&& entityLocation.getY() <= Math.max(pos1.getY(), pos2.getY())
				&& entityLocation.getZ() <= Math.max(pos1.getZ(), pos2.getZ())) ? true : false;
	}

	/**
	 * @apiNote this can be unused if your arena dosen't need something like bed
	 * @return true when that item exists
	 */
	public static boolean isItemExist(ArenaTeam team, Material material) {
		return (!(getBlockSpawn(team).getBlock().getType() == material)) ? true : false;
	}

	public static boolean isItemExist(JavaPlugin instance, ArenaTeam team, Material material) {
		return (!(getBlockSpawn(instance, team).getBlock().getType() == material)) ? true : false;
	}

}
