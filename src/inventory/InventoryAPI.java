package inventory;

import java.util.List;

import javax.annotation.Nullable;

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

	public void setDefaultItem(ItemStack defaultItem) {
		this.defaultItem = defaultItem;
	}

	public void setDefaultMeta(ItemMeta meta) {
		this.defaultMeta = meta;
	}

	public InventoryAPI(int length, String name) {
		this.length = length;
		inv = Bukkit.createInventory(null, length, name);
		defaultItem = new ItemStack(Material.STAINED_GLASS_PANE, (short) 1, (byte) 4);
		defaultMeta = defaultItem.getItemMeta();
		defaultMeta.setDisplayName(Chati.translate("&r"));
	}

	public InventoryAPI() {

	}

	public Inventory getInventory() {
		return inv;
	}

	public void setItem(int slot, @Nullable ItemStack item, @Nullable ItemMeta meta, @Nullable int exceptValue) {
		ItemStack itm = item != null ? item : defaultItem;
		itm.setItemMeta(meta != null ? meta : defaultMeta);
		if (exceptValue == 0) {
			inv.setItem(slot, item != null ? item : itm);
		} else if (exceptValue == 1) {
			inv.clear(exceptValue);
		}

	}

	public void setItem(int slot, @Nullable ItemStack item, @Nullable int exceptValue) {
		ItemStack itm = item != null ? item : defaultItem;
		if (exceptValue == 0) {
			inv.setItem(slot, item != null ? item : itm);
		} else if (exceptValue == 1) {
			inv.clear(exceptValue);
		}

	}

	public void setItemDefaultRange(int start, @Nullable ItemStack item, @Nullable ItemMeta meta) {
		for (int i = start; i < length; i++) {
			if (!(i == 53)) {
				setItem(i, item, meta, 0);
			} else {
				setItem(0, item, meta, 0);
				setItem(52, item, meta, 0);
			}
		}

	}

	public void setItemDefaultRange(int start, @Nullable ItemStack item) {
		for (int i = start; i < length; i++) {
			if (!(i == 53)) {
				setItem(i, item, 0);
			} else {
				setItem(0, item, 0);
				setItem(52, item, 0);
			}
		}

	}

	public void setName(ItemStack item, String name) {
		item.getItemMeta().setDisplayName(Chati.translate(name));
	}

	public void setLore(ItemStack item, List<String> lore) {
		item.getItemMeta().setLore(lore);
	}

	public void openInv(@Nullable String playerName) {
		Bukkit.getPlayer(playerName).openInventory(inv);
	}

}
