package npc;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import arena.Arena;
import arena.Chati;

public class NPC {

	public static void npc(EntityType npcType, Location location, Arena arena) {
		Entity entity = location.getWorld().spawnEntity(location, npcType);
		entity.setVelocity(new Vector(0, 0, 0));
		entity.setCustomName(Chati.translate(arena.getName() + " &lSHOPKEEPER"));
		entity.setCustomNameVisible(true);
	}

}
