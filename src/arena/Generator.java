package arena;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Generator {

	private String arenaName;

	private Location location;

	private ItemStack itemStack;

	public Generator(String arenaName, Location location, ItemStack itemStack) {
		this.arenaName = arenaName;
		this.location = location;
		this.itemStack = itemStack;
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

}
