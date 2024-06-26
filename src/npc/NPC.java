package npc;

import java.util.Objects;

import arena.PropertiesAPI;
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

	public NPC(ArenaTeam team, EntityType type, Location location) {
		if (!ArenaManager.SNPCS.containsKey(team) && ArenaManager.SNPCS.containsValue(this)) {
			this.type = type;
			this.location = location;
			name = team.getTeam().name();
			Entity entity = location.getWorld().spawnEntity(location, type);
			entity.setVelocity(new Vector(0, 0, 0));
			entity.setCustomName(Chati.translate(PropertiesAPI.getProperty("shopiname", team.getArena().getName() + " &lSHOPKEEPER",
					ArenaManager.DIR + team.getArena().getName() + ".dcnf")));
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

	@Override
	public int hashCode() {
		return Objects.hash(location, name, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof NPC)) {
			return false;
		}
		NPC other = (NPC) obj;
		return Objects.equals(location, other.location) && Objects.equals(name, other.name) && type == other.type;
	}

	public String getName() {
		return name;
	}

}
