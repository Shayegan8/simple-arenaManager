package arena;

import java.util.Objects;

import org.bukkit.Location;

public class ArenaTeam {

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
