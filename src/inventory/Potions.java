package inventory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import arena.Arena;
import arena.ArenaManager;
import arena.PropertiesAPI;

public class Potions extends InventoryAPI {

	public static String name = "potions.dcnf";
	private Arena arena;

	public Potions(String arenaName) {
		setInv(new InventoryAPI(
				Integer.parseInt(PropertiesAPI.getProperty("size", "53", ArenaManager.DIR + arenaName + "/" + name)),
				name.split(".")[0]).getInv());
		this.arena = ArenaManager.getArenaByName(arenaName);
	}

	public Inventory getInv() {
		return super.getInv();
	}

	public Arena getArena() {
		return arena;
	}

	public void defaultProperties(Plugin instance) throws IOException {
		String arenaName = arena.getName();
		String fileName = ArenaManager.DIR + arenaName + "/shop.dcnf";
		if (Files.notExists(Paths.get(fileName))) {
			Files.createFile(Paths.get(fileName));

			PropertiesAPI.setProperty(instance, "PAPER:&c&lQuit:1", "63", arenaName);
			PropertiesAPI.setProperty(instance, "ANVIL:&c&lHeal:1", "10", arenaName);
		}

	}

}
