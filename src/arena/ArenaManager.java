package arena;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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

	/**
	 * @apiNote this stores players values, first is arena name, second is their
	 *          status and last one is their team
	 */
	public final static Map<String, PlayerData> PLAYERS = new MapMaker().weakKeys().weakValues().makeMap();

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void putInPLAYERS(String key, PlayerData value) {
		PLAYERS.put(key, value);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void removeFromPLAYERS(String key, PlayerData value) {
		PLAYERS.remove(key, value);
	}

	/**
	 * 
	 * @param key
	 */
	public static void PLAYERSRemoveAll(String key) {
		PLAYERS.remove(key);
	}

	/**
	 * @apiNote this stores npcs by their arenaName
	 */
	public final static ListMultimap<String, NPC> NPCS = MultimapBuilder.hashKeys().arrayListValues().build();

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void putInNPCS(String key, NPC value) {
		NPCS.put(key, value);
	}

	/**
	 * 
	 * @param index
	 * @param key
	 * @param value
	 */
	public static void setInNPCS(int index, String key, NPC value) {
		NPCS.get(key).set(index, value);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void removeFromNPCS(String key, NPC value) {
		NPCS.remove(key, value);
	}

	/**
	 * 
	 * @param key
	 */
	public static void NPCSRemoveAll(String key) {
		NPCS.removeAll(key);
	}

	/**
	 * @apiNote this stores generators by their arenaName 0 name 1 location 2
	 *          itemStack
	 */
	public final static ListMultimap<String, Object> GENERATORS = ListMultimapBuilder.hashKeys().arrayListValues()
			.build();

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void putInGENS(String key, Generator value) {
		GENERATORS.put(key, value);
	}

	/**
	 * 
	 * @param index
	 * @param key
	 * @param value
	 */
	public static void setInGENS(int index, String key, Object value) {
		GENERATORS.get(key).set(index, value);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void removeFromGENS(String key, Object value) {
		NPCS.remove(key, value);
	}

	/**
	 * 
	 * @param key
	 */
	public static void GENSRemoveAll(String key) {
		GENERATORS.removeAll(key);
	}

	/**
	 * @apiNote this saves arenas
	 */
	public final static List<Arena> ARENALIST = new ArrayList<>();

	/**
	 * 
	 * @param key
	 */
	public static void addInARENALIST(Arena key) {
		ARENALIST.add(key);
	}

	/**
	 * 
	 * @param index
	 * @param key
	 */
	public static void setInARENALIST(int index, Arena key) {
		ARENALIST.set(index, key);
	}

	/**
	 * 
	 * @param key
	 */
	public static void removeFromARENALIST(Arena key) {
		ARENALIST.remove(key);
	}

	/**
	 * @apiNote this is the arenas directory location
	 */
	public final static String DIR = "plugins/";

	/**
	 * <p>
	 * use createArena() instead
	 * </p>
	 * 
	 * @param minPlayer
	 * @param maxPlayer
	 * @param time
	 * @param waitingSpawn
	 * @param status
	 * @param arenaName
	 * @param worldName
	 * @return
	 */
	@Deprecated
	public static Arena arena(int minPlayer, int maxPlayer, int time, Location waitingSpawn, STATES status,
			String arenaName, String worldName) {
		return new Arena(minPlayer, maxPlayer, time, waitingSpawn, status, arenaName, worldName);
	}

	/**
	 * 
	 * @param team
	 * @param playerName
	 * @param status
	 * @return
	 */
	public static PlayerData data(ArenaTeam team, String playerName, STATES status) {
		return new PlayerData(team, playerName, status);
	}

	/**
	 * <p>
	 * use createTeam() instead
	 * </p>
	 * 
	 * @param arena
	 * @param minNumber
	 * @param maxNumber
	 * @param team
	 * @param blockLocation
	 * @param waitingSpawn
	 * @return
	 */
	@Deprecated
	public static ArenaTeam team(Arena arena, int minNumber, int maxNumber, TEAMS team, Location blockLocation,
			Location waitingSpawn) {
		return new ArenaTeam(arena, minNumber, maxNumber, team, blockLocation, waitingSpawn);
	}

	/**
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
	 * 
	 * @param arenaName
	 * @param type
	 * @param hand
	 * @param uuid
	 * @param data
	 * @param name
	 * @param skinName
	 * @param location
	 * @return
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

	/**
	 * 
	 * @param sender
	 * @param instance
	 * @param arenaName
	 * @param type
	 * @param hand
	 * @param uuid
	 * @param data
	 * @param name
	 * @param skinName
	 * @param location
	 * @return
	 */
	public static NPC addNPC(Plugin instance, String arenaName, EntityType type, Material hand, String uuid,
			String data, String name, String skinName, Location location) {
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
		putInNPCS(arenaName, npc);
		return npc;
	}

	/**
	 * 
	 * @param instance
	 * @param game
	 */
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

	/**
	 * 
	 * @param game
	 */
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

	/**
	 * 
	 * @param arenaName
	 * @throws IOException
	 */
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

	/**
	 * 
	 * @param arenaName
	 * @param minPlayer
	 * @param maxPlayer
	 * @param arenaTime
	 * @param waitingSpawn
	 * @param world
	 * @return
	 */
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
	 * 
	 * @param sender
	 * @param instance
	 * @param arenaName
	 * @param minPlayer
	 * @param maxPlayer
	 * @param arenaTime
	 * @param waitingSpawn
	 * @param world
	 * @return
	 */
	public static Arena createArena(Plugin instance, String arenaName, Integer minPlayer, Integer maxPlayer,
			Integer arenaTime, Location waitingSpawn, String world) {
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
	 * 
	 * @param sender
	 * @param instance
	 * @param playerName
	 * @param team
	 */
	public static void selectTeam(String playerName, TEAMS team) {
		Arena arena = getPlayersArena(playerName);
		ArenaTeam teamm = createTeam(arena, arena.getWorld(), team);
		setPlayerTeam(playerName, teamm);

	}

	/**
	 * 
	 * @param arena
	 * @param world
	 * @param teamm
	 * @return
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

	/**
	 * 
	 * @param arenaName
	 * @param arenaFile
	 * @return
	 */
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

	/**
	 * 
	 * @param clazz
	 * @return
	 */
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

	/**
	 * 
	 * @param class
	 * @return
	 */
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

	/**
	 * 
	 * @param instance
	 * @throws IOException
	 */
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
	 * 
	 * @param arena
	 * @return
	 */
	public static List<Player> getArenasPlayers(Arena arena) {
		return arena.getPlayersNames().stream().map((x) -> Bukkit.getPlayer(x)).collect(Collectors.toList());
	}

	/**
	 * 
	 * @param instance
	 * @param playerName
	 * @return
	 */
	public static Arena getPlayersArena(String playerName) {
		Optional<Arena> arena = ARENALIST.stream()
				.filter((x) -> getPlayersArena(playerName).getPlayersNames().contains(playerName)).findFirst();
		if (arena.isPresent())
			arena.get();

		return null;
	}

	/**
	 * 
	 * @param instance
	 * @param playerName
	 * @return
	 */
	public static ArenaTeam getPlayersTeam(String playerName) {
		ConcurrentLinkedQueue<ArenaTeam> lnk = new ConcurrentLinkedQueue<>();
		Optional<String> player = PLAYERS.keySet().stream().filter((x) -> x.equals(playerName)).findFirst();
		if (player.isPresent()) {
			Optional<Entry<String, PlayerData>> pData = PLAYERS.entrySet().stream()
					.filter((x) -> x.getKey().equals(player.get()) && x.getValue().equals(PLAYERS.get(player.get())))
					.findFirst();
			PlayerData playerData = pData.get().getValue();
			Arena arena = getPlayersArena(playerName);
			ArenaTeam arenaTeam = createTeam(arena, arena.getWorld(), playerData.getTeam().getTeam());
			lnk.add(arenaTeam);
		}
		return lnk.peek();
	}

	/**
	 * 
	 * @param playerName
	 * @param arena
	 * @param status
	 * @param team
	 * @param locationToSpawn
	 * @param check
	 */
	public static void addPlayer(String playerName, Arena arena, STATES status, ArenaTeam team,
			Location locationToSpawn, boolean check) {
		Player player = Bukkit.getPlayer(playerName);
		if (player != null) {
			if (!isPlayerSettedOnce(playerName)) {
				PlayerData data = new PlayerData(team, playerName, status);
				putInPLAYERS(playerName, data);
				arena.getPlayersNames().add(playerName);
			} else {
				Bukkit.getPlayer(playerName).sendMessage(Chati.translate(PropertiesAPI
						.getProperty_C("playerAlreadyInIt", "&c{PLAYER} is already in arena", DIR + "messages.dcnf")
						.replaceAll("{PLAYER}", playerName)));
				Bukkit.getLogger().severe("Player is already exist!");
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

	/**
	 * 
	 * @param arena
	 * @return
	 */
	public static int getArenaIndex(Arena arena) {
		return ARENALIST.indexOf(arena);
	}

	/**
	 * 
	 * @param playerName
	 * @return
	 */
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

	/**
	 * 
	 * @param playerName
	 * @param arena
	 * @param status
	 * @param locationToSpawn
	 */
	public static void randomAddPlayer(String playerName, Arena arena, STATES status, Location locationToSpawn) {
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
			arena.getPlayersNames().add(playerName);
			PlayerData data = new PlayerData(team, playerName, status);
			putInPLAYERS(playerName, data);
		}
		if (locationToSpawn != null && player != null)
			player.teleport(locationToSpawn);
	}

	/**
	 * 
	 * @param playerName
	 * @param arena
	 */
	public static void removePlayer(String playerName, Arena arena) {
		arena.getPlayersNames().remove(playerName);
		PLAYERSRemoveAll(playerName);
	}

	/**
	 * 
	 * @param playerName
	 * @return
	 */
	public static STATES getPlayerStatus(String playerName) {
		Optional<Entry<String, PlayerData>> value = PLAYERS.entrySet().stream()
				.filter((x) -> x.getKey().equals(playerName) && x.getValue() instanceof PlayerData).findFirst();
		if (value.isPresent()) {
			PlayerData data = value.get().getValue();
			return data.getStatus();
		}
		return null;
	}

	public static void setPlayerStatus(String playerName, STATES status) {
		PLAYERS.entrySet().stream().filter((x) -> x.getKey().equals(playerName) && x.getValue() instanceof PlayerData)
				.forEach((x) -> {
					PlayerData oldData = x.getValue();
					oldData.setStatus(status);
					x.setValue(oldData);
				});
	}

	public static void setPlayerData(String playerName, PlayerData data) {
		PLAYERS.entrySet().stream().filter((x) -> x.getKey().equals(playerName) && x.getValue() instanceof PlayerData)
				.forEach((x) -> {
					x.setValue(data);
				});
	}

	/**
	 * 
	 * @param arenaName
	 * @return
	 */
	public static Arena getArenaByName(String arenaName) {
		Optional<Arena> arena = ARENALIST.stream().filter((x) -> x.getName().equals(arenaName)).findFirst();
		if (arena.isPresent()) {
			return arena.get();
		}
		return null;
	}

	/**
	 * 
	 * @param arena
	 * @param team
	 * @return
	 */
	public static ConcurrentSkipListSet<String> getTeamsPlayers(Arena arena, TEAMS team) {
		ConcurrentSkipListSet<String> fls = new ConcurrentSkipListSet<>();
		arena.getPlayersNames().stream().filter((x) -> getPlayersTeam(x).getTeam() == team).forEach((x) -> {
			fls.add(x);
		});
		return fls;
	}

	/**
	 * 
	 * @param playerName
	 * @param team
	 * @return
	 */
	public static Arena getArenaByPlayerAndTeam(String playerName, ArenaTeam team) {
		Optional<Arena> value = ARENALIST.stream()
				.filter((x) -> x.equals(team.getArena()) && x.equals(getPlayersArena(playerName))).findFirst();
		if (value.isPresent()) {
			return value.get();
		}
		return null;
	}

	/**
	 * 
	 * @param playerName
	 * @param arena
	 * @return
	 */
	public static Integer getPlayerIndexInArena(String playerName, Arena arena) {
		Optional<Arena> arenaa = ARENALIST.stream().filter((x) -> x.equals(arena)).findFirst();
		if (arenaa.isPresent()) {
			return ARENALIST.indexOf(arenaa.get());
		}
		return null;
	}

	/**
	 * 
	 * @param arena
	 * @return
	 */
	public static String getArenaWorld(Arena arena) {
		if (arena.getWorld() != null) {
			return arena.getWorld();
		} else {
			return PropertiesAPI.getProperty_C("world", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
		}
	}

	/**
	 * 
	 * @param arena
	 * @param world
	 */
	public static void setArenaWorld(Arena arena, String world) {
		arena.setWorld(world);
		PropertiesAPI.setProperty_NS("world", world, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	/**
	 * 
	 * @param instance
	 * @param arena
	 * @param world
	 */
	public static void setArenaWorld(Plugin instance, Arena arena, String world) {
		arena.setWorld(world);
		PropertiesAPI.setProperty(instance, "world", world, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	/**
	 * 
	 * @param sender
	 * @param instance
	 * @param playerName
	 * @param team
	 */
	public static void setPlayerTeam(String playerName, ArenaTeam team) {
		PlayerData data = new PlayerData(team, playerName, getPlayerStatus(playerName));
		putInPLAYERS(playerName, data);
	}

	/**
	 * 
	 * @param arena
	 * @param location
	 */
	public static void setPos1(Arena arena, Location location) {
		arena.setPos1(location);
		PropertiesAPI.setProperty_NS("pos1",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	/**
	 * 
	 * @param instance
	 * @param arena
	 * @param location
	 */
	public static void setPos1(Plugin instance, Arena arena, Location location) {
		arena.setPos1(location);
		PropertiesAPI.setProperty(instance, "pos1",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	/**
	 * 
	 * @param arena
	 * @param location
	 */
	public static void setPos2(Arena arena, Location location) {
		arena.setPos2(location);
		PropertiesAPI.setProperty_NS("pos2",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	/**
	 * 
	 * @param instance
	 * @param arena
	 * @param location
	 */
	public static void setPos2(Plugin instance, Arena arena, Location location) {
		arena.setPos2(location);
		PropertiesAPI.setProperty(instance, "pos2",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	/**
	 * 
	 * @param arena
	 * @return
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
	 * 
	 * @param arenaName
	 * @param worldName
	 * @return
	 */
	public static Location getPos1(String arenaName, String worldName) {
		String values[] = PropertiesAPI.getProperty_C("pos1", null, DIR + arenaName + "/" + arenaName + ".dcnf")
				.split(",");
		return new Location(Bukkit.getWorld(worldName), Integer.parseInt(values[0]), Integer.parseInt(values[1]),
				Integer.parseInt(values[2]));
	}

	/**
	 * 
	 * @param arena
	 * @return
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
	 * 
	 * @param arenaName
	 * @param worldName
	 * @return
	 */
	public static Location getPos2(String arenaName, String worldName) {
		String values[] = PropertiesAPI.getProperty_C("pos2", null, DIR + arenaName + "/" + arenaName + ".dcnf")
				.split(",");
		return new Location(Bukkit.getWorld(worldName), Integer.parseInt(values[0]), Integer.parseInt(values[1]),
				Integer.parseInt(values[2]));
	}

	/**
	 * 
	 * @param location
	 * @param arenaName
	 * @param generatorName
	 * @param itemName
	 * @param seconds
	 * @param amount
	 */
	public static void setGenerator(Plugin instance, Location location, String arenaName, String generatorName,
			String itemName, String seconds, String amount) {
		PropertiesAPI.setProperties(instance, true, generatorName + "." + itemName,
				DIR + arenaName + "/" + arenaName + ".dcnf",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(), seconds, amount);
	}

	/**
	 * 
	 * @param location
	 * @param arenaName
	 * @param generatorName
	 * @param itemName
	 * @param seconds
	 * @param amount
	 */
	public static void setGenerator(Location location, String arenaName, String generatorName, String itemName,
			String seconds, String amount) {
		PropertiesAPI.setProperties_NS(true, generatorName + "." + itemName,
				DIR + arenaName + "/" + arenaName + ".dcnf",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(), seconds, amount);
	}

	/**
	 * 
	 * @param arenaName
	 * @param generatorName
	 * @param itemName
	 * @return
	 */
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

	/**
	 * 
	 * @param sender
	 * @param playerName
	 * @param arena
	 * @return
	 */
	public static ArenaTeam getTeamByArenaAndPlayer(String playerName, Arena arena) {
		Optional<Arena> aren = ARENALIST.stream().filter((x) -> x.equals(arena) && x.equals(arena)).findFirst();
		if (aren.isPresent()) {
			return getPlayersTeam(playerName);
		}
		return null;
	}

	/**
	 * 
	 * @param team
	 * @param location
	 */
	public static void setTeamSpawn(ArenaTeam team, Location location) {
		team.setTeamSpawn(location);
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".teamspawn",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
	}

	/**
	 * 
	 * @param instance
	 * @param team
	 * @param location
	 */
	public static void setTeamSpawn(Plugin instance, ArenaTeam team, Location location) {
		team.setTeamSpawn(location);
		PropertiesAPI.setProperty(instance, team.getTeam().name() + ".teamspawn",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
	}

	/**
	 * 
	 * @param team
	 * @param world
	 * @return
	 */
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
	 * 
	 * @param team
	 * @param location
	 */
	public static void setBlockSpawn(ArenaTeam team, Location location) {
		team.setBlockSpawn(location);
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".block",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
	}

	/**
	 * 
	 * @param instance
	 * @param team
	 * @param location
	 */
	public static void setBlockSpawn(Plugin instance, ArenaTeam team, Location location) {
		team.setBlockSpawn(location);
		PropertiesAPI.setProperty(instance, team.getTeam().name() + ".block",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
	}

	/**
	 * 
	 * @param team
	 * @return
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

	/**
	 * 
	 * @param arena
	 * @return
	 */
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

	/**
	 * 
	 * @param arenaName
	 * @param worldName
	 * @return
	 */
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

	/**
	 * 
	 * @param arena
	 * @param number
	 */
	public static void setMaxArena(Arena arena, String number) {
		arena.setMaxPlayers(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS("max", number, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	/**
	 * 
	 * @param instance
	 * @param arena
	 * @param number
	 */
	public static void setMaxArena(Plugin instance, Arena arena, String number) {
		arena.setMaxPlayers(Integer.parseInt(number));
		PropertiesAPI.setProperty(instance, "max", number, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	/**
	 * 
	 * @param arena
	 * @return
	 */
	public static Integer getMaxArena(Arena arena) {
		return arena.getMaxPlayer() != null ? arena.getMaxPlayer()
				: Integer.parseInt(PropertiesAPI.getProperty_C("max", null,
						DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
	}

	/**
	 * 
	 * @param arena
	 * @param number
	 */
	public static void setMinArena(Arena arena, String number) {
		arena.setMinPlayers(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS("min", number, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	/**
	 * 
	 * @param instance
	 * @param arena
	 * @param number
	 */
	public static void setMinArena(Plugin instance, Arena arena, String number) {
		arena.setMinPlayers(Integer.parseInt(number));
		PropertiesAPI.setProperty(instance, "min", number, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	/**
	 * 
	 * @param arena
	 * @return
	 */
	public static Integer getMinArena(Arena arena) {
		return arena.getMinPlayer() != null ? arena.getMinPlayer()
				: Integer.parseInt(PropertiesAPI.getProperty_C("min", null,
						DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
	}

	/**
	 * 
	 * @param team
	 * @param number
	 */
	public static void setTeamMax(ArenaTeam team, String number) {
		team.setMaxNumber(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".min", number,
				DIR + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
	}

	/**
	 * 
	 * @param instance
	 * @param team
	 * @param number
	 */
	public static void setTeamMax(Plugin instance, ArenaTeam team, String number) {
		team.setMaxNumber(Integer.parseInt(number));
		PropertiesAPI.setProperty(instance, team.getTeam().name() + ".min", number,
				DIR + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
	}

	/**
	 * 
	 * @param team
	 * @return
	 */
	public static Integer getTeamMax(ArenaTeam team) {
		return team.getMaxNumber() != null ? team.getMaxNumber()
				: Integer.parseInt(PropertiesAPI.getProperty_C(team.getTeam().name() + ".max", null,
						DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf"));
	}

	/**
	 * 
	 * @param team
	 * @param number
	 */
	public static void setTeamMin(ArenaTeam team, String number) {
		team.setMinNumber(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".max", number,
				DIR + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
	}

	/**
	 * 
	 * @param instance
	 * @param team
	 * @param number
	 */
	public static void setTeamMin(Plugin instance, ArenaTeam team, String number) {
		team.setMinNumber(Integer.parseInt(number));
		PropertiesAPI.setProperty(instance, team.getTeam().name() + ".max", number,
				DIR + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
	}

	/**
	 * 
	 * @param team
	 * @return
	 */
	public static Integer getTeamMin(ArenaTeam team) {
		return team.getMinNumber() != null ? team.getMinNumber()
				: Integer.parseInt(PropertiesAPI.getProperty_C(team.getTeam().name() + ".min", null,
						DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf"));
	}

	/**
	 * 
	 * @param arena
	 * @param entityLocation
	 * @return
	 */
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
	 * 
	 * @param arena
	 */
	public static void regenerateBlocks(Arena arena) {
		arena.getBlocks().stream().filter((x) -> isEntityOnRegion(arena, x)).forEach((x) -> {
			arena.getStoredBlocks().stream().filter((y) -> isEntityOnRegion(arena, y)).forEach((y) -> {
				x.getBlock().setType(y.getBlock().getType());
			});
		});
	}

	/**
	 * 
	 * @param arena
	 * @param location
	 */
	public static void saveBlock(Arena arena, Location location) {
		arena.getStoredBlocks().add(location);
	}

	/**
	 * 
	 * @param team
	 * @param material
	 * @return
	 */
	public static boolean isItemExist(ArenaTeam team, Material material) {
		return (!(getBlockSpawn(team).getBlock().getType() == material)) ? true : false;
	}

	/**
	 * 
	 * @param arena
	 * @return
	 */
	public static boolean isArenaFull(Arena arena) {
		return arena.getPlayersNames().size() - 1 == arena.getMaxPlayer();
	}

	/**
	 * 
	 * @param arena
	 * @param location
	 */
	public static void teleportPlayers(Arena arena, Location location) {
		arena.getPlayersNames().stream().forEach((x) -> {
			Bukkit.getPlayer(x).teleport(location);
		});
	}

	/**
	 * 
	 * @param team
	 * @return
	 */
	public static boolean isPlayersGone(ArenaTeam team) {
		return getTeamsPlayers(team.getArena(), team.getTeam()).size() == 0;
	}

	/**
	 * 
	 * @param team
	 * @return
	 */
	public static boolean isBlockGone(ArenaTeam team) {
		return getBlockSpawn(team).getBlock().getType() == Material.AIR;
	}

}
