package arena;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;

import inventory.EMaterial;
import inventory.InventoryAPI;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import npc.NPC;

/**
 * @author shayegan8
 */
public class ArenaManager {

	public static final ConcurrentLinkedQueue<Inventory> INVS = new ConcurrentLinkedQueue<>();

	/**
	 * 
	 * @param inv
	 */
	public static void addInINVS(Inventory inv) {
		INVS.add(inv);
	}

	/**
	 * 
	 * @param inv
	 */
	public static void removeFromINVS(Inventory inv) {
		INVS.remove(inv);
	}

	public static final ConcurrentLinkedQueue<EMaterial> ITEMS = new ConcurrentLinkedQueue<>();

	/**
	 * 
	 * @param material
	 */
	public static void addInITEMS(EMaterial material) {
		ITEMS.add(material);
	}

	/**
	 * 
	 * @param material
	 */
	public static void removeFromITEMS(EMaterial material) {
		ITEMS.remove(material);
	}

	public static final ConcurrentHashMap<ArenaTeam, NPC> SNPCS = new ConcurrentHashMap<>();

	/**
	 * 
	 * @param team
	 * @param npc
	 */
	public static void putInSNPCS(ArenaTeam team, NPC npc) {
		SNPCS.put(team, npc);
	}

	/**
	 * 
	 * @param tea
	 * @param npc
	 */
	public static void removeFromSNPCS(ArenaTeam tea, NPC npc) {
		SNPCS.remove(tea, npc);
	}

	/**
	 * 
	 * @param team
	 */
	public static void SNPCSRemoveAll(ArenaTeam team) {
		SNPCS.remove(team);
	}

	/**
	 * <p>
	 * this stores players values, first is arena name, second is their status and
	 * last one is their team
	 * </p>
	 */
	public final static ConcurrentHashMap<String, PlayerData> PLAYERS = new ConcurrentHashMap<>();

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
	 * <p>
	 * this stores generators by their names
	 * </p>
	 */
	public final static ListMultimap<String, Generator> GENERATORS = ListMultimapBuilder.hashKeys().arrayListValues()
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
	public static void setInGENS(int index, String key, Generator value) {
		GENERATORS.get(key).set(index, value);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void removeFromGENS(String key, Object value) {
		GENERATORS.remove(key, value);
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
	public final static ConcurrentLinkedQueue<Arena> ARENALIST = new ConcurrentLinkedQueue<>();

	/**
	 * 
	 * @param key
	 */
	public static void addInARENALIST(Arena key) {
		ARENALIST.add(key);
	}

	/**
	 * 
	 * @param key
	 */
	public static void removeFromARENALIST(Arena key) {
		ARENALIST.remove(key);
	}

	/**
	 * <p>
	 * this is the arenas directory location
	 * </p>
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

	private static void doShopItems(String arenaName) throws IOException {

		ConcurrentLinkedQueue<EMaterial> shopItems = new ConcurrentLinkedQueue<>();
		PropertiesAPI.reader(new String(DIR + arenaName + "/shop.dcnf").toCharArray()).filter((x) -> x.contains(":"))
				.forEach((x) -> {
					String splite[] = x.split(":");
					String item = splite[0];
					String name = Chati.translate(splite[1]);
					String page = splite[2];
					int amount = Integer.parseInt(splite[3]);
					EMaterial material = new EMaterial(amount, amount, arenaName, Material.valueOf(item));
					if (!page.equals("NULL"))
						material.setPage(page);
					material.setName(name);
					ArenaManager.addInITEMS(material);
					shopItems.add(material);

				});
		InventoryAPI shopINV = new InventoryAPI(
				Integer.parseInt(PropertiesAPI.getProperty("size", "53", DIR + arenaName + "/shop.dcnf")), arenaName);
		shopItems.stream().forEach((x) -> {
			ItemStack item = new ItemStack(x.getMaterial(), x.getAmount());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(x.getName());
			meta.setLore(Arrays.asList(""));
			shopINV.setItem(x.getIndex(), item, meta);
		});
		addInINVS(shopINV.getInv());
	}

	private static void doTeam(String arenaName) throws IOException {

		ConcurrentLinkedQueue<EMaterial> teamselect = new ConcurrentLinkedQueue<>();
		PropertiesAPI.reader(new String(DIR + arenaName + "/teamselect.dcnf").toCharArray())
				.filter((x) -> x.contains(":")).forEach((x) -> {
					String splite[] = x.split(":");
					String item = splite[0];
					String name = Chati.translate(splite[1]);
					String page = splite[2];
					int amount = Integer.parseInt(splite[3]);
					EMaterial material = new EMaterial(amount, amount, arenaName, Material.valueOf(item));
					if (!page.equals("NULL"))
						material.setPage(page);
					material.setName(name);
					ArenaManager.addInITEMS(material);
				});
		InventoryAPI teamselectINV = new InventoryAPI(
				Integer.parseInt(PropertiesAPI.getProperty("size", "53", DIR + arenaName + "/teamselect.dcnf")),
				arenaName);
		teamselect.stream().forEach((x) -> {
			ItemStack item = new ItemStack(x.getMaterial(), x.getAmount());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(x.getName());
			meta.setLore(Arrays.asList(""));
			teamselectINV.setItem(x.getIndex(), item, meta);
		});
		addInINVS(teamselectINV.getInv());
	}

	public static void doArmory(String arenaName) throws IOException {
		ConcurrentLinkedQueue<EMaterial> armory = new ConcurrentLinkedQueue<>();
		PropertiesAPI.reader(new String(DIR + arenaName + "/armory.dcnf").toCharArray()).filter((x) -> x.contains(":"))
				.forEach((x) -> {
					String splite[] = x.split(":");
					String item = splite[0];
					String name = Chati.translate(splite[1]);
					String page = splite[2];
					int amount = Integer.parseInt(splite[3]);
					EMaterial material = new EMaterial(amount, amount, arenaName, Material.valueOf(item));
					material.setName(name);
					if (!page.equals("NULL"))
						material.setPage(page);
					ArenaManager.addInITEMS(material);
				});
		InventoryAPI armoryINV = new InventoryAPI(
				Integer.parseInt(PropertiesAPI.getProperty("size", "53", DIR + arenaName + "/armory.dcnf")), arenaName);
		armory.stream().forEach((x) -> {
			ItemStack item = new ItemStack(x.getMaterial(), x.getAmount());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(x.getName());
			meta.setLore(Arrays.asList(""));
			armoryINV.setItem(x.getIndex(), item, meta);
		});
		addInINVS(armoryINV.getInv());
	}

	public static void doPotions(String arenaName) throws IOException {
		ConcurrentLinkedQueue<EMaterial> potions = new ConcurrentLinkedQueue<>();
		PropertiesAPI.reader(new String(DIR + arenaName + "/potions.dcnf").toCharArray()).filter((x) -> x.contains(":"))
				.forEach((x) -> {
					String splite[] = x.split(":");
					String item = splite[0];
					String name = Chati.translate(splite[1]);
					String page = splite[2];
					int amount = Integer.parseInt(splite[3]);
					EMaterial material = new EMaterial(amount, amount, arenaName, Material.valueOf(item));
					if (!page.equals("NULL"))
						material.setPage(page);
					material.setName(name);
					ArenaManager.addInITEMS(material);
				});

		InventoryAPI potionsINV = new InventoryAPI(
				Integer.parseInt(PropertiesAPI.getProperty("size", "53", DIR + arenaName + "/potions.dcnf")),
				arenaName);
		potions.stream().forEach((x) -> {
			ItemStack item = new ItemStack(x.getMaterial(), x.getAmount());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(x.getName());
			meta.setLore(Arrays.asList(""));
			potionsINV.setItem(x.getIndex(), item, meta);
		});
		addInINVS(potionsINV.getInv());
	}

	public static void loadItems(String arenaName) {
		try {
			doShopItems(arenaName);
			doTeam(arenaName);
			doArmory(arenaName);
			doPotions(arenaName);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			Location npc, Location waitingSpawn) {
		return new ArenaTeam(arena, minNumber, maxNumber, team, blockLocation, npc, waitingSpawn);
	}

	/**
	 * 
	 * @return the entityPlayer if there is no entityPlayer there
	 */
	public static EntityPlayer getEntityPlayer(Location location, Entity entity, String arenaName) {
		for (Entity e : Bukkit
				.getWorld(PropertiesAPI.getProperty("world", null, DIR + arenaName + "/" + arenaName + ".dcnf"))
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

	public static Location getNPCLocation(String arenaName, TEAMS team) {

		String lsk[] = PropertiesAPI.getProperty(arenaName + "." + team.name() != null ? team.name() : null, null,
				DIR + arenaName + "/npcs.dcnf").split(",");
		return new Location(Bukkit.getWorld(ArenaManager.getArenaByName(arenaName).getName()), Integer.parseInt(lsk[0]),
				Integer.parseInt(lsk[1]), Integer.parseInt(lsk[2]));
	}

	public static ArenaTeam setNPCLocation(Plugin instance, String arenaName, TEAMS team, Location location) {
		try {
			PropertiesAPI.setProperty(instance, arenaName + "." + team.name() != null ? team.name() : null,
					location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
					DIR + arenaName + "/npcs.dcnf");
			ArenaTeam teamm = getTeamByArenaAndName(getArenaByName(arenaName), team);
			return teamm;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static EntityType setNPCType(Plugin instance, String arenaName, TEAMS team, EntityType type) {
		try {
			PropertiesAPI.setProperty(instance, arenaName + "." + team.name() != null ? team.name() : null, type.name(),
					DIR + arenaName + "/npcs.dcnf");
			return type;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addNPC(Plugin instance, String arenaName, Location location, TEAMS team, EntityType type) {
		ArenaTeam teamm = setNPCLocation(instance, arenaName, team, location);
		EntityType typee = setNPCType(instance, arenaName, team, type);
		putInSNPCS(teamm, new NPC(teamm, typee, location));
	}

	public static void loadNPCS(String arenaName) throws IOException {
		final ConcurrentLinkedQueue<String> lnk = new ConcurrentLinkedQueue<>(
				Files.readAllLines(Paths.get(DIR + arenaName + "/npcs.dcnf")));
		Iterator<String> iterate = lnk.iterator();
		lnk.stream().forEach((x -> {
			String next = null;
			if (x.contains(arenaName)) {
				while (iterate.hasNext()) {
					next = iterate.next();
					if (next.equals(x))
						break;
				}
			}
			String property[] = PropertiesAPI.getProperty(next, null, DIR + arenaName + "/npcs.dcnf").split(",");
			Location loc = new Location(Bukkit.getWorld(getArenaByName(arenaName).getWorld()),
					Integer.parseInt(property[0]), Integer.parseInt(property[1]), Integer.parseInt(property[2]));
			String propertyy[] = next.split(".");

			ArenaTeam teamm = getTeamByArenaAndName(getArenaByName(propertyy[0]), TEAMS.valueOf(propertyy[1]));

			NPC npc = new NPC(teamm, EntityType.valueOf(PropertiesAPI.getProperty(arenaName + "." + propertyy[1],
					"VILLAGER", DIR + arenaName + "/npcs.dcnf")), loc);
			putInSNPCS(teamm, npc);
		}));
	}

	public static void loadGenerators(String arenaName) throws IOException {
		final ConcurrentLinkedQueue<String> lnk = new ConcurrentLinkedQueue<>(
				Files.readAllLines(Paths.get(DIR + arenaName + "/generators.dcnf")));
		Iterator<String> iterate = lnk.iterator();
		lnk.stream().forEach((x) -> {
			String next = null;
			if (x.contains("* ")) {
				while (iterate.hasNext()) {
					next = iterate.next();
					if (next.equals(x))
						break;

				}
			}
			String property[] = next.split(".");
			ConcurrentLinkedQueue<String> ls = PropertiesAPI.getProperties(property[0] + "." + property[1],
					DIR + arenaName + "/generators.dcnf", "NULL");
			if (!ls.peek().equals("NULL")) {
				Iterator<String> iti = ls.iterator();
				if (iti.hasNext()) {
					String[] splitedL = iti.next().split(",");
					Location loc = new Location(Bukkit.getWorld(ArenaManager.getArenaByName(arenaName).getWorld()),
							Integer.parseInt(splitedL[0]), Integer.parseInt(splitedL[1]),
							Integer.parseInt(splitedL[2]));
					int seconds = Integer.parseInt(iti.next());
					int amount = Integer.parseInt(iti.next());
					String genName = property[0];
					ItemStack item = new ItemStack(Material.valueOf(property[1]), amount);
					putInGENS(genName, new Generator(arenaName, loc, item, seconds, amount));
				}

			}
		});
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
			try {
				PropertiesAPI.setProperty(instance, "minPlayers", String.valueOf(minPlayer), arenaFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String minC = PropertiesAPI.getProperty("minPlayers", "2", arenaFile);

		if (maxPlayer != null) {
			try {
				PropertiesAPI.setProperty(instance, "maxPlayers", String.valueOf(maxPlayer), arenaFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String maxC = PropertiesAPI.getProperty("maxPlayers", "8", arenaFile);

		if (arenaTime != null) {
			try {
				PropertiesAPI.setProperty(instance, "arenaTime", String.valueOf(arenaTime), arenaFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String timeC = PropertiesAPI.getProperty("arenaTime", "1800", arenaFile);

		ConcurrentLinkedQueue<String> waitingC = PropertiesAPI.getProperties("waitingSpawn", arenaFile, "0", "0", "0");
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
		ArenaTeam teamm = createTeam(arena, arena.getWorld(), null, team);
		setPlayerTeam(playerName, teamm);
	}

	/**
	 * 
	 * @param arena
	 * @param world
	 * @param teamm
	 * @return
	 */
	public static ArenaTeam createTeam(Arena arena, String world, String npcName, TEAMS teamm) {

		int min = Integer.parseInt(PropertiesAPI.getProperty(teamm.name() + ".min", "1",
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
		int max = Integer.parseInt(PropertiesAPI.getProperty(teamm.name() + "max", "2",
				DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));

		String coordinates[] = PropertiesAPI
				.getProperty(teamm.name() + ".block", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf")
				.split(",");
		Location bed = new Location(Bukkit.getWorld(world), Integer.parseInt(coordinates[0]),
				Integer.parseInt(coordinates[1]), Integer.parseInt(coordinates[2]));
		String ncoordinates[] = PropertiesAPI
				.getProperty(teamm.name() + ".teamspawn", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf")
				.split(",");
		Location spawn = new Location(Bukkit.getWorld(world), Integer.parseInt(ncoordinates[0]),
				Integer.parseInt(ncoordinates[1]), Integer.parseInt(ncoordinates[2]));
		String nncoordinates[] = PropertiesAPI
				.getProperty(arena.getName() + "." + npcName + "." + teamm.name() != null ? teamm.name() : null, null,
						DIR + "npcs.dcnf")
				.split(",");
		Location npc = null;
		if (npcName != null)
			npc = new Location(Bukkit.getWorld(world), Integer.parseInt(nncoordinates[0]),
					Integer.parseInt(nncoordinates[1]), Integer.parseInt(nncoordinates[2]));

		ArenaTeam team = new ArenaTeam(arena, min, max, teamm, bed, npc, spawn);
		arena.getTeams().add(team);
		return team;
	}

	/**
	 * 
	 * @param arenaName
	 * @param arenaFile
	 * @return
	 */
	public static Arena loadArena(String arenaName, String arenaFile) {
		int min = Integer.parseInt(PropertiesAPI.getProperty("min", "2", arenaFile));
		int max = Integer.parseInt(PropertiesAPI.getProperty("max", "8", arenaFile));
		int time = Integer.parseInt(PropertiesAPI.getProperty("arenaTime", "1800", arenaFile));

		String locationCoordinates[] = PropertiesAPI.getProperty("waitingSpawn", null, arenaFile).split(",");

		Location location = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", null, arenaFile)),
				Double.parseDouble(locationCoordinates[0]), Double.parseDouble(locationCoordinates[1]),
				Double.parseDouble(locationCoordinates[2]));

		String nlocationCoordinates[] = PropertiesAPI.getProperty("pos1", null, arenaFile).split(",");

		Location pos1 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", arenaFile, null)),
				Double.parseDouble(nlocationCoordinates[0]), Double.parseDouble(nlocationCoordinates[1]),
				Double.parseDouble(nlocationCoordinates[2]));

		String nnlocationCoordinates[] = PropertiesAPI.getProperty("pos2", null, arenaFile).split(",");

		Location pos2 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", null, arenaFile)),
				Double.parseDouble(nnlocationCoordinates[0]), Double.parseDouble(nnlocationCoordinates[1]),
				Double.parseDouble(nnlocationCoordinates[2]));

		Arena arena = new Arena(min, max, time, location, STATES.WAITING, arenaName,
				PropertiesAPI.getProperty("world", null, arenaFile), pos1, pos2);
		final List<TEAMS> tm = arena.getTeams().stream().map((x) -> x.getTeam()).collect(Collectors.toList());

		tm.stream().forEach((x) -> {
			String ls[] = new String[3];
			PropertiesAPI
					.getProperties(x.name() + ".block", DIR + arena.getName() + "/" + arena.getName() + ".dcnf", "NULL")
					.stream().map((y) -> y.split("-\\s*", 2)[1].split(",")).forEach((y) -> {
						ls[0] = y[0];
						ls[1] = y[1];
						ls[2] = y[2];
					});
			Location blockSpawn = new Location(Bukkit.getWorld(arena.getWorld()), Integer.parseInt(ls[0]),
					Integer.parseInt(ls[1]), Integer.parseInt(ls[2]));

			ArenaTeam team = team(arena,
					Integer.parseInt(PropertiesAPI.getProperty(x.name() + ".min", null,
							DIR + arena.getName() + "/" + x.name() + ".dcnf")),
					Integer.parseInt(PropertiesAPI.getProperty(x.name() + ".max", null,
							DIR + arena.getName() + "/" + x.name() + ".dcnf")),
					x, blockSpawn, getNPCLocation(arenaName, x), getWaitingSpawn(arena));
			arena.getTeams().add(team);
			try {
				loadGenerators(arena.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				loadNPCS(arena.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}

			loadItems(arena.getName());

		});
		addInARENALIST(arena);
		return arena;
	}

	/**
	 * 
	 * @param clazz
	 * @return
	 */
	public static ConcurrentLinkedQueue<Arena> loadArenasByAnnotation(Class<?> clazz) {
		ArenaMaker maker = clazz.getAnnotation(ArenaMaker.class);
		List<String> files = Arrays.asList(maker.arenas());
		files.stream().forEach((x) -> {
			String arenaFile = DIR + x + "/" + x + ".dcnf";

			int min = Integer.parseInt(PropertiesAPI.getProperty("min", "2", arenaFile));
			int max = Integer.parseInt(PropertiesAPI.getProperty("max", "8", arenaFile));
			int time = Integer.parseInt(PropertiesAPI.getProperty("arenaTime", "1800", arenaFile));

			String locationCoordinates[] = PropertiesAPI.getProperty("waitingSpawn", null, arenaFile).split(",");

			Location location = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", null, arenaFile)),
					Double.parseDouble(locationCoordinates[0]), Double.parseDouble(locationCoordinates[1]),
					Double.parseDouble(locationCoordinates[2]));

			String nlocationCoordinates[] = PropertiesAPI.getProperty("pos1", null, arenaFile).split(",");

			Location pos1 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", arenaFile, null)),
					Double.parseDouble(nlocationCoordinates[0]), Double.parseDouble(nlocationCoordinates[1]),
					Double.parseDouble(nlocationCoordinates[2]));

			String nnlocationCoordinates[] = PropertiesAPI.getProperty("pos2", null, arenaFile).split(",");

			Location pos2 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", null, arenaFile)),
					Double.parseDouble(nnlocationCoordinates[0]), Double.parseDouble(nnlocationCoordinates[1]),
					Double.parseDouble(nnlocationCoordinates[2]));

			Arena arena = new Arena(min, max, time, location, STATES.WAITING, x,
					PropertiesAPI.getProperty("world", null, arenaFile), pos1, pos2);

			final List<TEAMS> tm = arena.getTeams().stream().map((y) -> y.getTeam()).collect(Collectors.toList());

			tm.stream().forEach((y) -> {
				String ls[] = new String[3];
				PropertiesAPI.getProperties(y.name() + ".block",
						DIR + arena.getName() + "/" + arena.getName() + ".dcnf", "NULL").stream()
						.map((z) -> z.split("-\\s*", 2)[1].split(",")).forEach((z) -> {
							ls[0] = z[0];
							ls[1] = z[1];
							ls[2] = z[2];
						});
				Location blockSpawn = new Location(Bukkit.getWorld(arena.getWorld()), Integer.parseInt(ls[0]),
						Integer.parseInt(ls[1]), Integer.parseInt(ls[2]));

				ArenaTeam team = team(arena,
						Integer.parseInt(PropertiesAPI.getProperty(y.name() + ".min", null,
								DIR + arena.getName() + "/" + y.name() + ".dcnf")),
						Integer.parseInt(PropertiesAPI.getProperty(y.name() + ".max", null,
								DIR + arena.getName() + "/" + y.name() + ".dcnf")),
						y, blockSpawn, getNPCLocation(x, y), getWaitingSpawn(arena));
				arena.getTeams().add(team);
				try {
					loadGenerators(arena.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					loadNPCS(arena.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}

				loadItems(arena.getName());

			});

			addInARENALIST(arena);
		});
		return ARENALIST;
	}

	/**
	 * 
	 * @param class
	 * @return
	 */
	public static ConcurrentLinkedQueue<Arena> concurrentLoadArenasByAnnotation(Class<?> clazz) {
		ArenaMaker maker = clazz.getAnnotation(ArenaMaker.class);
		ConcurrentLinkedQueue<Arena> lsk = new ConcurrentLinkedQueue<>();
		List<String> files = Arrays.asList(maker.arenas());
		files.stream().forEach((x) -> {
			String arenaFile = DIR + x + "/" + x + ".dcnf";

			int min = Integer.parseInt(PropertiesAPI.getProperty("min", "2", arenaFile));
			int max = Integer.parseInt(PropertiesAPI.getProperty("max", "8", arenaFile));
			int time = Integer.parseInt(PropertiesAPI.getProperty("arenaTime", "1800", arenaFile));

			String locationCoordinates[] = PropertiesAPI.getProperty("waitingSpawn", null, arenaFile).split(",");

			Location location = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", null, arenaFile)),
					Double.parseDouble(locationCoordinates[0]), Double.parseDouble(locationCoordinates[1]),
					Double.parseDouble(locationCoordinates[2]));

			String nlocationCoordinates[] = PropertiesAPI.getProperty("pos1", null, arenaFile).split(",");

			Location pos1 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", arenaFile, null)),
					Double.parseDouble(nlocationCoordinates[0]), Double.parseDouble(nlocationCoordinates[1]),
					Double.parseDouble(nlocationCoordinates[2]));

			String nnlocationCoordinates[] = PropertiesAPI.getProperty("pos2", null, arenaFile).split(",");

			Location pos2 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", null, arenaFile)),
					Double.parseDouble(nnlocationCoordinates[0]), Double.parseDouble(nnlocationCoordinates[1]),
					Double.parseDouble(nnlocationCoordinates[2]));

			Arena arena = new Arena(min, max, time, location, STATES.WAITING, x,
					PropertiesAPI.getProperty("world", null, arenaFile), pos1, pos2);

			final List<TEAMS> tm = arena.getTeams().stream().map((y) -> y.getTeam()).collect(Collectors.toList());

			tm.stream().forEach((y) -> {
				String ls[] = new String[3];
				PropertiesAPI.getProperties(y.name() + ".block",
						DIR + arena.getName() + "/" + arena.getName() + ".dcnf", "NULL").stream()
						.map((z) -> z.split("-\\s*", 2)[1].split(",")).forEach((z) -> {
							ls[0] = z[0];
							ls[1] = z[1];
							ls[2] = z[2];
						});
				Location blockSpawn = new Location(Bukkit.getWorld(arena.getWorld()), Integer.parseInt(ls[0]),
						Integer.parseInt(ls[1]), Integer.parseInt(ls[2]));

				ArenaTeam team = team(arena,
						Integer.parseInt(PropertiesAPI.getProperty(y.name() + ".min", null,
								DIR + arena.getName() + "/" + y.name() + ".dcnf")),
						Integer.parseInt(PropertiesAPI.getProperty(y.name() + ".max", null,
								DIR + arena.getName() + "/" + y.name() + ".dcnf")),
						y, blockSpawn, getNPCLocation(x, y), getWaitingSpawn(arena));
				arena.getTeams().add(team);

				try {
					loadGenerators(arena.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					loadNPCS(arena.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}

				loadItems(arena.getName());

			});

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
			Optional<String> arenaName = dirs.stream().filter((x) -> x.equals(dir))
					.filter((x) -> x.toString().split("//")[1].contains(".dcnf"))
					.map((x) -> x.toString().split("//")[1]).findFirst();
			if (arenaName.isPresent()) {
				return;
			}

			String arenaFile = DIR + dir + "/" + dir + ".dcnf";

			String min = PropertiesAPI.getProperty("min", "2", arenaFile);
			String max = PropertiesAPI.getProperty("max", "8", arenaFile);
			String time = PropertiesAPI.getProperty("arenaTime", "1800", arenaFile);

			String locationCoordinates[] = PropertiesAPI.getProperty("waitingSpawn", null, arenaFile).split(",");

			Location location = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", null, arenaFile)),
					Double.parseDouble(locationCoordinates[0]), Double.parseDouble(locationCoordinates[1]),
					Double.parseDouble(locationCoordinates[2]));

			String nlocationCoordinates[] = PropertiesAPI.getProperty("pos1", null, arenaFile).split(",");

			Location pos1 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", arenaFile, null)),
					Double.parseDouble(nlocationCoordinates[0]), Double.parseDouble(nlocationCoordinates[1]),
					Double.parseDouble(nlocationCoordinates[2]));

			String nnlocationCoordinates[] = PropertiesAPI.getProperty("pos2", null, arenaFile).split(",");

			Location pos2 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", null, arenaFile)),
					Double.parseDouble(nnlocationCoordinates[0]), Double.parseDouble(nnlocationCoordinates[1]),
					Double.parseDouble(nnlocationCoordinates[2]));

			String world = PropertiesAPI.getProperty("world", null, arenaFile);

			if (arenaName.isPresent()) {
				try {
					loadGenerators(arenaName.get());
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					loadNPCS(arenaName.get());
				} catch (IOException e) {
					e.printStackTrace();
				}

				loadItems(arenaName.get());
			}
			if (pos2 == null || pos1 == null || location == null || world == null)
				throw new IllegalStateException("Some of values or null for " + arenaName);

			Arena arena = new Arena(Integer.parseInt(min), Integer.parseInt(max), Integer.parseInt(time), location,
					STATES.WAITING, arenaName.get(), world, pos1, pos2);

			final List<TEAMS> tm = arena.getTeams().stream().map((y) -> y.getTeam()).collect(Collectors.toList());

			tm.stream().forEach((y) -> {
				String ls[] = new String[3];
				PropertiesAPI.getProperties(y.name() + ".block",
						DIR + arena.getName() + "/" + arena.getName() + ".dcnf", "NULL").stream()
						.map((z) -> z.split("-\\s*", 2)[1].split(",")).forEach((z) -> {
							ls[0] = z[0];
							ls[1] = z[1];
							ls[2] = z[2];
						});
				Location blockSpawn = new Location(Bukkit.getWorld(arena.getWorld()), Integer.parseInt(ls[0]),
						Integer.parseInt(ls[1]), Integer.parseInt(ls[2]));

				ArenaTeam team = team(arena,
						Integer.parseInt(PropertiesAPI.getProperty(y.name() + ".min", null,
								DIR + arena.getName() + "/" + y.name() + ".dcnf")),
						Integer.parseInt(PropertiesAPI.getProperty(y.name() + ".max", null,
								DIR + arena.getName() + "/" + y.name() + ".dcnf")),
						y, blockSpawn, getNPCLocation(arenaName.get(), y), getWaitingSpawn(arena));
				arena.getTeams().add(team);
			});

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
				Files.walk(Paths.get(DIR)).skip(0).filter(Files::isDirectory).collect(Collectors.toList()));

		dirs.poll();

		for (Path dir : dirs) {
			Optional<String> arenaName = dirs.stream().filter((x) -> x.equals(dir))
					.filter((x) -> x.toString().split("//")[1].contains(".dcnf"))
					.map((x) -> x.toString().split("//")[1]).findFirst();
			if (arenaName.isPresent()) {
				return;
			}

			String arenaFile = DIR + dir + "/" + dir + ".dcnf";

			String min = PropertiesAPI.getProperty("min", "2", arenaFile);
			String max = PropertiesAPI.getProperty("max", "8", arenaFile);
			String time = PropertiesAPI.getProperty("arenaTime", "1800", arenaFile);

			String locationCoordinates[] = PropertiesAPI.getProperty("waitingSpawn", null, arenaFile).split(",");

			Location location = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", null, arenaFile)),
					Double.parseDouble(locationCoordinates[0]), Double.parseDouble(locationCoordinates[1]),
					Double.parseDouble(locationCoordinates[2]));

			String nlocationCoordinates[] = PropertiesAPI.getProperty("pos1", null, arenaFile).split(",");

			Location pos1 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", arenaFile, null)),
					Double.parseDouble(nlocationCoordinates[0]), Double.parseDouble(nlocationCoordinates[1]),
					Double.parseDouble(nlocationCoordinates[2]));

			String nnlocationCoordinates[] = PropertiesAPI.getProperty("pos2", null, arenaFile).split(",");

			Location pos2 = new Location(Bukkit.getWorld(PropertiesAPI.getProperty("world", null, arenaFile)),
					Double.parseDouble(nnlocationCoordinates[0]), Double.parseDouble(nnlocationCoordinates[1]),
					Double.parseDouble(nnlocationCoordinates[2]));

			String world = PropertiesAPI.getProperty("world", null, arenaFile);

			try {
				loadGenerators(arenaName.get());
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				loadNPCS(arenaName.get());
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (pos2 == null || pos1 == null || location == null || world == null)
				throw new IllegalStateException("Some of values or null for " + arenaName);

			Arena arena = new Arena(Integer.parseInt(min), Integer.parseInt(max), Integer.parseInt(time), location,
					STATES.WAITING, arenaName.get(), world, pos1, pos2);
			final List<TEAMS> tm = arena.getTeams().stream().map((y) -> y.getTeam()).collect(Collectors.toList());

			tm.stream().forEach((y) -> {
				String ls[] = new String[3];
				PropertiesAPI.getProperties(y.name() + ".block",
						DIR + arena.getName() + "/" + arena.getName() + ".dcnf", "NULL").stream()
						.map((z) -> z.split("-\\s*", 2)[1].split(",")).forEach((z) -> {
							ls[0] = z[0];
							ls[1] = z[1];
							ls[2] = z[2];
						});
				Location blockSpawn = new Location(Bukkit.getWorld(arena.getWorld()), Integer.parseInt(ls[0]),
						Integer.parseInt(ls[1]), Integer.parseInt(ls[2]));

				ArenaTeam team = team(arena,
						Integer.parseInt(PropertiesAPI.getProperty(y.name() + ".min", null,
								DIR + arena.getName() + "/" + y.name() + ".dcnf")),
						Integer.parseInt(PropertiesAPI.getProperty(y.name() + ".max", null,
								DIR + arena.getName() + "/" + y.name() + ".dcnf")),
						y, blockSpawn, getNPCLocation(arenaName.get(), y), getWaitingSpawn(arena));
				arena.getTeams().add(team);
			});

			addInARENALIST(arena);
		}
	}

	public static ArenaTeam getTeamByArenaAndName(Arena arena, TEAMS team) {
		ConcurrentLinkedQueue<ArenaTeam> t = new ConcurrentLinkedQueue<>();
		ARENALIST.stream().filter((x) -> x.equals(arena)).forEach((x) -> {
			Optional<ArenaTeam> teamm = x.getTeams().stream()
					.filter((y) -> y.getTeam() == team && y.getArena().equals(arena)).findFirst();
			if (teamm.isPresent())
				t.add(teamm.get());
		});
		return t.peek();
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
			ArenaTeam arenaTeam = createTeam(arena, arena.getWorld(), null, playerData.getTeam().getTeam());
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
	public static void addAndSelectTeamPlayer(String playerName, Arena arena, STATES status, ArenaTeam team,
			Location locationToSpawn, boolean check) {
		Player player = Bukkit.getPlayer(playerName);
		if (player != null) {
			if (ARENALIST.contains(arena)) {
				if (!isPlayerSettedOnce(playerName)) {
					PlayerData data = new PlayerData(team, playerName, status);
					putInPLAYERS(playerName, data);
					arena.getPlayersNames().add(playerName);
				} else {
					Bukkit.getPlayer(playerName).sendMessage(Chati.translate(PropertiesAPI
							.getProperty("playerAlreadyInIt", "&c{PLAYER} is already in arena", DIR + "messages.dcnf")
							.replaceAll("{PLAYER}", playerName)));
					Bukkit.getLogger().severe("Player is already exist!");
				}
				if (locationToSpawn != null && player != null)
					player.teleport(locationToSpawn);
				if (check == true) {
					String property = PropertiesAPI.getProperty("selectTeamItem", "COMPASS",
							DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
					ItemStack item = new ItemStack(Material.valueOf(property), 1);
					player.getInventory().setItem(40, item);
				}
			}
		}
	}

	public static void randomSelectTeam(Arena arena, String playerName, STATES status) {
		if (Bukkit.getPlayer(playerName) != null) {
			if (ARENALIST.contains(arena)) {
				Random rand = new Random();
				TEAMS team = TEAMS.values()[rand.nextInt(TEAMS.values().length)];
				ArenaTeam teamm = getTeamByArenaAndName(arena, team);
				Player player = Bukkit.getPlayer(playerName);
				setAndgetPlayersData(player, teamm);
			}
		}
	}

	public static void addPlayer(String playerName, Arena arena, STATES status) {
		if (Bukkit.getPlayer(playerName) != null) {
			if (ARENALIST.contains(arena)) {
				if (!isPlayerSettedOnce(playerName)) {
					PlayerData data = new PlayerData(null, playerName, status);
					putInPLAYERS(playerName, data);
					arena.getPlayersNames().add(playerName);
				} else {
					Bukkit.getPlayer(playerName).sendMessage(Chati.translate(PropertiesAPI
							.getProperty("playerAlreadyInIt", "&c{PLAYER} is already in arena", DIR + "messages.dcnf")
							.replaceAll("{PLAYER}", playerName)));
					Bukkit.getLogger().severe("Player is already exist!");
				}
			}
		}
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
	public static void randomAddAndSelectPlayer(String playerName, Arena arena, STATES status,
			Location locationToSpawn) {
		if (Bukkit.getPlayer(playerName) != null) {
			if (ARENALIST.contains(arena)) {
				Random rand = new Random();
				int random = rand.nextInt(TEAMS.values().length);
				TEAMS teamm = TEAMS.values()[random];
				ArenaTeam team = getTeamByArenaAndName(arena, teamm);
				Player player = Bukkit.getPlayer(playerName);
				setAndgetPlayersData(player, team);

				if (locationToSpawn != null && player != null)
					player.teleport(locationToSpawn);
			}
		}
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

	@Deprecated
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
	public static ConcurrentLinkedQueue<String> getTeamsPlayers(Arena arena, TEAMS team) {
		ConcurrentLinkedQueue<String> fls = new ConcurrentLinkedQueue<>();
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
	 * @param arena
	 * @return
	 */
	public static String getArenaWorld(Arena arena) {
		if (arena.getWorld() != null) {
			return arena.getWorld();
		} else {
			return PropertiesAPI.getProperty("world", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
		}
	}

	/**
	 * 
	 * @param instance
	 * @param arena
	 * @param world
	 */
	public static void setArenaWorld(Plugin instance, Arena arena, String world) {
		arena.setWorld(world);
		try {
			PropertiesAPI.setProperty(instance, "world", world,
					DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	 * @param instance
	 * @param arena
	 * @param location
	 */
	public static void setPos1(Plugin instance, Arena arena, Location location) {
		arena.setPos1(location);
		try {
			PropertiesAPI.setProperty(instance, "pos1",
					location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
					DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param instance
	 * @param arena
	 * @param location
	 */
	public static void setPos2(Plugin instance, Arena arena, Location location) {
		arena.setPos2(location);
		try {
			PropertiesAPI.setProperty(instance, "pos2",
					location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
					DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
					.getProperty("pos1", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf").split(",");
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
		String values[] = PropertiesAPI.getProperty("pos1", null, DIR + arenaName + "/" + arenaName + ".dcnf")
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
					.getProperty("pos2", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf").split(",");
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
		String values[] = PropertiesAPI.getProperty("pos2", null, DIR + arenaName + "/" + arenaName + ".dcnf")
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
	 * @param arenaName
	 * @param generatorName
	 * @param itemName
	 * @return
	 */
	public static Location getGeneratorLocation(String arenaName, String generatorName, String itemName) {
		String values[] = new String[3];
		PropertiesAPI.getProperties(generatorName + "." + itemName, DIR + arenaName + "/" + arenaName + ".dcnf", "NULL")
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
	 * @param instance
	 * @param team
	 * @param location
	 */
	public static void setTeamSpawn(Plugin instance, ArenaTeam team, Location location) {
		team.setTeamSpawn(location);
		try {
			PropertiesAPI.setProperty(instance, team.getTeam().name() + ".teamspawn",
					location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
					DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		String values[] = PropertiesAPI.getProperty(team.getTeam().name() + ".spawn", null,
				DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf").split(",");
		return new Location(Bukkit.getWorld(world), Double.parseDouble(values[0]), Double.parseDouble(values[1]),
				Double.parseDouble(values[2]));
	}

	/**
	 * 
	 * @param instance
	 * @param team
	 * @param location
	 */
	public static void setBlockSpawn(Plugin instance, ArenaTeam team, Location location) {
		team.setBlockSpawn(location);
		try {
			PropertiesAPI.setProperty(instance, team.getTeam().name() + ".block",
					location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
					DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
					.getProperties(team.getTeam().name() + ".block",
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
			PropertiesAPI.getProperties("waiting", DIR + arena.getName() + "/" + arena.getName() + ".dcnf", "NULL")
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
		PropertiesAPI.getProperties("waiting", DIR + arenaName + "/" + arenaName + ".dcnf", "NULL").stream()
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
	 * @param instance
	 * @param arena
	 * @param number
	 */
	public static void setMaxArena(Plugin instance, Arena arena, String number) {
		arena.setMaxPlayers(Integer.parseInt(number));
		try {
			PropertiesAPI.setProperty(instance, "max", number, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param arena
	 * @return
	 */
	public static Integer getMaxArena(Arena arena) {
		return arena.getMaxPlayer() != null ? arena.getMaxPlayer()
				: Integer.parseInt(PropertiesAPI.getProperty("max", null,
						DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
	}

	/**
	 * 
	 * @param instance
	 * @param arena
	 * @param number
	 */
	public static void setMinArena(Plugin instance, Arena arena, String number) {
		arena.setMinPlayers(Integer.parseInt(number));
		try {
			PropertiesAPI.setProperty(instance, "min", number, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param arena
	 * @return
	 */
	public static Integer getMinArena(Arena arena) {
		return arena.getMinPlayer() != null ? arena.getMinPlayer()
				: Integer.parseInt(PropertiesAPI.getProperty("min", null,
						DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
	}

	/**
	 * 
	 * @param instance
	 * @param team
	 * @param number
	 */
	public static void setTeamMax(Plugin instance, ArenaTeam team, String number) {
		team.setMaxNumber(Integer.parseInt(number));
		try {
			PropertiesAPI.setProperty(instance, team.getTeam().name() + ".min", number,
					DIR + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param team
	 * @return
	 */
	public static Integer getTeamMax(ArenaTeam team) {
		return team.getMaxNumber() != null ? team.getMaxNumber()
				: Integer.parseInt(PropertiesAPI.getProperty(team.getTeam().name() + ".max", null,
						DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf"));
	}

	/**
	 * 
	 * @param instance
	 * @param team
	 * @param number
	 */
	public static void setTeamMin(Plugin instance, ArenaTeam team, String number) {
		team.setMinNumber(Integer.parseInt(number));
		try {
			PropertiesAPI.setProperty(instance, team.getTeam().name() + ".max", number,
					DIR + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param team
	 * @return
	 */
	public static Integer getTeamMin(ArenaTeam team) {
		return team.getMinNumber() != null ? team.getMinNumber()
				: Integer.parseInt(PropertiesAPI.getProperty(team.getTeam().name() + ".min", null,
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
	public static void regenerateBreakedBlocks(Arena arena) {
		arena.getBreakedBlocks().stream().filter((x) -> isEntityOnRegion(arena, x.getLocation())).forEach((x) -> {
			Bukkit.getWorld(arena.getWorld()).getBlockAt(x.getLocation()).setType(x.getType());
		});
	}

	/**
	 * 
	 * @param arena
	 */
	public static void deletePlacedBlocks(Arena arena) {
		arena.getPlacedBlocks().stream().filter((x) -> isEntityOnRegion(arena, x.getLocation())).forEach((x) -> {
			Bukkit.getWorld(arena.getWorld()).getBlockAt(x.getLocation()).setType(Material.AIR);
		});
	}

	/**
	 * 
	 * @param arena
	 * @param location
	 */
	public static void addBreakedBlock(Arena arena, Location location) {
		arena.getBreakedBlocks().add(location.getBlock());
	}

	/**
	 * 
	 * @param arena
	 * @param location
	 */
	public static void addPlacedBlock(Arena arena, Location location) {
		arena.getPlacedBlocks().add(location.getBlock());
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

	/**
	 * 
	 * @param player
	 * @return
	 */
	public static PlayerData getPlayersData(Player player) {
		Optional<Entry<String, PlayerData>> opt = ArenaManager.PLAYERS.entrySet().stream()
				.filter((x) -> x.getKey().equals(player.getName())).findFirst();
		if (opt.isPresent()) {
			return opt.get().getValue();
		}
		return null;
	}

	public static PlayerData setAndgetPlayersData(Player player, ArenaTeam team) {
		PLAYERS.entrySet().stream().filter((x) -> x.getValue().getTeam() == null && x.getKey().equals(player.getName()))
				.forEach((x) -> {
					PlayerData data = getPlayersData(player);
					data.setTeam(team);
					putInPLAYERS(player.getName(), data);
				});
		Optional<Entry<String, PlayerData>> opt = ArenaManager.PLAYERS.entrySet().stream()
				.filter((x) -> x.getKey().equals(player.getName())).findFirst();
		if (opt.isPresent()) {
			return opt.get().getValue();
		}
		return null;
	}

	/**
	 * 
	 * @param instance
	 * @param location
	 */
	public static void setLobbySpawn(Plugin instance, Location location) {
		try {
			PropertiesAPI.setProperty(
					instance, "lobbySpawn", String.valueOf(location.getBlockX()) + ","
							+ String.valueOf(location.getBlockY()) + "," + String.valueOf(location.getBlockZ()),
					DIR + "messages.dcnf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
