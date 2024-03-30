package inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EMaterial {

	private String page;
	private int index;
	private int amount;
	private boolean openable;
	private Material material;
	private String name;
	private String arenaName;
	private ItemStack item;
	private InventoryAPI inv;

	public EMaterial(int index, int amount, String arenaName, Material material) {
		this.index = index;
		this.amount = amount;
		this.material = material;
		this.arenaName = arenaName;
	}

	public ItemStack item() {
		if (item == null) {
			ItemStack item = new ItemStack(material, index);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			this.item = item;
		}

		return item;
	}

	public Material getMaterial() {
		return material;
	}

	public boolean isOpenable() {
		return openable;
	}

	public void setOpenable(boolean openable) {
		this.openable = openable;
	}

	public int getIndex() {
		return index;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArenaName() {
		return arenaName;
	}

	public InventoryAPI getInv() {
		return inv;
	}

	public int getAmount() {
		return amount;
	}

}
