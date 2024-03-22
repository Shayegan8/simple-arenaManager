package arena;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
import org.bukkit.plugin.Plugin;

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

	public static void putInARENAS(@Nullable CommandSender sender, Plugin instance, Arena key, String value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				ARENAS.put(key, value);
				List<String> players = ARENAS.get(key);
				players.remove(0);
				for (Player player : players.stream().map((x) -> Bukkit.getPlayer(x)).collect(Collectors.toList())) {
					if (sender.hasPermission("arena.putInARENAS")) {
						if (STATES.valueOf(value) != null) {
							String statusMSG = Chati
									.translate(
											PropertiesAPI.getProperty_C("putInARENAS", "&carena status is now " + value,
													ArenaManager.DIR + key.getName() + "/" + key.getName() + ".dcnf"))
									.replaceAll("{STATUS}", value);
							player.sendMessage(statusMSG);
						} else {
							String MSG = Chati
									.translate(PropertiesAPI.getProperty_C("arenaStatus", "&c " + value + " added",
											ArenaManager.DIR + key.getName() + "/" + key.getName() + ".dcnf"))
									.replaceAll("{PLAYER}", value);
							player.sendMessage(MSG);
						}
					}
				}
			});
		}, Executors.newSingleThreadExecutor());

		ARENASException(future, sender, key, value, true, true);
	}

	public static void putInARENAS(Arena key, String value) {
		ARENAS.put(key, value);
	}

	public static void setInARENAS(int index, @Nullable CommandSender sender, Plugin instance, Arena key,
			String value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				ARENAS.get(key).set(index, value);
				List<String> players = ARENAS.get(key);
				players.remove(0);
				for (Player player : players.stream().map((x) -> Bukkit.getPlayer(x)).collect(Collectors.toList())) {
					if (sender.hasPermission("arena.setInARENAS")) {
						if (index == 0) {
							String statusMSG = Chati
									.translate(
											PropertiesAPI.getProperty_C("setInARENAS", "&carena status is now " + value,
													ArenaManager.DIR + key.getName() + "/" + key.getName() + ".dcnf"))
									.replaceAll("{STATUS}", value);
							player.sendMessage(statusMSG);
						} else {
							String MSG = Chati
									.translate(PropertiesAPI.getProperty_C("arenaStatus",
											"&c " + value + " is on " + index,
											ArenaManager.DIR + key.getName() + "/" + key.getName() + ".dcnf"))
									.replaceAll("{PLAYER}", value);
							player.sendMessage(MSG);
						}
					}
				}
			});
		}, Executors.newSingleThreadExecutor());

		ARENASException(future, sender, key, value, true, true);
	}

	public static void setInARENAS(int index, Arena key, String value) {
		ARENAS.get(key).set(index, value);
	}

	public static void removeFromARENAS(@Nullable CommandSender sender, Plugin instance, Arena key, String value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				ARENAS.remove(key, value);
				List<String> players = ARENAS.get(key);
				players.remove(0);
				for (Player player : players.stream().map((x) -> Bukkit.getPlayer(x)).collect(Collectors.toList())) {
					if (sender.hasPermission("arena.removeFromARENAS")) {
						String MSG = Chati
								.translate(PropertiesAPI.getProperty_C("removeFromARENAS",
										"&c " + value + " removed from ARENAS",
										ArenaManager.DIR + key.getName() + "/" + key.getName() + ".dcnf"))
								.replaceAll("{PLAYER}", value);
						player.sendMessage(MSG);
					}
				}
			});
		});
		ARENASException(future, sender, key, value, true, true);
	}

	public static void removeFromARENAS(Arena key, String value) {
		ARENAS.remove(key, value);
	}

	public static void ARENASRemoveAll(@Nullable CommandSender sender, Plugin instance, Arena key) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				ARENAS.removeAll(key);
				List<String> players = ARENAS.get(key);
				players.remove(0);
				for (Player player : players.stream().map((x) -> Bukkit.getPlayer(x)).collect(Collectors.toList())) {
					if (sender.hasPermission("arena.ARENASRemoveAll")) {
						String MSG = Chati
								.translate(PropertiesAPI.getProperty_C("ARENASRemoveAll",
										"&c " + key.getName() + " removed from ARENAS",
										ArenaManager.DIR + key.getName() + "/" + key.getName() + ".dcnf"))
								.replaceAll("{ARENA}", key.getName());
						player.sendMessage(MSG);
					}
				}
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

	public static void putInPLAYERS(@Nullable CommandSender sender, Plugin instance, String key, PlayerData value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				PLAYERS.put(key, value);
				Arena arena = getPlayersArena(sender, key);
				if (sender.hasPermission("arena.putInPLAYERS")) {
					String statusMSG = Chati
							.translate(PropertiesAPI.getProperty_C("putInPLAYERS",
									"&c" + key + " status is now this \n" + value.toString(),
									ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf"))
							.replaceAll("{PLAYER}", key);
					sender.sendMessage(statusMSG);
				}
			});

		}, Executors.newSingleThreadExecutor());

		PLAYERSException(future, sender, key, value, true, true);
	}

	public static void putInPLAYERS(String key, PlayerData value) {
		PLAYERS.put(key, value);
	}

	public static void removeFromPLAYERS(@Nullable CommandSender sender, Plugin instance, String key,
			PlayerData value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				PLAYERS.remove(key, value);
				Arena arena = getPlayersArena(sender, key);
				if (sender.hasPermission("arena.removeFromPlayers")) {
					String statusMSG = Chati
							.translate(PropertiesAPI.getProperty_C("removeFromPlayers",
									"&c" + key + " is removed from PLAYERS",
									DIR + arena.getName() + "/" + arena.getName() + ".dcnf"))
							.replaceAll("{PLAYER}", key);
					sender.sendMessage(statusMSG);
				}
			});
		}, Executors.newSingleThreadExecutor());
		PLAYERSException(future, sender, key, value, true, true);
	}

	public static void removeFromPLAYERS(String key, PlayerData value) {
		PLAYERS.remove(key, value);
	}

	public static void PLAYERSRemoveAll(@Nullable CommandSender sender, Plugin instance, String key) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				PLAYERS.remove(key);
				Arena arena = getPlayersArena(sender, key);
				if (sender.hasPermission("arena.PLAYERSRemoveAll")) {
					String statusMSG = Chati
							.translate(PropertiesAPI.getProperty_C("PLAYERSRemoveAll",
									"&c" + arena.getName() + " is removed from PLAYERS",
									DIR + arena.getName() + "/" + arena.getName() + ".dcnf"))
							.replaceAll("{PLAYER}", key);
					sender.sendMessage(statusMSG);
				}
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

	private static void NPCSException(CompletableFuture<Void> future, CommandSender sender, String key, NPC value,
			boolean checkKey, boolean checkValue) {
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
			if (sender == null) {
				throw new IllegalStateException("sender can't be NULL " + Arrays.toString(exp.getStackTrace()));
			}
			throw new IllegalStateException(Arrays.toString(exp.getStackTrace()));
		});
	}

	public static void putInNPCS(CommandSender sender, Plugin instance, String key, NPC value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				NPCS.put(key, value);
				if (sender.hasPermission("arena.putInNPCS")) {
					String statusMSG = Chati.translate(PropertiesAPI.getProperty_C("putInNPCS",
							"&c" + key + "->" + value.getName(), DIR + key + "/" + key + ".dcnf"))
							.replaceAll("{PLAYER}", key);
					sender.sendMessage(statusMSG);
				}
			});
		}, Executors.newSingleThreadExecutor());

		NPCSException(future, sender, key, value, true, true);
	}

	public static void putInNPCS(String key, NPC value) {
		NPCS.put(key, value);
	}

	public static void setInNPCS(int index, CommandSender sender, Plugin instance, String key, NPC value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				NPCS.get(key).set(index, value);
				if (sender.hasPermission("arena.setInNPCS")) {
					String statusMSG = Chati.translate(PropertiesAPI.getProperty_C("setInNPCS",
							"&c" + key + " " + index + " -> " + value.getName(),
							ArenaManager.DIR + key + "/" + key + ".dcnf")).replaceAll("{PLAYER}", key);
					sender.sendMessage(statusMSG);
				}
			});

		}, Executors.newSingleThreadExecutor());

		NPCSException(future, sender, key, value, true, true);
	}

	public static void setInNPCS(int index, String key, NPC value) {
		NPCS.get(key).set(index, value);
	}

	public static void removeFromNPCS(CommandSender sender, Plugin instance, String key, NPC value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				NPCS.remove(key, value);
				if (sender.hasPermission("arena.removeFromNPCS")) {
					String statusMSG = Chati.translate(PropertiesAPI.getProperty_C("removeFromNPCS",
							"&c" + key + " removed from NPCS", ArenaManager.DIR + key + "/" + key + ".dcnf"))
							.replaceAll("{PLAYER}", key);
					sender.sendMessage(statusMSG);
				}
			});
		}, Executors.newSingleThreadExecutor());

		NPCSException(future, sender, key, value, true, true);
	}

	public static void removeFromNPCS(String key, NPC value) {
		NPCS.remove(key, value);
	}

	public static void NPCSRemoveAll(CommandSender sender, Plugin instance, String key) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				NPCS.removeAll(key);
				if (sender.hasPermission("arena.NPCRemoveAll")) {
					String statusMSG = Chati.translate(PropertiesAPI.getProperty_C("NPCRemoveAll",
							"&cAll npc's removed for " + key, DIR + key + "/" + key + ".dcnf"))
							.replaceAll("{PLAYER}", key);
					sender.sendMessage(statusMSG);
				}
			});
		}, Executors.newSingleThreadExecutor());

		NPCSException(future, sender, key, null, true, false);
	}

	public static void NPCSRemoveAll(String key) {
		NPCS.removeAll(key);
	}

	/**
	 * @apiNote this stores generators by their arenaName 0 name 1 location 2
	 *          itemStack
	 */
	public final static ListMultimap<String, Object> GENERATORS = ListMultimapBuilder.hashKeys().arrayListValues()
			.build();

	private static void GENSException(CompletableFuture<Void> future, CommandSender sender, String key, Generator value,
			boolean checkKey, boolean checkValue) {
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

	public static void putInGENS(CommandSender sender, Plugin instance, String key, String msg, Generator value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				if (sender.hasPermission("arena.putInGENS")) {
					sender.sendMessage(msg);
				}
				GENERATORS.put(key, value);
			});
		}, Executors.newSingleThreadExecutor());

		GENSException(future, sender, key, value, true, true);
	}

	public static void putInGENS(String key, Generator value) {
		GENERATORS.put(key, value);
	}

	public static void setInGENS(int index, CommandSender sender, Plugin instance, String key, String msg,
			Generator value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				if (sender.hasPermission("arena.setInGENS")) {
					sender.sendMessage(msg);
				}
				GENERATORS.get(key).set(index, value);
			});
		}, Executors.newSingleThreadExecutor());

		GENSException(future, sender, key, value, true, true);
	}

	public static void setInGENS(int index, String key, Object value) {
		GENERATORS.get(key).set(index, value);
	}

	public static void removeFromGENS(CommandSender sender, Plugin instance, String key, String msg, Generator value) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				if (sender.hasPermission("arena.removeFromGENS")) {
					sender.sendMessage(msg);
				}
				NPCS.remove(key, value);
			});
		}, Executors.newSingleThreadExecutor());

		GENSException(future, sender, key, value, true, true);
	}

	public static void removeFromGENS(String key, Object value) {
		NPCS.remove(key, value);
	}

	public static void GENSRemoveAll(CommandSender sender, Plugin instance, String key, String msg) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				if (sender.hasPermission("arena.GENSRemoveAll")) {
					sender.sendMessage(msg);
				}
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

	private static void ARENALISTException(CompletableFuture<Void> future, CommandSender sender, Arena key) {
		future.handle((reuslt, exp) -> {
			if (key == null)
				if (sender != null)
					sender.sendMessage("Key can't be NULL");
				else
					throw new IllegalStateException("key can't be NULL " + Arrays.toString(exp.getStackTrace()));
			throw new IllegalStateException(Arrays.toString(exp.getStackTrace()));
		});
	}

	public static void addInARENALIST(CommandSender sender, Plugin instance, Arena key, String msg) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				if (sender.hasPermission("arena.addInARENALIST")) {
					sender.sendMessage(msg);
				}
				ARENALIST.add(key);
			});
		}, Executors.newSingleThreadExecutor());

		ARENALISTException(future, sender, key);
	}

	public static void addInARENALIST(Arena key) {
		ARENALIST.add(key);
	}

	public static void setInARENALIST(int index, CommandSender sender, Plugin instance, Arena key, String msg) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				if (sender.hasPermission("arena.setInARENALIST")) {
					sender.sendMessage(msg);
				}
			});
			ARENALIST.set(index, key);
		}, Executors.newSingleThreadExecutor());

		ARENALISTException(future, sender, key);
	}

	public static void setInARENALIST(int index, Arena key) {
		ARENALIST.set(index, key);
	}

	public static void removeFromARENALIST(CommandSender sender, Plugin instance, Arena key, String msg) {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				if (sender.hasPermission("arena.removeFromARENALIST")) {
					sender.sendMessage(msg);
				}

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

	@Deprecated
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
				.getWorld(PropertiesAPI.getProperty_C("world", null, DIR + arenaName + "/" + arenaName + ".dcnf"))
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

	public static NPC addNPC(@Nullable CommandSender sender, Plugin instance, String arenaName, EntityType type,
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

	public void spawnNPCS(Plugin instance, Arena game) {
		for (Arena arena : ArenaManager.ARENALIST) {
			for (int i = 0; i < ArenaManager.NPCS.get(arena.getName()).size(); i++) {
				NPC npc = ArenaManager.NPCS.get(arena.getName()).get(i);
				if (game.getWorld().equals(npc.getStoredLocation().getWorld().getName())) {
					ConcurrentSkipListSet<String> lsk = PropertiesAPI.getProperties_C(
							game.getName() + "-" + game.getName(), ArenaManager.DIR + "npc.dcnf", "NULL");
					lsk.stream().filter((x) -> lsk.headSet(x).size() == 0).forEach((x) -> {
						if (x != "NULL") {
							String locates[] = x.split(",");
							Location location = new Location(Bukkit.getWorld(game.getWorld()),
									Double.parseDouble(locates[0]), Double.parseDouble(locates[1]),
									Double.parseDouble(locates[2]));
							npc.spawn(location);
						}
					});
				}
			}
		}
	}

	public void spawnNPCS(Arena game) {
		for (Arena arena : ArenaManager.ARENALIST) {
			for (int i = 0; i < ArenaManager.NPCS.get(arena.getName()).size(); i++) {
				NPC npc = ArenaManager.NPCS.get(arena.getName()).get(i);
				if (game.getWorld().equals(npc.getStoredLocation().getWorld().getName())) {
					ConcurrentSkipListSet<String> npcProperties = PropertiesAPI.getProperties_C(
							game.getName() + "-" + game.getName(), ArenaManager.DIR + "npc.dcnf", "NULL");
					String locates[] = npcProperties.first().split(",");
					Location location = new Location(Bukkit.getWorld(game.getWorld()), Double.parseDouble(locates[0]),
							Double.parseDouble(locates[1]), Double.parseDouble(locates[2]));
					npc.spawn(location);
				}
			}
		}
	}

	/**
	 * @apiNote material have to be exist in configuration
	 * @param instance
	 * @param arenaName
	 * @throws IOException
	 */
	public static void loadNPCS(String arenaName) throws IOException {
		ImmutableList<String> ls = ImmutableList.copyOf(Files.readAllLines(Paths.get(DIR + arenaName + "npc.dcnf")));
		ls.stream().forEach((x) -> {
			if (x.contains(arenaName + "-")) {
				ConcurrentSkipListSet<String> lsk = PropertiesAPI.getProperties_C(x, arenaName, "NULL");
				if (!lsk.first().equals("NULL")) {
					Iterator<String> iterator = lsk.iterator();
					EntityType type = null;
					String uuid = null;
					String data = null;
					String name = null;
					String skinName = null;
					if (iterator.hasNext())
						type = EntityType.valueOf(iterator.next());
					Material hand = Material.valueOf(iterator.next());
					if (iterator.hasNext())
						uuid = iterator.next();
					if (iterator.hasNext())
						data = iterator.next();
					if (iterator.hasNext())
						name = iterator.next();
					if (iterator.hasNext())
						skinName = iterator.next();
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
		});
	}

	public static void loadGenerators(String arenaName) throws IOException {
		ImmutableList<String> ls = ImmutableList
				.copyOf(Files.readAllLines(Paths.get(DIR + arenaName + "/generators.dcnf")));
		ls.parallelStream().forEach((x) -> {
			if (x.contains("* ")) {
				Iterator<String> iterator = ls.iterator();
				if (iterator.hasNext()) {

					while (ls.get(ls.indexOf(x)) != iterator.next()) {
						iterator.next();
					}

					String next = iterator.next();
					String aName = (String) next;

					String nexti = iterator.next();
					String middle[] = nexti.split(",");
					Location location = new Location(Bukkit.getWorld(getArenaByName(arenaName).getWorld()),
							Integer.parseInt(middle[0]), Integer.parseInt(middle[1]), Integer.parseInt(middle[2]));
					String nextii = iterator.next();
					ItemStack item = new ItemStack(Material.valueOf(nextii), 1);
					putInGENS(aName, new Generator(arenaName, location, item));
				}
			}
		});
	}

	public static Arena createArena(String arenaName, Integer minPlayer, Integer maxPlayer, Integer arenaTime,
			Location waitingSpawn, String world) {
		String arenaDir = DIR + arenaName;
		String arenaFile = DIR + arenaName + "/" + arenaName + ".dcnf";
		try {
			if (Files.notExists(Paths.get(arenaDir)))
				Files.createDirectory(Paths.get(arenaDir));
			if (Files.notExists(Paths.get(arenaFile)))
				Files.createFile(Paths.get(arenaFile));

		} catch (IOException e) {
			e.printStackTrace();
		}

		int min;

		if (minPlayer != null) {
			min = minPlayer;
			PropertiesAPI.setProperty_NS("minPlayers", String.valueOf(min), arenaFile);
		} else {
			min = Integer.parseInt(PropertiesAPI.getProperty_C("minPlayers", "2", arenaFile));
		}

		int max;

		if (maxPlayer != null) {
			max = maxPlayer;
			PropertiesAPI.setProperty_NS("maxPlayers", String.valueOf(max), arenaFile);
		} else {
			max = Integer.parseInt(PropertiesAPI.getProperty_C("maxPlayers", "8", arenaFile));
		}

		int time;

		if (arenaTime != null) {
			time = arenaTime;
			PropertiesAPI.setProperty_NS("arenaTime", String.valueOf(min), arenaFile);
		} else {
			time = Integer.parseInt(PropertiesAPI.getProperty_C("arenaTime", "1800", arenaFile));
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
		return arena;
	}

	/**
	 * @apiNote this is asynchronously function
	 *          <p>
	 *          it creates a arena with given parameters
	 *          </p>
	 */
	public static Arena createArena(@Nullable CommandSender sender, Plugin instance, String arenaName,
			Integer minPlayer, Integer maxPlayer, Integer arenaTime, Location waitingSpawn, String world) {
		final String arenaDir = DIR + arenaName;
		final String arenaFile = DIR + arenaName + "/" + arenaName + ".dcnf";
		String firstValue = null;
		String secondValue = null;
		String thirdValue = null;
		Location locationC = null;
		Bukkit.getScheduler().runTask(instance, () -> {
			try {
				if (Files.notExists(Paths.get(arenaDir)))
					Files.createDirectory(Paths.get(arenaDir));
				if (Files.notExists(Paths.get(arenaFile)))
					Files.createFile(Paths.get(arenaFile));

			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		if (minPlayer != null) {
			PropertiesAPI.setProperty(instance, "minPlayers", String.valueOf(minPlayer), arenaFile);
		}
		String minC = PropertiesAPI.getProperty_C("minPlayers", "2", arenaFile);

		if (maxPlayer != null) {
			PropertiesAPI.setProperty(instance, "maxPlayers", String.valueOf(maxPlayer), arenaFile);
		}
		String maxC = PropertiesAPI.getProperty_C("maxPlayers", "8", arenaFile);

		if (arenaTime != null) {
			PropertiesAPI.setProperty(instance, "arenaTime", String.valueOf(arenaTime), arenaFile);
		}
		String timeC = PropertiesAPI.getProperty_C("arenaTime", "1800", arenaFile);

		ConcurrentSkipListSet<String> waitingC = PropertiesAPI.getProperties_C("waitingSpawn", arenaFile, "0", "0",
				"0");
		if (waitingSpawn != null) {
			PropertiesAPI.setProperties(instance, true, "waitingSpawn", String.valueOf(waitingSpawn.getBlockX()),
					String.valueOf(waitingSpawn.getBlockY()), String.valueOf(waitingSpawn.getBlockZ()));
		}
		Iterator<String> iterator = waitingC.iterator();
		while (iterator.hasNext()) {
			if (firstValue == null) {
				firstValue = iterator.next();
			}
			if (firstValue != null) {
				String saved = iterator.next();
				if (!firstValue.equals(saved)) {
					secondValue = saved;
				}
			}

			if (secondValue != null) {
				String saved = iterator.next();
				if (!secondValue.equals(saved)) {
					secondValue = saved;
				}
			}

		}
		locationC = new Location(Bukkit.getWorld(world), Integer.parseInt(firstValue), Integer.parseInt(secondValue),
				Integer.parseInt(thirdValue));
		Arena arena = new Arena(Integer.parseInt(minC), Integer.parseInt(maxC), Integer.parseInt(timeC), locationC,
				STATES.INPROCESS, arenaName, world);
		addInARENALIST(arena);
		return arena;
	}

	/**
	 * <p>
	 * its almost same as setPlayerTeam but it checks if the player is valid in your
	 * arena
	 * </p>
	 */
	public static void selectTeam(@Nullable CommandSender sender, Plugin instance, String playerName, TEAMS team) {
		Arena arena = getPlayersArena(sender, instance, playerName);
		ArenaTeam teamm = createTeam(arena, arena.getWorld(), team);
		ARENAS.entries().stream().filter((x) -> x.getKey().equals(arena) && ARENAS.get(arena).contains(playerName))
				.forEach((unused) -> {
					setPlayerTeam(sender, instance, playerName, teamm);
				});
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

		int min = Integer.parseInt(PropertiesAPI.getProperty_C(teamm.name() + ".min", "1",
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
		int max = Integer.parseInt(PropertiesAPI.getProperty_C(teamm.name() + "max", "2",
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));

		String coordinates[] = PropertiesAPI
				.getProperty_C(teamm.name() + ".block", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf")
				.split(",");
		Location bed = new Location(Bukkit.getWorld(world), Double.parseDouble(coordinates[0]),
				Double.parseDouble(coordinates[1]), Double.parseDouble(coordinates[2]));
		String ncoordinates[] = PropertiesAPI.getProperty_C(teamm.name() + ".teamspawn", null,
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf").split(",");
		Location spawn = new Location(Bukkit.getWorld(world), Double.parseDouble(ncoordinates[0]),
				Double.parseDouble(ncoordinates[1]), Double.parseDouble(ncoordinates[2]));

		return new ArenaTeam(arena, min, max, teamm, bed, spawn);
	}

	public static Arena loadArena(String arenaName, String arenaFile) {
		int min = Integer.parseInt(PropertiesAPI.getProperty_C("min", "2", arenaFile));
		int max = Integer.parseInt(PropertiesAPI.getProperty_C("max", "8", arenaFile));
		int time = Integer.parseInt(PropertiesAPI.getProperty_C("arenaTime", "1800", arenaFile));

		String locationCoordinates[] = PropertiesAPI.getProperty_C("waitingSpawn", null, arenaFile).split(",");

		Location location = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", null, arenaFile)),
				Double.parseDouble(locationCoordinates[0]), Double.parseDouble(locationCoordinates[1]),
				Double.parseDouble(locationCoordinates[2]));

		String nlocationCoordinates[] = PropertiesAPI.getProperty_C("pos1", null, arenaFile).split(",");

		Location pos1 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", arenaFile, null)),
				Double.parseDouble(nlocationCoordinates[0]), Double.parseDouble(nlocationCoordinates[1]),
				Double.parseDouble(nlocationCoordinates[2]));

		String nnlocationCoordinates[] = PropertiesAPI.getProperty_C("pos2", null, arenaFile).split(",");

		Location pos2 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", null, arenaFile)),
				Double.parseDouble(nnlocationCoordinates[0]), Double.parseDouble(nnlocationCoordinates[1]),
				Double.parseDouble(nnlocationCoordinates[2]));

		Arena arena = new Arena(min, max, time, location, STATES.WAITING, arenaName,
				PropertiesAPI.getProperty_C("world", null, arenaFile), pos1, pos2);
		addInARENALIST(arena);
		return arena;
	}

	public static List<Arena> loadArenasByAnnotation(Class<?> clazz) {
		ArenaMaker maker = clazz.getAnnotation(ArenaMaker.class);
		List<String> files = Arrays.asList(maker.arenas());
		files.stream().forEach((x) -> {
			String arenaFile = DIR + x + "/" + x + ".dcnf";

			int min = Integer.parseInt(PropertiesAPI.getProperty_C("min", "2", arenaFile));
			int max = Integer.parseInt(PropertiesAPI.getProperty_C("max", "8", arenaFile));
			int time = Integer.parseInt(PropertiesAPI.getProperty_C("arenaTime", "1800", arenaFile));

			String locationCoordinates[] = PropertiesAPI.getProperty_C("waitingSpawn", null, arenaFile).split(",");

			Location location = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", null, arenaFile)),
					Double.parseDouble(locationCoordinates[0]), Double.parseDouble(locationCoordinates[1]),
					Double.parseDouble(locationCoordinates[2]));

			String nlocationCoordinates[] = PropertiesAPI.getProperty_C("pos1", null, arenaFile).split(",");

			Location pos1 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", arenaFile, null)),
					Double.parseDouble(nlocationCoordinates[0]), Double.parseDouble(nlocationCoordinates[1]),
					Double.parseDouble(nlocationCoordinates[2]));

			String nnlocationCoordinates[] = PropertiesAPI.getProperty_C("pos2", null, arenaFile).split(",");

			Location pos2 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", null, arenaFile)),
					Double.parseDouble(nnlocationCoordinates[0]), Double.parseDouble(nnlocationCoordinates[1]),
					Double.parseDouble(nnlocationCoordinates[2]));

			Arena arena = new Arena(min, max, time, location, STATES.WAITING, x,
					PropertiesAPI.getProperty_C("world", null, arenaFile), pos1, pos2);

			addInARENALIST(arena);
		});
		return ARENALIST;
	}

	public static ConcurrentSkipListSet<Arena> concurrentLoadArenasByAnnotation(Class<?> clazz) {
		ArenaMaker maker = clazz.getAnnotation(ArenaMaker.class);
		ConcurrentSkipListSet<Arena> lsk = new ConcurrentSkipListSet<>();
		List<String> files = Arrays.asList(maker.arenas());
		files.stream().forEach((x) -> {
			String arenaFile = DIR + x + "/" + x + ".dcnf";

			int min = Integer.parseInt(PropertiesAPI.getProperty_C("min", "2", arenaFile));
			int max = Integer.parseInt(PropertiesAPI.getProperty_C("max", "8", arenaFile));
			int time = Integer.parseInt(PropertiesAPI.getProperty_C("arenaTime", "1800", arenaFile));

			String locationCoordinates[] = PropertiesAPI.getProperty_C("waitingSpawn", null, arenaFile).split(",");

			Location location = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", null, arenaFile)),
					Double.parseDouble(locationCoordinates[0]), Double.parseDouble(locationCoordinates[1]),
					Double.parseDouble(locationCoordinates[2]));

			String nlocationCoordinates[] = PropertiesAPI.getProperty_C("pos1", null, arenaFile).split(",");

			Location pos1 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", arenaFile, null)),
					Double.parseDouble(nlocationCoordinates[0]), Double.parseDouble(nlocationCoordinates[1]),
					Double.parseDouble(nlocationCoordinates[2]));

			String nnlocationCoordinates[] = PropertiesAPI.getProperty_C("pos2", null, arenaFile).split(",");

			Location pos2 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", null, arenaFile)),
					Double.parseDouble(nnlocationCoordinates[0]), Double.parseDouble(nnlocationCoordinates[1]),
					Double.parseDouble(nnlocationCoordinates[2]));

			Arena arena = new Arena(min, max, time, location, STATES.WAITING, x,
					PropertiesAPI.getProperty_C("world", null, arenaFile), pos1, pos2);

			lsk.add(arena);
		});
		return lsk;
	}

	public static void loadArenas() {
		List<Path> dirs = null;
		try {
			dirs = Files.walk(Paths.get(DIR)).filter(Files::isDirectory).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		dirs.remove(0);

		for (Path dir : dirs) {
			String arenaName = null;
			Optional<String> k = dirs.stream().filter((x) -> x.equals(dir))
					.filter((x) -> x.toString().split("//")[1].contains(".dcnf"))
					.map((x) -> x.toString().split("//")[1]).findFirst();
			if (k.isPresent()) {
				arenaName = k.get();
			}

			String arenaFile = DIR + dir + "/" + dir + ".dcnf";

			String min = PropertiesAPI.getProperty_C("min", "2", arenaFile);
			String max = PropertiesAPI.getProperty_C("max", "8", arenaFile);
			String time = PropertiesAPI.getProperty_C("arenaTime", "1800", arenaFile);

			String locationCoordinates[] = PropertiesAPI.getProperty_C("waitingSpawn", null, arenaFile).split(",");

			Location location = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", null, arenaFile)),
					Double.parseDouble(locationCoordinates[0]), Double.parseDouble(locationCoordinates[1]),
					Double.parseDouble(locationCoordinates[2]));

			String nlocationCoordinates[] = PropertiesAPI.getProperty_C("pos1", null, arenaFile).split(",");

			Location pos1 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", arenaFile, null)),
					Double.parseDouble(nlocationCoordinates[0]), Double.parseDouble(nlocationCoordinates[1]),
					Double.parseDouble(nlocationCoordinates[2]));

			String nnlocationCoordinates[] = PropertiesAPI.getProperty_C("pos2", null, arenaFile).split(",");

			Location pos2 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", null, arenaFile)),
					Double.parseDouble(nnlocationCoordinates[0]), Double.parseDouble(nnlocationCoordinates[1]),
					Double.parseDouble(nnlocationCoordinates[2]));

			String world = PropertiesAPI.getProperty_C("world", null, arenaFile);

			try {
				loadGenerators(arenaName);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				loadNPCS(arenaName);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (pos2 == null || pos1 == null || location == null || world == null)
				throw new IllegalStateException("Some of values or null for " + arenaName);
			Arena arena = new Arena(Integer.parseInt(min), Integer.parseInt(max), Integer.parseInt(time), location,
					STATES.WAITING, arenaName, world, pos1, pos2);
			addInARENALIST(arena);
		}
	}

	public static void loadArenas(Plugin instance) throws IOException {
		ConcurrentLinkedQueue<Path> dirs = new ConcurrentLinkedQueue<>(
				Files.walk(Paths.get(DIR)).filter(Files::isDirectory).collect(Collectors.toList()));

		dirs.poll();

		for (Path dir : dirs) {
			String arenaName = null;
			Optional<String> k = dirs.stream().filter((x) -> x.equals(dir))
					.filter((x) -> x.toString().split("//")[1].contains(".dcnf"))
					.map((x) -> x.toString().split("//")[1]).findFirst();
			if (k.isPresent()) {
				arenaName = k.get();
			}

			String arenaFile = DIR + dir + "/" + dir + ".dcnf";

			String min = PropertiesAPI.getProperty_C("min", "2", arenaFile);
			String max = PropertiesAPI.getProperty_C("max", "8", arenaFile);
			String time = PropertiesAPI.getProperty_C("arenaTime", "1800", arenaFile);

			String locationCoordinates[] = PropertiesAPI.getProperty_C("waitingSpawn", null, arenaFile).split(",");

			Location location = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", null, arenaFile)),
					Double.parseDouble(locationCoordinates[0]), Double.parseDouble(locationCoordinates[1]),
					Double.parseDouble(locationCoordinates[2]));

			String nlocationCoordinates[] = PropertiesAPI.getProperty_C("pos1", null, arenaFile).split(",");

			Location pos1 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", arenaFile, null)),
					Double.parseDouble(nlocationCoordinates[0]), Double.parseDouble(nlocationCoordinates[1]),
					Double.parseDouble(nlocationCoordinates[2]));

			String nnlocationCoordinates[] = PropertiesAPI.getProperty_C("pos2", null, arenaFile).split(",");

			Location pos2 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty_C("world", null, arenaFile)),
					Double.parseDouble(nnlocationCoordinates[0]), Double.parseDouble(nnlocationCoordinates[1]),
					Double.parseDouble(nnlocationCoordinates[2]));

			String world = PropertiesAPI.getProperty_C("world", null, arenaFile);

			try {
				loadGenerators(arenaName);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				loadNPCS(arenaName);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (pos2 == null || pos1 == null || location == null || world == null)
				throw new IllegalStateException("Some of values or null for " + arenaName);
			Arena arena = new Arena(Integer.parseInt(min), Integer.parseInt(max), Integer.parseInt(time), location,
					STATES.WAITING, arenaName, world, pos1, pos2);
			addInARENALIST(arena);
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

	public static Arena getPlayersArena(@Nullable CommandSender sender, Plugin instance, String playerName) {
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

	public static ArenaTeam getPlayersTeam(@Nullable CommandSender sender, Plugin instance, String playerName) {
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
			String property = PropertiesAPI.getProperty_C("selectTeamItem", "COMPASS",
					DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
			ItemStack item = new ItemStack(Material.valueOf(property), 1);
			player.getInventory().setItem(40, item);
		}
	}

	public static void addPlayer(@Nullable CommandSender sender, Plugin instance, String playerName, Arena arena,
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

	public static void randomAddPlayer(@Nullable CommandSender sender, Plugin instance, String playerName, Arena arena,
			STATES status, Location locationToSpawn, boolean check) {
		Random rand = new Random();
		int random = rand.nextInt(TEAMS.values().length);
		TEAMS teamm = TEAMS.values()[random];

		String coordinates[] = PropertiesAPI
				.getProperty_C(teamm.name() + ".block", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf")
				.split(",");
		Location bed = new Location(Bukkit.getWorld(arena.getWorld()), Double.parseDouble(coordinates[0]),
				Double.parseDouble(coordinates[1]), Double.parseDouble(coordinates[2]));
		String ncoordinates[] = PropertiesAPI.getProperty_C(teamm.name() + ".teamspawn", null,
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf").split(",");
		Location spawn = new Location(Bukkit.getWorld(arena.getWorld()), Double.parseDouble(ncoordinates[0]),
				Double.parseDouble(ncoordinates[1]), Double.parseDouble(ncoordinates[2]));
		ArenaTeam team = new ArenaTeam(arena,
				Integer.parseInt(
						PropertiesAPI.getProperty_C(teamm.name() + ".min", "1", DIR + "/" + arena.getName() + ".dcnf")),
				Integer.parseInt(
						PropertiesAPI.getProperty_C(teamm.name() + ".max", "2", DIR + "/" + arena.getName() + ".dcnf")),
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

	public static void removePlayer(@Nullable CommandSender sender, Plugin instance, String playerName, Arena arena) {
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

	public static void setArenaStatus(@Nullable CommandSender sender, Plugin instance, Arena arena, STATES status) {
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

	public static ConcurrentSkipListSet<String> getTeamsPlayers(@Nullable CommandSender sender, Plugin instance,
			Arena arena, TEAMS team) {
		ConcurrentSkipListSet<String> fls = new ConcurrentSkipListSet<>();
		arena.getPlayersNames().stream().filter((x) -> getPlayersTeam(sender, instance, x).getTeam() == team)
				.forEach((x) -> {
					fls.add(x);
				});
		return fls;
	}

	public static Arena getArenaByPlayerAndTeam(@Nullable CommandSender sender, Plugin instance, String playerName,
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

	public static void setArenaWorld(Plugin instance, Arena arena, String world) {
		arena.setWorld(world);
		PropertiesAPI.setProperty(instance, "world", world, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static void setPlayerTeam(String playerName, ArenaTeam team) {
		PlayerData data = new PlayerData(team, playerName, getPlayerStatus(playerName));
		putInPLAYERS(playerName, data);
	}

	public static void setPlayerTeam(@Nullable CommandSender sender, Plugin instance, String playerName,
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

	public static void setPos1(Plugin instance, Arena arena, Location location) {
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

	public static void setPos2(Plugin instance, Arena arena, Location location) {
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

	public static Location getGeneratorLocation(String arenaName, String generatorName, String itemName) {
		String values[] = new String[3];
		PropertiesAPI
				.getProperties_C(generatorName + "." + itemName, DIR + arenaName + "/" + arenaName + ".dcnf", "NULL")
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

	public static ArenaTeam getTeamByArenaAndPlayer(@Nullable CommandSender sender, Plugin instance, String playerName,
			Arena arena) {
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

	public static void setTeamSpawn(Plugin instance, ArenaTeam team, Location location) {
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

	public static void setBlockSpawn(Plugin instance, ArenaTeam team, Location location) {
		team.setBlockSpawn(location);
		PropertiesAPI.setProperty(instance, team.getTeam().name() + ".block",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
	}

	/**
	 * @return the location of block of the arenaTeam
	 */
	public static Location getBlockSpawn(ArenaTeam team) {
		if (team.getBlockSpawn() != null) {
			return team.getBlockSpawn();
		} else {
			String ls[] = new String[3];
			PropertiesAPI
					.getProperties_C(team.getTeam().name() + ".block",
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

	public static Location getWaitingSpawn(Arena arena) {
		if (arena.getWaitingSpawn() != null) {
			return arena.getWaitingSpawn();
		} else {
			String ls[] = new String[3];
			PropertiesAPI.getProperties_C("waiting", DIR + arena.getName() + "/" + arena.getName() + ".dcnf", "NULL")
					.stream().map((x) -> x.split("-\\s*", 2)[1].split(",")).forEach((x) -> {
						ls[0] = x[0];
						ls[1] = x[1];
						ls[2] = x[2];
					});
			return new Location(Bukkit.getWorld(arena.getWorld()), Integer.parseInt(ls[0]), Integer.parseInt(ls[1]),
					Integer.parseInt(ls[2]));
		}
	}

	public static Location getWaitingSpawn(String arenaName, String worldName) {

		String ls[] = new String[3];
		PropertiesAPI.getProperties_C("waiting", DIR + arenaName + "/" + arenaName + ".dcnf", "NULL").stream()
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

	public static void setMaxArena(Plugin instance, Arena arena, String number) {
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

	public static void setMinArena(Plugin instance, Arena arena, String number) {
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

	public static void setTeamMax(Plugin instance, ArenaTeam team, String number) {
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

	public static void setTeamMin(Plugin instance, ArenaTeam team, String number) {
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

	public static boolean isItemExist(ArenaTeam team, Material material) {
		return (!(getBlockSpawn(team).getBlock().getType() == material)) ? true : false;
	}

	public static boolean isArenaFull(Arena arena) {
		Optional<Entry<Arena, String>> aren = ARENAS.entries().stream()
				.filter((x) -> x.getKey().equals(arena) && x.getKey().getMaxPlayer() == ARENAS.get(arena).size() - 1)
				.findFirst();
		if (aren.isPresent())
			if (aren.get().getKey() != null)
				return true;
		return false;
	}

}
