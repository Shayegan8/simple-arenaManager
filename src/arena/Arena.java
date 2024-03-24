package arena;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Location;

public class Arena {

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
	
	private List<Location> blocks = new ArrayList<>();
		
	private List<Location> storedBlocks = new ArrayList<>();
	
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

	public List<Location> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<Location> blocks) {
		this.blocks = blocks;
	}

	public List<Location> getStoredBlocks() {
		return storedBlocks;
	}

	public void setStoredBlocks(List<Location> storedBlocks) {
		this.storedBlocks = storedBlocks;
	}

}
