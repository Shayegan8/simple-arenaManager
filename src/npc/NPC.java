package npc;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import arena.ArenaManager;
import arena.ArenaTeam;
import arena.Chati;

public class NPC {

	private EntityType type;

	private Location location;

	private String name;

	public void npc(String name, ArenaTeam team, EntityType type, Location location) {
		this.type = type;
		this.location = location;
		this.name = name;
		if (!ArenaManager.SNPCS.containsKey(team) && ArenaManager.SNPCS.containsValue(this)) {

			Entity entity = location.getWorld().spawnEntity(location, type);
			entity.setVelocity(new Vector(0, 0, 0));
			entity.setCustomName(Chati.translate(team.getArena().getName() + " &lSHOPKEEPER"));
			entity.setCustomNameVisible(true);
			ArenaManager.putInSNPCS(team, this);
		}
	}

	public Location getLocation() {
		return location;
	}

	public EntityType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

}
