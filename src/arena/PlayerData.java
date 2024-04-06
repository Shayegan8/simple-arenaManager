package arena;

import java.util.Arrays;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import arena.threads.DeathTimer;
import arena.threads.EndedTimer;
import arena.threads.StartedTimer;
import arena.threads.WaitingTimer;

public class PlayerData {

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

	public StartedTimer getStartedTimer() {
		return startedTimer;
	}

	public void setStartedTimer(StartedTimer startedTimer) {
		this.startedTimer = startedTimer;
	}

	public DeathTimer getDeathTimer() {
		return deathTimer;
	}

	public void setDeathTimer(DeathTimer deathTimer) {
		this.deathTimer = deathTimer;
	}

	public EndedTimer getEndedTimer() {
		return endedTimer;
	}

	public void setEndedTimer(EndedTimer endedTimer) {
		this.endedTimer = endedTimer;
	}

	public WaitingTimer getWaitingTimer() {
		return waitingTimer;
	}

	public void setWaitingTimer(WaitingTimer waitingTimer) {
		this.waitingTimer = waitingTimer;
	}

	private StartedTimer startedTimer;
	private DeathTimer deathTimer;
	private EndedTimer endedTimer;
	private WaitingTimer waitingTimer;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(inventory);
		result = prime * result + Objects.hash(boots, chestplate, deathTimer, endedTimer, helmet, leggings, location,
				player, startedTimer, status, team, waitingTimer);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PlayerData)) {
			return false;
		}
		PlayerData other = (PlayerData) obj;
		return Objects.equals(boots, other.boots) && Objects.equals(chestplate, other.chestplate)
				&& Objects.equals(deathTimer, other.deathTimer) && Objects.equals(endedTimer, other.endedTimer)
				&& Objects.equals(helmet, other.helmet) && Arrays.equals(inventory, other.inventory)
				&& Objects.equals(leggings, other.leggings) && Objects.equals(location, other.location)
				&& Objects.equals(player, other.player) && Objects.equals(startedTimer, other.startedTimer)
				&& status == other.status && Objects.equals(team, other.team)
				&& Objects.equals(waitingTimer, other.waitingTimer);
	}

}
