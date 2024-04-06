package arena;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Generator {

	private String arenaName;

	private Location location;

	private ItemStack itemStack;

	private int seconds;

	private int amount;

	public Generator(String arenaName, Location location, ItemStack itemStack, int seconds, int amount) {
		this.arenaName = arenaName;
		this.location = location;
		this.itemStack = itemStack;
		this.seconds = seconds;
		this.amount = amount;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public Location getLocation() {
		return location;
	}

	public String getArenaName() {
		return arenaName;
	}

	public int getSeconds() {
		return seconds;
	}

	public int getAmount() {
		return amount;
	}

}
