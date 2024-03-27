package scoreboard;

import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import com.google.common.collect.ImmutableList;

import arena.Arena;
import arena.ArenaManager;
import arena.Chati;
import arena.PropertiesAPI;
import arena.STATES;
import arena.threads.StartedTimer;

public class Scoresex {

	public static void waitingScores(Arena arena) {
		Bukkit.getOnlinePlayers().stream().filter((x) -> ArenaManager.getPlayersArena(x.getName()).equals(arena))
				.forEach((x) -> {
					FastBoard board = new FastBoard(x);
					ImmutableList<String> im = ImmutableList.copyOf(PropertiesAPI.getProperties("waitScores",
							ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf", "&c&lSEXWARS", "&r",
							"{PLAYER}, the match will be started in {TIME}secs"));
					ConcurrentSkipListSet<String> data = new ConcurrentSkipListSet<>();
					Iterator<String> iterate = im.iterator();
					while (iterate.hasNext()) {
						String nstr = Chati.translate(iterate.next());
						nstr.replaceAll("{PLAYER}", x.getName());
						StartedTimer timer = ArenaManager.getPlayersData(x).getStartedTimer();
						nstr.replaceAll("{TIME}", String.valueOf(timer.getCounter()));
						data.add(nstr);

					}

					board.updateTitle(data.first());
					board.updateLines(data.stream().skip(0).collect(Collectors.toList()));
				});
	}

	public static void gameScores(Arena arena) {
		Bukkit.getOnlinePlayers().stream().filter((x) -> ArenaManager.getPlayersArena(x.getName()).equals(arena))
				.forEach((x) -> {
					FastBoard board = new FastBoard(x);
					ImmutableList<String> im = ImmutableList.copyOf(PropertiesAPI.getProperties("gameScores",
							ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf", "&c&lSEXWARS", "&r",
							"RED {RED}", "BLUE {BLUE}"));
					ConcurrentSkipListSet<String> data = new ConcurrentSkipListSet<>();
					Iterator<String> iterate = im.iterator();
					while (iterate.hasNext()) {
						String nstr = Chati.translate(iterate.next());
						nstr.replaceAll("{PLAYER}", x.getName());
						StartedTimer timer = ArenaManager.getPlayersData(x).getStartedTimer();
						nstr.replaceAll("{TIME}", String.valueOf(timer.getCounter()));
						data.add(nstr);

					}

					board.updateTitle(data.first());
					board.updateLines(data.stream().skip(0).collect(Collectors.toList()));
				});
	}

	public static void lobbyScores() {
		Bukkit.getOnlinePlayers().stream().filter((x) -> ArenaManager.getPlayerStatus(x.getName()) == STATES.NONE)
				.forEach((x) -> {
					FastBoard board = new FastBoard(x);
					ImmutableList<String> im = ImmutableList.copyOf(PropertiesAPI.getProperties("lobbyScores",
							ArenaManager.DIR + "messages.dcnf", "&c&lSEXWARS", "&r",
							"Get the fuck in plugins/messages.dcnf", "and set the lobbyScores"));
					ConcurrentSkipListSet<String> data = new ConcurrentSkipListSet<>();
					Iterator<String> iterate = im.iterator();
					while (iterate.hasNext()) {
						String nstr = Chati.translate(iterate.next());
						nstr.replaceAll("{PLAYER}", x.getName());
						StartedTimer timer = ArenaManager.getPlayersData(x).getStartedTimer();
						nstr.replaceAll("{TIME}", String.valueOf(timer.getCounter()));
						data.add(nstr);

					}

					board.updateTitle(data.first());
					board.updateLines(data.stream().skip(0).collect(Collectors.toList()));
				});
	}
}
