package customs;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import arena.Arena;
import arena.ArenaManager;
import arena.Chati;
import arena.PropertiesAPI;

public class ZombieSpawner implements Listener {

	private String file = ArenaManager.DIR + "customitems/zombiespawner.dcnf";
	private ItemStack sitem = new ItemStack(Material.EGG, 1);
	private ItemStack hand;
	private ItemMeta meta = sitem.getItemMeta();

	public ZombieSpawner() {
		meta.setDisplayName(PropertiesAPI.getProperty("zombieSpawner", "&ZOMBIE &8- &aSPAWN", file));
		meta.setLore(Arrays.asList(""));
		hand = new ItemStack(Material.valueOf(PropertiesAPI.getProperty("hand", "GOLD_AXE", file)), 1);
	}

	@EventHandler
	public void onLanding(PlayerInteractEvent e, ProjectileHitEvent e1) {
		Player player = e.getPlayer();
		Arena arena = ArenaManager.getPlayersArena(player.getName());
		if (player.getItemInHand().equals(sitem)) {
			if (e1.getEntity() instanceof Egg) {
				Entity zombie = Bukkit.getWorld(arena.getWorld()).spawnEntity(e1.getEntity().getLocation(),
						EntityType.ZOMBIE);
				zombie.setCustomName(Chati.translate(PropertiesAPI.getProperty("name", "&c&lFUCKER &e&lMAN", file)));
				zombie.setCustomNameVisible(true);
				Optional<LivingEntity> living = Bukkit.getWorld(file).getLivingEntities().stream()
						.filter((x) -> x.getType() == EntityType.ZOMBIE
								&& x.getLocation().getWorld().getName().equals(arena.getWorld())
								&& zombie.getCustomName().equals(Chati.translate(PropertiesAPI.getProperty("name", "&c&lFUCKER &e&lMAN", file))))
						.findFirst();
				if (living.isPresent()) {
					LivingEntity lv = living.get();
					EntityEquipment eq = lv.getEquipment();
					eq.setItemInHand(hand);
					ConcurrentLinkedQueue<String> armory = PropertiesAPI.getProperties("armory", file, "DIAMOND_HELMET",
							"DIAMOND_CHESTPLATE", "NULL", "NULL");
					armory.stream().forEach((x) -> {
						if (!x.equals("NULL")) {
							ItemStack item = new ItemStack(Material.valueOf(x), 1);

							if (x.contains("HELMET")) {
								eq.setHelmet(item);
							} else if (x.contains("CHESTPLATE")) {
								eq.setChestplate(item);
							} else if (x.contains("LEGGINGS")) {
								eq.setLeggings(item);
							} else if (x.contains("BOOT")) {
								eq.setBoots(item);
							}
						}
					});
					lv.setHealth(50D);
				}
			}
		}
	}

}
