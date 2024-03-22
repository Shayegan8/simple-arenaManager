package arena;

import java.io.IOException;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@ArenaMaker(arenas = { "firstArena", "lastArena" })
public class Example extends JavaPlugin implements CommandExecutor {

	private static Example instance;
	private static List<Arena> savedARENALIST;
	private Arena myCustomArena;

	public void onEnable() {
		instance = this;
		getCommand("createMyArena").setExecutor(this);
		ArenaManager.loadArenas(); // loading arenas by reading configs
		savedARENALIST = ArenaManager.loadArenasByAnnotation(getClass()); // loading arenas by reading configs but just
																			// the arenas that are in ArenaMaker
																			// annotations
		try {
			ArenaManager.loadGenerators("firstArena");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ArenaManager.loadNPCS("lastArena");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (sender.hasPermission("arena.special")) {
				myCustomArena = ArenaManager.createArena("myCustomArena", 2, 8, 10000, player.getLocation(), "world");
				return true;
			} else if (sender.hasPermission("arena.bullshit")) {
				myCustomArena = ArenaManager.arena(0, 0, 0, player.getLocation(), STATES.INPROCESS, "kossher", "world");
			}
		}

		return false;
	}

	public static List<Arena> getSavedARENALIST() {
		return savedARENALIST;
	}

	public static Example getInstance() {
		return instance;
	}

	public Arena getMyCustomArena() {
		return myCustomArena;
	}

}