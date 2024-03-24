package scoreboard;

import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import com.google.common.collect.ImmutableList;

import arena.Arena;
import arena.ArenaManager;
import arena.Chati;
import arena.PropertiesAPI;
import arena.threads.StartedTimer;

public class Scoresex {

	public static void waitingScores(Player player) {
		FastBoard board = new FastBoard(player);
		Arena arena = ArenaManager.getPlayersArena(player.getName());
		ImmutableList<String> im = ImmutableList.copyOf(PropertiesAPI.getProperties_C("waitScores",
				ArenaManager.DIR + arena.getName() + "/" + arena.getName() + ".dcnf", "&c&lSEXWARS", "I dontknow"));
		ConcurrentSkipListSet<String> data = new ConcurrentSkipListSet<>();
		Iterator<String> iterate = im.iterator();
		while (iterate.hasNext()) {
			String nstr = Chati.translate(iterate.next());
			nstr.replaceAll("{PLAYER}", player.getName());
			StartedTimer timer = ArenaManager.getPlayersData(player).getStartedTimer();
			nstr.replaceAll("{TIME}", String.valueOf(timer.getCounter()));
			data.add(nstr);
		}

		board.updateTitle(data.first());
		board.updateLines(data.stream().skip(0).collect(Collectors.toList()));
	}

}
