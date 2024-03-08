import java.util.List;

@ArenaMaker(arena = "arenaName")
public class ArenaExample {

	private Arena arena;

	public Arena getArena() {
		return arena;
	}

	public void load() {
		Arena arena = ArenaManager.loadArena("arenaName", "arenaFile");
		this.arena = arena;
	}

}

class ArenaWithNoAnnotation {

	private Arena arena;

	public Arena getArena() {
		return arena;
	}

	public void load() {
		Arena arena = new Arena(2, 4, 300000, ArenaManager.getWaitingSpawn("arenaName", "worldName"), STATES.WAITING,
				"arenaName", "worldName", ArenaManager.getPos1("arenaName", "worldName"),
				ArenaManager.getPos2("arenaName", "arenaName"));
		this.arena = arena;
		ArenaManager.ARENALIST.add(arena);
	}

}

@ArenaMaker(arenas = { "arena1", "arena2" })
class Arenas {

	private final static List<Arena> ARENAS = ArenaManager.loadArenasByAnnotation(Arenas.class);

}
