package inventory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Material;
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

					EMaterial mat = null;

					if (x.contains(":")) {
						String splite[] = x.split(":");
						String item = splite[0];
						String name = Chati.translate(splite[1]);
						String page = splite[2];
						int index = Integer.parseInt(PropertiesAPI.getProperty(splited[0], "0", fileName));
						mat = new EMaterial(index, arenaName, Material.valueOf(item));
						if (!page.equals("NULL")) {
							mat.setOpenable(true);
							mat.setPage(page);
						}
						mat.setName(name);
					}
					ArenaManager.addInITEMS(mat);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public InventoryAPI getInv() {
		return inv;
	}

	/**
	 * <p>
	 * This is work for empty files
	 * </p>
	 * 
	 * @throws IOException
	 */
	public void defaultProperties(Plugin instance) throws IOException {
		String fileName = ArenaManager.DIR + arenaName + "/shop.dcnf";
		if (Files.notExists(Paths.get(fileName)))
			Files.createFile(Paths.get(fileName));

		ConcurrentLinkedQueue<String> lnk = new ConcurrentLinkedQueue<>(Files.readAllLines(Paths.get(arenaName)));
		if (lnk == null || lnk.isEmpty() || lnk.peek().equals("")) {
			PropertiesAPI.setProperty(instance, "ANVIL:&c&lARMORY:armory.dcnf", "10", arenaName);
		}

	}

}
