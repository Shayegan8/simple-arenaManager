package arena;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;

enum STATES {
	STARTED, ENDED, WAITING, INPROCESS, RUNNING
}

enum TEAMS {
	RED, BLUE, GREEN, YELLOW, ORANGE, BLACK, PINK, PURPLE, BROWN, WHITE
}

class ArenaTeam {

	private Integer minNumber;

	private Integer maxNumber;

	private Location teamSpawn;

	private Location bedSpawn;

	private Arena arena;

	private STATES teamStatus;

	private TEAMS team;

	public ArenaTeam(Arena arena, int minNumber, int maxNumber, TEAMS team, Location bedSpawn, Location teamSpawn) {
		this.arena = arena;
		this.minNumber = minNumber;
		this.maxNumber = maxNumber;
		this.team = team;
		this.bedSpawn = bedSpawn;
		this.teamSpawn = teamSpawn;
	}

	public Integer getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(int maxNumber) {
		this.maxNumber = maxNumber;
	}

	public Integer getMinNumber() {
		return minNumber;
	}

	public void setMinNumber(int minNumber) {
		this.minNumber = minNumber;
	}

	public TEAMS getTeam() {
		return team;
	}

	public void setTeam(TEAMS team) {
		this.team = team;
	}

	public Location getTeamSpawn() {
		return teamSpawn;
	}

	public void setTeamSpawn(Location teamSpawn) {
		this.teamSpawn = teamSpawn;
	}

	public Location getBedSpawn() {
		return bedSpawn;
	}

	public void setBedSpawn(Location bedSpawn) {
		this.bedSpawn = bedSpawn;
	}

	public STATES getTeamStatus() {
		return teamStatus;
	}

	public void setTeamStatus(STATES teamStatus) {
		this.teamStatus = teamStatus;
	}

	public Arena getArena() {
		return arena;
	}

	@Override
	public int hashCode() {
		return Objects.hash(arena, bedSpawn, maxNumber, minNumber, team, teamSpawn, teamStatus);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ArenaTeam)) {
			return false;
		}
		ArenaTeam other = (ArenaTeam) obj;
		return Objects.equals(arena, other.arena) && Objects.equals(bedSpawn, other.bedSpawn)
				&& Objects.equals(maxNumber, other.maxNumber) && Objects.equals(minNumber, other.minNumber)
				&& team == other.team && Objects.equals(teamSpawn, other.teamSpawn) && teamStatus == other.teamStatus;
	}

}

class Arena {

	private STATES status;

	private String world;

	private String name;

	private Integer minPlayer;

	private Integer maxPlayer;

	private Integer arenaTime;

	private Location waitingSpawn;

	private Location pos1;

	private Location pos2;

	private List<String> playersNames = new ArrayList<>();

	private List<TEAMS> teams = new ArrayList<>();

	public Arena(int minPlayer, int maxPlayer, int arenaTime, Location waitingSpawn, STATES status, String name,
			String world) {
		this.minPlayer = minPlayer;
		this.maxPlayer = maxPlayer;
		this.arenaTime = arenaTime;
		this.waitingSpawn = waitingSpawn;
		this.name = name;
		this.world = world;
		this.status = status;
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

	public Integer getMinPlayer() {
		return minPlayer;
	}

	public void setMinPlayers(int minPlayer) {
		this.minPlayer = minPlayer;
	}

	public Integer getMaxPlayer() {
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

	public Integer getArenaTime() {
		return arenaTime;
	}

	public void setArenaTime(int arenaTime) {
		this.arenaTime = arenaTime;
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

	public Location getPos1() {
		return pos1;
	}

	public void setPos1(Location pos1) {
		this.pos1 = pos1;
	}

	public Location getPos2() {
		return pos2;
	}

	public void setPos2(Location pos2) {
		this.pos2 = pos2;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	@Override
	public int hashCode() {
		return Objects.hash(arenaTime, maxPlayer, minPlayer, name, playersNames, pos1, pos2, status, teams,
				waitingSpawn, world);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Arena)) {
			return false;
		}
		Arena other = (Arena) obj;
		return Objects.equals(arenaTime, other.arenaTime) && Objects.equals(maxPlayer, other.maxPlayer)
				&& Objects.equals(minPlayer, other.minPlayer) && Objects.equals(name, other.name)
				&& Objects.equals(playersNames, other.playersNames) && Objects.equals(pos1, other.pos1)
				&& Objects.equals(pos2, other.pos2) && status == other.status && Objects.equals(teams, other.teams)
				&& Objects.equals(waitingSpawn, other.waitingSpawn) && Objects.equals(world, other.world);
	}

}

/**
 * @author shayegan8
 */
public class ArenaManager {

	public final static ListMultimap<Arena, String> ARENAS = ListMultimapBuilder.hashKeys().arrayListValues().build();

	public final static ListMultimap<String, Object> PLAYERS = ListMultimapBuilder.hashKeys().arrayListValues().build();

	public final static ConcurrentMap<String, Location> NPCS = new MapMaker().weakKeys().weakValues().makeMap();

	public final static ConcurrentMap<String, Location> GENERATORS = new MapMaker().weakKeys().weakValues().makeMap();

	public final static List<Arena> ARENALIST = new ArrayList<>();

	public static String directory = "plugins/";

	public static void setDirectory(String directory) {
		ArenaManager.directory = directory;
	}

	public static void loadNPCS_NS() {

	}

	public static void createArena(Plugin instance, String arenaName, Integer minPlayer, Integer maxPlayer,
			Integer arenaTime, Location waitingSpawn, String world) {
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

			int min = minPlayer != null ? minPlayer
					: Integer.parseInt(PropertiesAPI.getProperty_NS("minPlayers", "2", arenaFile));
			int max = maxPlayer != null ? maxPlayer
					: Integer.parseInt(PropertiesAPI.getProperty_NS("maxPlayers", "8", arenaFile));
			int time = arenaTime != null ? arenaTime
					: Integer.parseInt(PropertiesAPI.getProperty_NS("arenaTime", "1800", arenaFile));

			List<String> locationCoordinates = PropertiesAPI.getProperties_NNS("waitingSpawn", arenaFile, "0.0", "0.0",
					"0.0");

			Location location = waitingSpawn != null ? waitingSpawn
					: new Location(Bukkit.getWorld(world), Double.parseDouble(locationCoordinates.get(0)),
							Double.parseDouble(locationCoordinates.get(1)),
							Double.parseDouble(locationCoordinates.get(2)));

			ARENALIST.add(new Arena(min, max, time, location, STATES.INPROCESS, arenaFile, world));

		});
	}

	public static void selectTeam(Arena arena, String playerName, TEAMS team) {
		if (ARENAS.get(arena).contains(playerName))
			setPlayerTeam(playerName, null);
	}

	public static ArenaTeam createTeam(Arena arena, String world, TEAMS teamm, String arenaFile) {

		int min = Integer.parseInt(PropertiesAPI.getProperty_NS(teamm.name() + ".min", "1", arenaFile));
		int max = Integer.parseInt(PropertiesAPI.getProperty_NS(teamm.name() + "max", "2", arenaFile));

		String coordinates[] = PropertiesAPI.getProperty_NS(teamm.name() + ".bedspawn", null, arenaFile).split(",");
		Location bed = new Location(Bukkit.getWorld(world), Double.parseDouble(coordinates[0]),
				Double.parseDouble(coordinates[1]), Double.parseDouble(coordinates[2]));
		String ncoordinates[] = PropertiesAPI.getProperty_NS(teamm.name() + ".teamspawn", null, arenaFile).split(",");
		Location spawn = new Location(Bukkit.getWorld(world), Double.parseDouble(ncoordinates[0]),
				Double.parseDouble(ncoordinates[1]), Double.parseDouble(ncoordinates[2]));

		return (new ArenaTeam(arena, min, max, teamm, bed, spawn));
	}

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

				int min = Integer.parseInt(PropertiesAPI.getProperty_NS("min", "2", arenaFile));
				int max = Integer.parseInt(PropertiesAPI.getProperty_NS("max", "8", arenaFile));
				int time = Integer.parseInt(PropertiesAPI.getProperty_NS("arenaTime", "1800", arenaFile));

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

				Arena arena = new Arena(min, max, time, location, STATES.WAITING, arenaName,
						PropertiesAPI.getProperty_NS("world", null, arenaFile));
				arena.setPos1(pos1);
				arena.setPos2(pos2);

				ARENALIST.add(arena);
			}
		}
	}

	public static List<Player> getArenaPlayers(Arena arena) {
		return ARENAS.get(arena).stream().map((x) -> Bukkit.getPlayer(x)).collect(Collectors.toList());
	}

	public static Arena getPlayersArena_NS(String name) {
		for (Arena arena : ARENALIST) {
			List<String> ls = ARENAS.get(arena);
			ls.remove(0);
			for (String playerName : ls) {
				if (playerName.equals(name)) {
					return arena;
				}
			}
		}
		return null;
	}

	public static void addPlayer(String playerName, Arena arena, STATES status, ArenaTeam team,
			Location locationToSpawn) {
		Player player = Bukkit.getPlayer(playerName);
		if (player != null && ARENALIST.contains(arena)) {
			ARENAS.get(arena).add(playerName);
			PLAYERS.put(playerName, arena.getName());
			PLAYERS.put(playerName, status);
			PLAYERS.put(playerName, team);
		}
		if (locationToSpawn != null && player != null)
			player.teleport(locationToSpawn);
	}

	public static void removePlayer(String playerName, Arena arena) {
		ARENAS.remove(arena, playerName);
		PLAYERS.removeAll(playerName);
	}

	public static STATES getArenaStatus(Arena arena) {
		return STATES.valueOf(ARENAS.get(arena).get(0));
	}

	public static Arena getPlayerArena(String playerName) {
		return (Arena) PLAYERS.get(playerName).get(0);
	}

	public static STATES getPlayerStatus(String playerName) {
		return STATES.valueOf(PLAYERS.get(playerName).get(1).toString());
	}

	public static ArenaTeam getPlayerTeam(String playerName) {
		return (ArenaTeam) PLAYERS.get(playerName).get(2);
	}

	public static Arena getArenaByName_NS(String arenaName) {
		for (Arena arena : ARENALIST)
			if (arena.getName().equals(arenaName))
				return arena;
		return null;
	}

	public static List<String> getTeamsPlayers_NS(Arena arena, TEAMS team) {
		List<String> newLS = new ArrayList<String>();
		for (String playerName : arena.getPlayersNames()) {
			if (getPlayerTeam(playerName).getTeam() == team) {
				newLS.add(playerName);
			}
		}
		return newLS;
	}

	public static Arena getArenaByPlayerAndTeam_NS(String playerName, ArenaTeam team) {
		for (Arena arena : ARENALIST)
			if (arena.equals(team.getArena()) && arena.equals(getPlayerArena(playerName)))
				if (getPlayerTeam(playerName).equals(team))
					return arena;

		return null;
	}

	public static Integer getPlayerIndexInArena_NS(String playerName, Arena arena) {
		int i = 1;

		while (i < ARENAS.get(arena).size()) {
			i++;
			if (ARENAS.get(arena).get(i).equals(playerName)) {
				return i;
			}
		}
		return null;
	}

	public static String getArenaWorld_NS(Arena arena) {
		if (arena.getWorld() != null) {
			return arena.getWorld();
		} else {
			return PropertiesAPI.getProperty_NS("world", null,
					directory + arena.getName() + "/" + arena.getName() + ".dcnf");
		}
	}

	public static void setArenaWorld_NS(Arena arena, String world) {
		arena.setWorld(world);
		PropertiesAPI.setProperty_NS("world", world, directory + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static void setPlayerTeam(String playerName, ArenaTeam team) {
		PLAYERS.get(playerName).set(2, team);
	}

	public static void setPos1_NS(Arena arena, Location location) {
		arena.setPos1(location);
		PropertiesAPI.setProperty_NS("pos1",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				directory + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static void setPos2_NS(Arena arena, Location location) {
		arena.setPos1(location);
		PropertiesAPI.setProperty_NS("pos2",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				directory + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static Location getPos1_NS(Arena arena) {
		if (arena.getPos1() != null) {
			return arena.getPos1();
		} else {
			String values[] = PropertiesAPI
					.getProperty_NS("pos1", null, directory + arena.getName() + "/" + arena.getName() + ".dcnf")
					.split(",");
			return new Location(Bukkit.getWorld(arena.getName()), Integer.parseInt(values[0]),
					Integer.parseInt(values[1]), Integer.parseInt(values[2]));
		}
	}

	public static Location getPos2_NS(Arena arena) {
		if (arena.getPos1() != null) {
			return arena.getPos1();
		} else {
			String values[] = PropertiesAPI
					.getProperty_NS("pos2", null, directory + arena.getName() + "/" + arena.getName() + ".dcnf")
					.split(",");
			return new Location(Bukkit.getWorld(arena.getName()), Integer.parseInt(values[0]),
					Integer.parseInt(values[1]), Integer.parseInt(values[2]));
		}
	}

	public static void setGenerator_NS(Location location, String arenaName, String generatorName, String itemName,
			String secounds, String amount) {
		PropertiesAPI.setProperties_NS(true, generatorName + "." + itemName,
				directory + arenaName + "/" + arenaName + ".dcnf",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(), secounds, amount);
	}

	public static Location getGeneratorLocation_NS(String arenaName, String generatorName, String itemName) {
		String values[] = new String[3];
		PropertiesAPI
				.getProperties_NS(generatorName + "." + itemName, directory + arenaName + "/" + arenaName + ".dcnf",
						null)
				.stream().filter((x) -> x.contains(",")).map((x) -> x.split("-\\s*", 2)[1].split(",")).forEach((x) -> {
					values[0] = x[0];
					values[1] = x[1];
					values[2] = x[2];
				});
		return new Location(Bukkit.getWorld(itemName), Integer.parseInt(values[0]), Integer.parseInt(values[1]),
				Integer.parseInt(values[2]));
	}

	public static ArenaTeam getTeamByArenaAndPlayer_NS(String playerName, Arena arena) {
		for (Arena a : ARENALIST)
			if (a.equals(arena) && arena.equals(getPlayerArena(playerName)))
				if (getPlayerArena(playerName).equals(arena))
					return getPlayerTeam(playerName);

		return null;
	}

	public static void setTeamSpawn_NS(ArenaTeam team, Location location) {
		team.setTeamSpawn(location);
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".teamspawn",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				directory + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
	}

	public static Location getTeamSpawn_NS(ArenaTeam team, String world) {
		if (team.getTeamSpawn() != null) {
			return team.getTeamSpawn();
		}
		String values[] = PropertiesAPI.getProperty_NS(team.getTeam().name() + ".spawn", null,
				directory + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf").split(",");
		return new Location(Bukkit.getWorld(world), Double.parseDouble(values[0]), Double.parseDouble(values[1]),
				Double.parseDouble(values[2]));
	}

	public static void setBedSpawn_NS(ArenaTeam team, Location location) {
		team.setBedSpawn(location);
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".bedspawn",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				directory + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
	}

	public static Location getBedSpawn_NS(ArenaTeam team) {
		if (team.getBedSpawn() != null) {
			return team.getBedSpawn();
		} else {
			String ls[] = new String[3];
			PropertiesAPI
					.getProperties_NS(team.getTeam().name() + ".bedspawn",
							directory + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf", null)
					.stream().map((x) -> x.split("-\\s*", 2)[1].split(",")).forEach((x) -> {
						ls[0] = x[0];
						ls[1] = x[1];
						ls[2] = x[2];
					});
			return new Location(Bukkit.getWorld(team.getArena().getName()), Integer.parseInt(ls[0]),
					Integer.parseInt(ls[1]), Integer.parseInt(ls[2]));
		}
	}

	public static void setMaxArena_NS(Arena arena, String number) {
		arena.setMaxPlayers(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS("max", number, directory + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static Integer getMaxArena_NS(Arena arena) {
		return arena.getMaxPlayer() != null ? arena.getMaxPlayer()
				: Integer.parseInt(PropertiesAPI.getProperty_NS("max", null,
						directory + arena.getName() + "/" + arena.getName() + ".dcnf"));
	}

	public static void setMinArena_NS(Arena arena, String number) {
		arena.setMinPlayers(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS("min", number, directory + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static Integer getMinArena_NS(Arena arena) {
		return arena.getMinPlayer() != null ? arena.getMinPlayer()
				: Integer.parseInt(PropertiesAPI.getProperty_NS("min", null,
						directory + arena.getName() + "/" + arena.getName() + ".dcnf"));
	}

	public static void setTeamMax_NS(ArenaTeam team, String number) {
		team.setMaxNumber(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".min", number,
				directory + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
	}

	public static Integer getTeamMax_NS(ArenaTeam team) {
		return team.getMaxNumber() != null ? team.getMaxNumber()
				: Integer.parseInt(PropertiesAPI.getProperty_NS(team.getTeam().name() + ".max", null,
						directory + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf"));
	}

	public static void setTeamMin_NS(ArenaTeam team, String number) {
		team.setMinNumber(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".max", number,
				directory + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
	}

	public static Integer getTeamMin_NS(ArenaTeam team) {
		return team.getMinNumber() != null ? team.getMinNumber()
				: Integer.parseInt(PropertiesAPI.getProperty_NS(team.getTeam().name() + ".min", null,
						directory + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf"));
	}

	public static boolean isEntityOnRegion(Arena arena, Location entityLocation) {
		Location pos1 = getPos1_NS(arena);
		Location pos2 = getPos2_NS(arena);
		return (entityLocation.getX() >= Math.min(pos1.getX(), pos2.getX())
				&& entityLocation.getY() >= Math.min(pos1.getY(), pos2.getY())
				&& entityLocation.getZ() >= Math.min(pos1.getZ(), pos2.getZ())
				&& entityLocation.getX() <= Math.max(pos1.getX(), pos2.getX())
				&& entityLocation.getY() <= Math.max(pos1.getY(), pos2.getY())
				&& entityLocation.getZ() <= Math.max(pos1.getZ(), pos2.getZ())) ? true : false;
	}

	public static boolean isBedExists(ArenaTeam team) {
		return (!(getBedSpawn_NS(team).getBlock().getType() == Material.BED)) ? true : false;
	}
}