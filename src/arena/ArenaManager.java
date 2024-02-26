package arena;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;

import bedwars.BedWarsPlugin;

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

	private int minPlayer;

	private int maxPlayer;

	private int arenaTime;

	private Location waitingSpawn;

	private List<String> playersNames = new ArrayList<>();

	private List<Location> npcLocations = new ArrayList<>();

	private List<Location> bedsSpawns = new ArrayList<>();

	private List<Location> teamsSpawns = new ArrayList<>();

	private List<Location> arenaGenerators = new ArrayList<>();

	private List<TEAMS> teams;

	public Arena(int minPlayer, int maxPlayer, int arenaTime, Location waitingSpawn, List<TEAMS> teams) {
		this.minPlayer = minPlayer;
		this.maxPlayer = maxPlayer;
		this.arenaTime = arenaTime;
		this.waitingSpawn = waitingSpawn;
		this.teams = teams;
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

	public List<Location> getBedsSpawns() {
		return bedsSpawns;
	}

	public List<Location> getTeamsSpawns() {
		return teamsSpawns;
	}

	public List<Location> getArenaGenerators() {
		return arenaGenerators;
	}

	public List<Location> getNpcLocations() {
		return npcLocations;
	}

	public List<String> getPlayersNames() {
		return playersNames;
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
			if (arenaList != null) {
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

					LivingEntity lv;
				}
				final int min = minPlayer != null ? minPlayer
						: Integer.parseInt(PropertiesAPI.getProperty_NS(false, "minPlayers", "2", arenaFile));
				final int max = maxPlayer != null ? maxPlayer
						: Integer.parseInt(PropertiesAPI.getProperty_NS(false, "maxPlayers", "8", arenaFile));
				final int time = arenaTime != null ? arenaTime
						: Integer.parseInt(PropertiesAPI.getProperty_NS(false, "arenaTime", "1800", arenaFile));

				final List<String> locationCoordinates = PropertiesAPI.getProperties_NS("waitingSpawn", arenaFile,
						"0.0", "0.0", "0.0");

				final Location location = waitingSpawn != null ? waitingSpawn
						: new Location(Bukkit.getWorld(world), Double.parseDouble(locationCoordinates.get(0)),
								Double.parseDouble(locationCoordinates.get(1)),
								Double.parseDouble(locationCoordinates.get(2)));

				final List<TEAMS> team = teams != null ? teams
						: PropertiesAPI.getProperties_NS("teams", arenaFile, "RED", "BLUE", "GREEN", "YELLOW").stream()
								.filter((x) -> TEAMS.valueOf(x.toUpperCase()) != null).map((x) -> TEAMS.valueOf(x))
								.collect(Collectors.toList());

				Arena arena = new Arena(min, max, time, location, team);
				arenaList.add(arena);
			}
		});
	}

	/**
	 * @apiNote this have to be used in server starting
	 */
	public static void loadArenas_NS() {
		List<Path> directories = null;
		try {
			directories = Files.walk(Paths.get(directory)).filter(Files::isDirectory).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		directories.remove(0);
		for (Path dirs : directories) {
			String arenaFile = dirs.toString();
			final int min = Integer.parseInt(PropertiesAPI.getProperty_NS(false, "minPlayers", "2", arenaFile));
			final int max = Integer.parseInt(PropertiesAPI.getProperty_NS(false, "maxPlayers", "8", arenaFile));
			final int time = Integer.parseInt(PropertiesAPI.getProperty_NS(false, "arenaTime", "1800", arenaFile));

			final List<String> locationCoordinates = PropertiesAPI.getProperties_NS("waitingSpawn", arenaFile, "0.0",
					"0.0", "0.0");

			final Location location = new Location(
					Bukkit.getWorld(PropertiesAPI.getProperty_NS(true, "world", arenaFile, null)),
					Double.parseDouble(locationCoordinates.get(0)), Double.parseDouble(locationCoordinates.get(1)),
					Double.parseDouble(locationCoordinates.get(2)));

			final List<TEAMS> team = PropertiesAPI
					.getProperties_NS("teams", arenaFile, "RED", "BLUE", "GREEN", "YELLOW").stream()
					.filter((x) -> TEAMS.valueOf(x.toUpperCase()) != null).map((x) -> TEAMS.valueOf(x))
					.collect(Collectors.toList());

			Arena arena = new Arena(min, max, time, location, team);
			arenaList.add(arena);
		}
	}

	public STATES getArenaStatus(Arena arena) {
		return STATES.valueOf(arenaValues.get(arena).get(0));
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
		if (player != null && arenaList.contains(arena))
			arenaValues.get(arena).add(playerName);
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

	public STATES getPlayerStatus(String playerName, Arena arena) {
		return STATES.valueOf(arenaValues.get(arena).get(0));
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
		List<String> ls = PropertiesAPI.getProperties_NS(arenaName + ".pos1", "plugins/bedwars/" + arenaName + ".dcnf",
				"");
		return new Location(Bukkit.getWorld(arenaName), Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)),
				Integer.parseInt(ls.get(2)));
	}

	public static Location getPos2_NS(String arenaName) {
		List<String> ls = PropertiesAPI.getProperties_NS(arenaName + ".pos2", "plugins/bedwars/" + arenaName + ".dcnf",
				"");
		return new Location(Bukkit.getWorld(arenaName), Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)),
				Integer.parseInt(ls.get(2)));
	}

	public void setGenerator(Player player, String arenaName, String generatorName, String itemName, String secounds,
			String amount) {
		PropertiesAPI.setProperties_NS(generatorName + "." + itemName, true, "plugins/bedwars/" + arenaName + ".dcnf",
				String.valueOf(player.getLocation().getBlockX()), String.valueOf(player.getLocation().getBlockY()),
				String.valueOf(player.getLocation().getBlockZ()), secounds, amount);
	}

	public Location getGeneratorLocation(String arenaName, String generatorName, String itemName) {
		List<String> ls = BedWarsPlugin.getPapijoy().getListProperties(arenaName + "." + generatorName + "." + itemName,
				"plugins/bedwars/" + arenaName + ".dcnf", null);
		return new Location(Bukkit.getWorld(itemName), Integer.parseInt(ls.get(0)), Integer.parseInt(ls.get(1)),
				Integer.parseInt(ls.get(2)));
	}

	public int getGeneratorAmount(String arenaName, String generatorName, String itemName) {
		return Integer
				.parseInt(BedWarsPlugin.getPapijoy().getListProperties(arenaName + "." + generatorName + "." + itemName,
						"plugins/bedwars/" + arenaName + ".dcnf", null).get(5));
	}

}
