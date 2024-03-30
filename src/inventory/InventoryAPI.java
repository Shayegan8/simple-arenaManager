package inventory;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import arena.Chati;

public class InventoryAPI {

	private Inventory inv;
	private ItemStack defaultItem;
	private ItemMeta defaultMeta;
	private int length;
	private String name;

	public Inventory getInv() {
		return inv;
	}

	public void setInv(Inventory inv) {
		this.inv = inv;
	}

	public void setDefaultItem(ItemStack defaultItem) {
		this.defaultItem = defaultItem;
	}

	public void setDefaultMeta(ItemMeta meta) {
		this.defaultMeta = meta;
	}

	public InventoryAPI(int length, String name) {
		this.length = length;
		this.name = name;
		inv = Bukkit.createInventory(null, length, name);
		defaultItem = new ItemStack(Material.STAINED_GLASS_PANE, (short) 1, (byte) 4);
		defaultMeta = defaultItem.getItemMeta();
		defaultMeta.setDisplayName(Chati.translate("&r"));
	}

	public InventoryAPI() {

	}

	public void setItem(int slot, ItemStack item, ItemMeta meta) {
		ItemStack itm = item != null ? item : defaultItem;
		itm.setItemMeta(meta != null ? meta : defaultMeta);
		inv.setItem(slot, item != null ? item : itm);

	}

	public void setItem(int slot, ItemStack item) {
		ItemStack itm = item != null ? item : defaultItem;
		inv.setItem(slot, item != null ? item : itm);
	}

	public void removeItem(int slot) {
		inv.clear(slot);
	}

	public void setItemDefaultRange(int start, ItemStack item, ItemMeta meta) {
		for (int i = start; i < length; i++) {
			if (!(i == 53)) {
				setItem(i, item, meta);
			} else {
				setItem(0, item, meta);
				setItem(52, item, meta);
			}
		}

	}

	public void setItemDefaultRange(int start, ItemStack item) {
		for (int i = start; i < length; i++) {
			if (!(i == 53)) {
				setItem(i, item);
			} else {
				setItem(0, item);
				setItem(52, item);
			}
		}

	}

	public void setName(ItemStack item, String name) {
		item.getItemMeta().setDisplayName(Chati.translate(name));
	}

	public void setLore(ItemStack item, List<String> lore) {
		item.getItemMeta().setLore(lore);
	}

	public void openInv(String playerName) {
		Bukkit.getPlayer(playerName).openInventory(inv);
	}

	public String getName() {
		return name;
	}

}
