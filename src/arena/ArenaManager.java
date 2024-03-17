package arena;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
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

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MapMaker;
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

class PlayerData {

	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;
	private Location location;
	private ItemStack inventory[];
	private ArenaTeam team;
	private Player player;
	private STATES status;

	public PlayerData(ArenaTeam team, String playerName, STATES status) {
		this.player = Bukkit.getPlayer(playerName);
		this.team = team;
		this.status = status;
	}

	public PlayerData(ArenaTeam team, String playerName, STATES status, ItemStack helmet, ItemStack chestplate,
			ItemStack leggings, ItemStack boots, ItemStack inventory[]) {
		this.player = Bukkit.getPlayer(playerName);
		this.team = team;
		this.status = status;
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
		this.inventory = inventory;
	}

	public ItemStack getHelmet() {
		return helmet;
	}

	public void setHelmet(ItemStack helmet) {
		this.helmet = helmet;
	}

	public ItemStack getChestplate() {
		return chestplate;
	}

	public void setChestplate(ItemStack chestplate) {
		this.chestplate = chestplate;
	}

	public ItemStack getLeggings() {
		return leggings;
	}

	public void setLeggings(ItemStack leggings) {
		this.leggings = leggings;
	}

	public ItemStack getBoots() {
		return boots;
	}

	public void setBoots(ItemStack boots) {
		this.boots = boots;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public STATES getStatus() {
		return status;
	}

	public void setStatus(STATES status) {
		this.status = status;
	}

	public ItemStack[] getInventory() {
		return inventory;
	}

	public void setInventory(ItemStack[] inventory) {
		this.inventory = inventory;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public ArenaTeam getTeam() {
		return team;
	}

	public void setTeam(ArenaTeam team) {
		this.team = team;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(inventory);
		result = prime * result + Objects.hash(boots, chestplate, helmet, leggings, location, player, status, team);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerData other = (PlayerData) obj;
		return Objects.equals(boots, other.boots) && Objects.equals(chestplate, other.chestplate)
				&& Objects.equals(helmet, other.helmet) && Arrays.equals(inventory, other.inventory)
				&& Objects.equals(leggings, other.leggings) && Objects.equals(location, other.location)
				&& Objects.equals(player, other.player) && status == other.status && Objects.equals(team, other.team);
	}

}

class ArenaTeam {

	private Integer minNumber;

	private Integer maxNumber;

	private Location teamSpawn;

	private Location block;

	private Arena arena;

	private STATES teamStatus;

	private TEAMS team;

	public ArenaTeam(Arena arena, int minNumber, int maxNumber, TEAMS team, Location block, Location teamSpawn) {
		this.arena = arena;
		this.minNumber = minNumber;
		this.maxNumber = maxNumber;
		this.team = team;
		this.block = block;
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

	public Location getBlockSpawn() {
		return block;
	}

	public void setBlockSpawn(Location block) {
		this.block = block;
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

	/**
	 * @apiNote thats useless
	 */
	@Override
	public int hashCode() {
		return Objects.hash(arena, block, maxNumber, minNumber, team, teamSpawn, teamStatus);
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
		return Objects.equals(arena, other.arena) && Objects.equals(block, other.block)
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

	/**
	 * @param minPlayer
	 * @param maxPlayer
	 * @param arenaTime
	 * @param waitingSpawn
	 * @param status
	 * @param name
	 * @param world
	 */
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

	public Arena(int minPlayer, int maxPlayer, int arenaTime, Location waitingSpawn, STATES status, String name,
			String world, Location pos1, Location pos2) {
		this.minPlayer = minPlayer;
		this.maxPlayer = maxPlayer;
		this.arenaTime = arenaTime;
		this.waitingSpawn = waitingSpawn;
		this.name = name;
		this.world = world;
		this.status = status;
		this.pos1 = pos1;
		this.pos2 = pos2;
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
		ShitClass.getPlugin(ShitClass.class);
		this.pos2 = pos2;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	/**
	 * @apiNote thats useless
	 */
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

	/**
	 * @apiNote this stores arenas players and status, the status is on first index
	 */
	public final static ListMultimap<Arena, String> ARENAS = ListMultimapBuilder.hashKeys().arrayListValues().build();

	/**
	 * @apiNote this stores players values, first is arena name, second is their
	 *          status and last one is their team
	 */
	public final static Map<String, PlayerData> PLAYERS = new MapMaker().weakKeys().weakValues().makeMap();

	/**
	 * @apiNote this stores npcs by their arenaName
	 */
	public final static ConcurrentMap<String, NPC> NPCS = new MapMaker().weakKeys().weakValues().makeMap();

	/**
	 * @apiNote this stores generators by their arenaName
	 */
	public final static ConcurrentMap<String, Location> GENERATORS = new MapMaker().weakKeys().weakValues().makeMap();

	/**
	 * @apiNote this saves arenas
	 */
	public final static List<Arena> ARENALIST = new ArrayList<>();

	/**
	 * @apiNote this is the arenas directory location
	 */
	public final static String DIR = "plugins/";

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
		NPCS.put(arenaName, npc);
		return npc;
	}

	/**
	 * <p>
	 * this is a safe function
	 * </p>
	 */
	public void spawnNPCS(Plugin plugin, Arena game) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			for (Arena arena : ArenaManager.ARENALIST) {
				NPC npc = ArenaManager.NPCS.get(arena.getName());
				if (game.getWorld().equals(npc.getStoredLocation().getWorld().getName())) {
					List<String> npcProperties = PropertiesAPI.getProperties_NS(game.getName() + "-" + game.getName(),
							ArenaManager.DIR + "npc.dcnf", null);
					String locates[] = npcProperties.get(0).split(",");
					Location location = new Location(Bukkit.getWorld(game.getWorld()), Double.parseDouble(locates[0]),
							Double.parseDouble(locates[1]), Double.parseDouble(locates[2]));
					npc.spawn(location);
				}
			}
		});
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
					NPCS.put(arenaName, npc);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createArena(Plugin instance, String arenaName, Integer minPlayer, Integer maxPlayer,
			Integer arenaTime, Location waitingSpawn, String world) {
		String arenaDir = DIR + arenaName;
		String arenaFile = DIR + arenaName + "/" + arenaName + ".dcnf";
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
		addArenaIn(arena);
	}

	public static void addArenaIn(Arena arena) {
		ARENALIST.add(arena);
	}

	/**
	 * @apiNote this is asynchronously function
	 *          <p>
	 *          it creates a arena with given parameters
	 *          </p>
	 */
	public static CompletableFuture<Void> createArena_S(Plugin instance, String arenaName, Integer minPlayer,
			Integer maxPlayer, Integer arenaTime, Location waitingSpawn, String world) {
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
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				ARENALIST.add(arena);
			});
		});
	}

	/**
	 * <p>
	 * its almost same as setPlayerTeam but it checks if the player is valid in your
	 * arena
	 * </p>
	 */
	public static void selectTeam(Arena arena, String playerName, TEAMS team) {
		if (ARENAS.get(arena).contains(playerName))
			setPlayerTeam(playerName, null);
	}

	/**
	 * @return a arenaTeam by given parameters
	 */
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
		ARENALIST.add(arena);
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

			arenas.add(arena);
		}
		return arenas;
	}

	/**
	 * <p>
	 * this loads all arenas by their directories
	 * </p>
	 */
	public static void loadArenas() {
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
						PropertiesAPI.getProperty_NS("world", null, arenaFile), pos1, pos2);

				ARENALIST.add(arena);
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
	public static Arena getPlayersArena(String name) {
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

	public static ArenaTeam getPlayersTeam(String playerName) {
		Object playerData = PLAYERS.get(playerName);
		if (playerData instanceof PlayerData && playerData != null) {
			PlayerData data = (PlayerData) playerData;
			return data.getTeam();
		}
		return null;
	}

	public static void addPlayer(String playerName, Arena arena, STATES status, ArenaTeam team,
			Location locationToSpawn) {
		Player player = Bukkit.getPlayer(playerName);
		if (player != null && ARENALIST.contains(arena)) {
			ARENAS.get(arena).add(playerName);
			PlayerData data = new PlayerData(team, playerName, status);
			PLAYERS.put(playerName, data);
		}
		if (locationToSpawn != null && player != null)
			player.teleport(locationToSpawn);
	}

	public static void removePlayer(String playerName, Arena arena) {
		ARENAS.remove(arena, playerName);
		PLAYERS.remove(playerName);
	}

	public static STATES getArenaStatus(Arena arena) {
		return STATES.valueOf(ARENAS.get(arena).get(0));
	}

	public static void setArenaStatus(Arena arena, STATES status) {
		ARENAS.get(arena).set(0, status.name());
	}

	public static STATES getPlayerStatus(String playerName) {
		Object data = PLAYERS.get(playerName);
		if (data instanceof PlayerData) {
			PlayerData playerData = (PlayerData) data;
			return playerData.getStatus();
		}
		return null;
	}

	public static Arena getArenaByName(String arenaName) {
		for (Arena arena : ARENALIST)
			if (arena.getName().equals(arenaName))
				return arena;
		return null;
	}

	public static List<String> getTeamsPlayers(Arena arena, TEAMS team) {
		List<String> newLS = new ArrayList<String>();
		for (String playerName : arena.getPlayersNames()) {
			if (getPlayersTeam(playerName).getTeam() == team) {
				newLS.add(playerName);
			}
		}
		return newLS;
	}

	public static Arena getArenaByPlayerAndTeam(String playerName, ArenaTeam team) {
		for (Arena arena : ARENALIST)
			if (arena.equals(team.getArena()) && arena.equals(getPlayersArena(playerName)))
				if (getPlayersTeam(playerName).equals(team))
					return arena;

		return null;
	}

	/**
	 * @return the index of arena in ARENAS
	 */
	public static Integer getPlayerIndexInArena(String playerName, Arena arena) {
		int i = 1;

		while (i < ARENAS.get(arena).size()) {
			i++;
			if (ARENAS.get(arena).get(i).equals(playerName)) {
				return i;
			}
		}
		return null;
	}

	public static String getArenaWorld(Arena arena) {
		if (arena.getWorld() != null) {
			return arena.getWorld();
		} else {
			return PropertiesAPI.getProperty_NS("world", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
		}
	}

	public static void setArenaWorld(Arena arena, String world) {
		arena.setWorld(world);
		PropertiesAPI.setProperty_NS("world", world, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static void setPlayerTeam(String playerName, ArenaTeam team) {
		PlayerData data = new PlayerData(team, playerName, getPlayerStatus(playerName));
		PLAYERS.put(playerName, data);
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
					.getProperty_NS("pos1", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf").split(",");
			return new Location(Bukkit.getWorld(arena.getName()), Integer.parseInt(values[0]),
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
		String values[] = PropertiesAPI.getProperty_NS("pos1", null, DIR + arenaName + "/" + arenaName + ".dcnf")
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
					.getProperty_NS("pos2", null, DIR + arena.getName() + "/" + arena.getName() + ".dcnf").split(",");
			return new Location(Bukkit.getWorld(arena.getName()), Integer.parseInt(values[0]),
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
		String values[] = PropertiesAPI.getProperty_NS("pos2", null, DIR + arenaName + "/" + arenaName + ".dcnf")
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
				.getProperties_NS(generatorName + "." + itemName, DIR + arenaName + "/" + arenaName + ".dcnf", null)
				.stream().filter((x) -> x.contains(",")).map((x) -> x.split("-\\s*", 2)[1].split(",")).forEach((x) -> {
					values[0] = x[0];
					values[1] = x[1];
					values[2] = x[2];
				});
		return new Location(Bukkit.getWorld(itemName), Integer.parseInt(values[0]), Integer.parseInt(values[1]),
				Integer.parseInt(values[2]));
	}

	public static ArenaTeam getTeamByArenaAndPlayer(String playerName, Arena arena) {
		for (Arena a : ARENALIST)
			if (a.equals(arena) && arena.equals(getPlayersArena(playerName)))
				if (getPlayersArena(playerName).equals(arena))
					return getPlayersTeam(playerName);

		return null;
	}

	public static void setTeamSpawn(ArenaTeam team, Location location) {
		team.setTeamSpawn(location);
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".teamspawn",
				location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
				DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf");
	}

	public static Location getTeamSpawn(ArenaTeam team, String world) {
		if (team.getTeamSpawn() != null) {
			return team.getTeamSpawn();
		}
		String values[] = PropertiesAPI.getProperty_NS(team.getTeam().name() + ".spawn", null,
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
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".bedspawn",
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
					.getProperties_NS(team.getTeam().name() + ".bedspawn",
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

	public static Location getWaitingSpawn(String arenaName, String worldName) {

		String ls[] = new String[3];
		PropertiesAPI.getProperties_NS("waiting", DIR + arenaName + "/" + arenaName + ".dcnf", null).stream()
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

	public static Integer getMaxArena(Arena arena) {
		return arena.getMaxPlayer() != null ? arena.getMaxPlayer()
				: Integer.parseInt(PropertiesAPI.getProperty_NS("max", null,
						DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
	}

	public static void setMinArena(Arena arena, String number) {
		arena.setMinPlayers(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS("min", number, DIR + arena.getName() + "/" + arena.getName() + ".dcnf");
	}

	public static Integer getMinArena(Arena arena) {
		return arena.getMinPlayer() != null ? arena.getMinPlayer()
				: Integer.parseInt(PropertiesAPI.getProperty_NS("min", null,
						DIR + arena.getName() + "/" + arena.getName() + ".dcnf"));
	}

	public static void setTeamMax(ArenaTeam team, String number) {
		team.setMaxNumber(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".min", number,
				DIR + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
	}

	public static Integer getTeamMax(ArenaTeam team) {
		return team.getMaxNumber() != null ? team.getMaxNumber()
				: Integer.parseInt(PropertiesAPI.getProperty_NS(team.getTeam().name() + ".max", null,
						DIR + team.getArena().getName() + "/" + team.getArena().getName() + ".dcnf"));
	}

	public static void setTeamMin(ArenaTeam team, String number) {
		team.setMinNumber(Integer.parseInt(number));
		PropertiesAPI.setProperty_NS(team.getTeam().name() + ".max", number,
				DIR + team.getTeam().name() + "/" + team.getTeam().name() + ".dcnf");
	}

	public static Integer getTeamMin(ArenaTeam team) {
		return team.getMinNumber() != null ? team.getMinNumber()
				: Integer.parseInt(PropertiesAPI.getProperty_NS(team.getTeam().name() + ".min", null,
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

}