package arena;

import java.util.Arrays;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

	@Override
	public String toString() {
		return "PlayerData [helmet=" + helmet + ", chestplate=" + chestplate + ", leggings=" + leggings + ", boots="
				+ boots + ", location=" + location + ", inventory=" + Arrays.toString(inventory) + ", team=" + team
				+ ", player=" + player + ", status=" + status + "]";
	}

}
