package inventory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import arena.ArenaManager;
import arena.Chati;
import arena.PropertiesAPI;

public class NPCShop {

	private InventoryAPI inv;
	private String arenaName;

	public NPCShop(int size, Plugin instance, String arenaName) {
		inv = new InventoryAPI(size, arenaName);
		this.arenaName = arenaName;
		String fileName = ArenaManager.DIR + arenaName + "/shop.dcnf";
		try {
			defaultProperties(instance);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			PropertiesAPI.reader(arenaName.toCharArray()).forEach((x) -> {
				String splited[] = x.split(PropertiesAPI.SPLITOR);
				if (splited.length < 2) {

					if (x.contains(":")) {
						String splite[] = x.split(":");
						String item = splite[0];
						String name = Chati.translate(splite[1]);
						String page = splite[2];
						int amount = Integer.parseInt(splite[3]);
						int index = Integer.parseInt(PropertiesAPI.getProperty(splited[0], "0", fileName));
						EMaterial mat = new EMaterial(index, amount, arenaName, Material.valueOf(item));
						if (!page.equals("NULL")) {
							mat.setOpenable(true);
							mat.setPage(page);
						}
						mat.setName(name);
						ArenaManager.addInITEMS(mat);
					}
				}
			});

			ArenaManager.ITEMS.stream().filter((x) -> x.getArenaName().equals(arenaName)).forEach((x) -> {
				InventoryAPI inv = new InventoryAPI(Integer.parseInt(PropertiesAPI.getProperty("size", "53", fileName)),
						x.getInv().getName());
				ItemStack item = new ItemStack(x.getMaterial(), x.getAmount());
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(x.getName());
				meta.setLore(Arrays.asList(""));
				inv.setItem(x.getIndex(), item, meta);
				ArenaManager.addInINVS(inv.getInv());
			});

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public InventoryAPI getInv() {
		return inv;
	}

	public void defaultProperties(Plugin instance) throws IOException {
		String fileName = ArenaManager.DIR + arenaName + "/shop.dcnf";
		if (Files.notExists(Paths.get(fileName))) {
			Files.createFile(Paths.get(fileName));

			PropertiesAPI.setProperty(instance, "ANVIL:&c&lARMORY:armory.dcnf:1", "10", arenaName);
			PropertiesAPI.setProperty(instance, "ANVIL:&6&lPOTIONS:potions.dcnf:1", "11", arenaName);
		}
	}

	public void create() {
		ArenaManager.ITEMS.stream().forEach((x) -> {
			ItemStack item = new ItemStack(x.getMaterial(), x.getIndex());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(x.getName());
			meta.setLore(Arrays.asList(""));
			x.getInv().setItem(x.getIndex(), item, meta);
		});
	}

}
