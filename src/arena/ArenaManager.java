package arena;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;

/**
 * @apiNote PLAYING and INPROCESS are used by Arena but the others are for
 *          players
 */
enum STATES {
	STARTED, ENDED, WAITING, INPROCESS, PLAYING
}

enum TEAMS {
	RED, BLUE, GREEN, YELLOW, ORANGE, BLACK, PINK, PURPLE, BROWN, WHITE
}

class Arena {

	private STATES status;

	private String name;

	private int minPlayer;

	private int maxPlayer;

	private int arenaTime;

	private Location waitingSpawn;

	private List<String> playersNames = new ArrayList<>();

	private List<Location> npcsLocations = new ArrayList<>();

	private Map<String, Location> bedsSpawns = new HashMap<>();

	private Map<String, Location> teamsSpawns = new HashMap<>();

	private Map<String, Location> arenaGenerators = new HashMap<>();

	private List<TEAMS> teams;

	public Arena(int minPlayer, int maxPlayer, int arenaTime, Location waitingSpawn, STATES status, String name) {
		this.minPlayer = minPlayer;
		this.maxPlayer = maxPlayer;
		this.arenaTime = arenaTime;
		this.waitingSpawn = waitingSpawn;
		this.name = name;
		this.status = status;
	}

	public Arena(int minPlayer, int maxPlayer, int arenaTime, Location waitingSpawn, List<TEAMS> teams,
			Map<String, Location> arenaGenerators, Map<String, Location> teamsSpawns, Map<String, Location> bedsSpawns,
			List<Location> npcsLocations, STATES status, String name) {
		this.minPlayer = minPlayer;
		this.maxPlayer = maxPlayer;
		this.arenaTime = arenaTime;
		this.waitingSpawn = waitingSpawn;
		this.teams = teams;
		this.arenaGenerators = arenaGenerators;
		this.teamsSpawns = teamsSpawns;
		this.bedsSpawns = bedsSpawns;
		this.npcsLocations = npcsLocations;
		this.status = status;
		this.name = name;
	}

	public void setStatus(STATES status) {
		this.status = status;
	}

	public List<TEAMS> getTeams() {
		return teams;
	}

	public STATES getStatus() {
		return status;
	}

	public int getMinPlayer() {
		return minPlayer;
	}

	public void setMinPlayers(int minPlayer) {
		this.minPlayer = minPlayer;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}

	public void setMaxPlayers(int maxPlayer) {
		this.maxPlayer = maxPlayer;
	}

	public Location getWaitingSpawn() {
		return waitingSpawn;
	}

	public void setWaitingSpawn(Location waitingSpawn) {
		this.waitingSpawn = waitingSpawn;
	}

	public void setTeams(List<TEAMS> teams) {
		this.teams = teams;
	}

	public int getArenaTime() {
		return arenaTime;
	}

	public void setArenaTime(int arenaTime) {
		this.arenaTime = arenaTime;
	}

	public Map<String, Location> getBedsSpawns() {
		return bedsSpawns;
	}

	public Map<String, Location> getTeamsSpawns() {
		return teamsSpawns;
	}

	public Map<String, Location> getArenaGenerators() {
		return arenaGenerators;
	}

	public List<Location> getNpcLocations() {
		return npcsLocations;
	}

	public List<String> getPlayersNames() {
		return playersNames;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

public class ArenaManager {

	public static ListMultimap<Arena, String> arenaValues = ListMultimapBuilder.hashKeys().arrayListValues().build();

	public static ListMultimap<String, String> playerValues = ListMultimapBuilder.hashKeys().arrayListValues().build();

	public static List<Arena> arenaList = new ArrayList<>();

	public static String directory = "plugins/";

	public static void setDirectory(String directory) {
		ArenaManager.directory = directory;
	}

	public static void createArena(Plugin instance, String arenaName, Integer minPlayer, Integer maxPlayer,
			Integer arenaTime, Location waitingSpawn, String world, List<TEAMS> teams) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			String arenaDir = directory + arenaName;
			String arenaFile = directory + arenaName + "/" + arenaName + ".dcnf";
			if (!Files.notExists(Paths.get(arenaDir))) {
				try {
					Files.createDirectory(Paths.get(arenaDir));
					Files.createFile(Paths.get(arenaFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (!Files.notExists(Paths.get(arenaFile))) {
				try {
					Files.createFile(Paths.get(arenaFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			final int min = minPlayer != null ? minPlayer
					: Integer.parseInt(PropertiesAPI.getProperty_NS(false, "minPlayers", "2", arenaFile));
			final int max = maxPlayer != null ? maxPlayer
					: Integer.parseInt(PropertiesAPI.getProperty_NS(false, "maxPlayers", "8", arenaFile));
			final int time = arenaTime != null ? arenaTime
					: Integer.parseInt(PropertiesAPI.getProperty_NS(false, "arenaTime", "1800", arenaFile));

			final List<String> locationCoordinates = PropertiesAPI.getProperties_NNS("waitingSpawn", arenaFile, "0.0",
					"0.0", "0.0");

			final Location location = waitingSpawn != null ? waitingSpawn
					: new Location(Bukkit.getWorld(world), Double.parseDouble(locationCoordinates.get(0)),
							Double.parseDouble(locationCoordinates.get(1)),
							Double.parseDouble(locationCoordinates.get(2)));

			Arena arena = new Arena(min, max, time, location, STATES.INPROCESS, arenaFile);
			arenaList.add(arena);

		});

	}

	/**
	 * @apiNote this have to be used in server starting
	 */
	public static void loadArenas_NS() {
		List<Path> dirs = null;
		List<Path> files = null;
		try {
			dirs = Files.walk(Paths.get(directory)).filter(Files::isDirectory).collect(Collectors.toList());
			files = Files.walk(Paths.get(directory)).filter(Files::isRegularFile).collect(Collectors.toList());
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

				int min = Integer.parseInt(PropertiesAPI.getProperty_NS(false, "minPlayers", "2", arenaFile));
				int max = Integer.parseInt(PropertiesAPI.getProperty_NS(false, "maxPlayers", "8", arenaFile));
				int time = Integer.parseInt(PropertiesAPI.getProperty_NS(false, "arenaTime", "1800", arenaFile));

				List<String> locationCoordinates = PropertiesAPI.getProperties_NNS("waitingSpawn", arenaFile, "0.0",
						"0.0", "0.0");

				Location location = new Location(
						Bukkit.getWorld(PropertiesAPI.getProperty_NS(true, "world", arenaFile, null)),
						Double.parseDouble(locationCoordinates.get(0)), Double.parseDouble(locationCoordinates.get(1)),
						Double.parseDouble(locationCoordinates.get(2)));

				List<TEAMS> team = PropertiesAPI.getProperties_NNS("teams", arenaFile, "RED", "BLUE", "GREEN", "YELLOW")
						.stream().filter((x) -> TEAMS.valueOf(x.toUpperCase()) != null).map((x) -> TEAMS.valueOf(x))
						.collect(Collectors.toList());

				List<String> npcs = PropertiesAPI.getProperties_NS("npcs", arenaFile, null);
				List<Location> realNpcs = new ArrayList<>();
				for (String notNpc : npcs) {

					String kir[] = notNpc.split("-\\s*", 2);
					String values[] = kir[1].split(",");

					Location loc = new Location(
							Bukkit.getWorld(PropertiesAPI.getProperty_NS(false, "npcs", null, arenaFile)),
							Double.parseDouble(values[0]), Double.parseDouble(values[1]),
							Double.parseDouble(values[2]));
					realNpcs.add(loc);
				}

				List<String> beds = PropertiesAPI.getProperties_NS("teamBeds", arenaFile, null);
				Map<String, Location> realBeds = new HashMap<>();
				for (String notBed : beds) {
					String kir[] = notBed.split("-\\s*", 2);
					String values[] = kir[1].split(",");

					Location loc = new Location(
							Bukkit.getWorld(PropertiesAPI.getProperty_NS(false, "beds", null, arenaFile)),
							Double.parseDouble(values[0]), Double.parseDouble(values[1]),
							Double.parseDouble(values[2]));
					realBeds.put(arenaName, loc);
				}

				List<String> spawns = PropertiesAPI.getProperties_NS("teamsSpawns", arenaFile, null);
				Map<String, Location> teamsSpawns = new HashMap<>();
				for (String notSpawn : spawns) {
					String kir[] = notSpawn.split("-\\s*", 2);
					String values[] = kir[1].split(",");

					Location loc = new Location(
							Bukkit.getWorld(PropertiesAPI.getProperty_NS(false, "teamsSpawns", null, arenaFile)),
							Double.parseDouble(values[0]), Double.parseDouble(values[1]),
							Double.parseDouble(values[2]));
					teamsSpawns.put(arenaName, loc);
				}

				List<String> generators = PropertiesAPI.getProperties_NS("arenaGenerators", arenaFile, null);
				Map<String, Location> arenaGenerators = new HashMap<>();
				for (String notGenerator : generators) {
					String kir[] = notGenerator.split("-\\s*", 2);
					String values[] = kir[1].split(",");

					Location loc = new Location(
							Bukkit.getWorld(PropertiesAPI.getProperty_NS(false, "teamsSpawns", null, arenaFile)),
							Double.parseDouble(values[0]), Double.parseDouble(values[1]),
							Double.parseDouble(values[2]));
					arenaGenerators.put(arenaName, loc);
				}

				Arena arena = new Arena(min, max, time, location, team, arenaGenerators, teamsSpawns, realBeds,
						realNpcs, STATES.WAITING, arenaName);
				arenaList.add(arena);
			}
		}
	}

	public void setArenaStatus(Arena arena, String state) {
		if (STATES.valueOf(state) != null)
			arenaValues.get(arena).set(0, state);
		else
			Bukkit.getLogger().severe("Problem with setArenaStatus() : the status its not valid");
	}

	public List<Player> getArenaPlayers(Arena arena) {
		return arenaValues.get(arena).stream().map((x) -> Bukkit.getPlayer(x)).collect(Collectors.toList());
	}

	public Arena getPlayersArena_NS(String name) {
		for (Arena arena : arenaList) {
			List<String> ls = arenaValues.get(arena);
			ls.remove(0);
			for (String playerName : ls) {
				if (playerName.equals(name)) {
					return arena;
				}
			}
		}
		return null;
	}

	public void addPlayer(String playerName, Arena arena, String status, String team, Location locationToSpawn) {
		Player player = Bukkit.getPlayer(playerName);
		if (player != null && arenaList.contains(arena)) {
			arenaValues.get(arena).add(playerName);
			playerValues.put(playerName, arena.getName());
		}
		if (STATES.valueOf(status) != null)
			playerValues.put(playerName, status);
		if (TEAMS.valueOf(team) != null)
			playerValues.put(playerName, team);
		if (locationToSpawn != null && player != null)
			player.teleport(locationToSpawn);
	}

	public void removePlayer(String playerName, Arena arena) {
		arenaValues.remove(arena, playerName);
		playerValues.removeAll(playerName);
	}

	public STATES getArenaStatus(Arena arena) {
		return STATES.valueOf(arenaValues.get(arena).get(0));
	}

	public STATES getPlayerStatus(String playerName) {
		return STATES.valueOf(playerValues.get(playerName).get(1));
	}

	public TEAMS getPlayerTeam(String playerName) {
		return TEAMS.valueOf(playerValues.get(playerName).get(2));
	}

	public Integer getPlayerIndexInArena_NS(String playerName, Arena arena) {
		int i = 1;

		while (i < arenaValues.get(arena).size()) {
			i++;
			if (arenaValues.get(arena).get(i).equals(playerName)) {
				return i;
			}
		}
		return null;
	}

	public static Location getPos1_NS(String arenaName) {
		List<String> ls = PropertiesAPI.getProperties_NNS(arenaName + ".pos1", "plugins/bedwars/" + arenaName + ".dcnf",
				"");
		return new Location(Bukkit.getWorld(arenaName), Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)),
				Integer.parseInt(ls.get(2)));
	}

	public static Location getPos2_NS(String arenaName) {
		List<String> ls = PropertiesAPI.getProperties_NNS(arenaName + ".pos2", "plugins/bedwars/" + arenaName + ".dcnf",
				"");
		return new Location(Bukkit.getWorld(arenaName), Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)),
				Integer.parseInt(ls.get(2)));
	}

	public static void setGenerator_NS(Player player, String arenaName, String generatorName, String itemName,
			String secounds, String amount) {
		PropertiesAPI.setProperties_NS(true, generatorName + "." + itemName, "plugins/bedwars/" + arenaName + ".dcnf",
				String.valueOf(player.getLocation().getBlockX()), String.valueOf(player.getLocation().getBlockY()),
				String.valueOf(player.getLocation().getBlockZ()), secounds, amount);
	}

	public static Location getGeneratorLocation_NS(String arenaName, String generatorName, String itemName) {
		List<String> ls = PropertiesAPI.getProperties_NS(generatorName + "." + itemName,
				"plugins/bedwars/" + arenaName + ".dcnf", null);
		return new Location(Bukkit.getWorld(itemName), Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)),
				Integer.parseInt(ls.get(2)));
	}

	public static void setTeamSpawn(Arena arena, Location location) {

	}

}
