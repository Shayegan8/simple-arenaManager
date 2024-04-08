package arena;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.scheduler.BukkitRunnable;

/* 
 * @author shayegan8
 * 
 */
public class PropertiesAPI {

	public static String alphabets[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "k", "j", "l", "m", "n", "o", "p",
			"q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
	public static final String SPLITOR = "@";
	public static final String LIST_SPLITOR = " - ";
	public static Plugin instance = null;

	public static void init_plugin(String pluginName) {
		instance = Bukkit.getPluginManager().getPlugin(pluginName);
	}


	public static String[] getAlphabets() {
		return PropertiesAPI.alphabets;
	}

	public static void setProperties(boolean check, String key, String fileName, String... args) {
		if (check) {
			if (Files.notExists(Paths.get(fileName))) {
				try {
					Files.createFile(Paths.get(fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			int i = 0;
			try (FileWriter writer = new FileWriter(fileName, true)) {
				writer.write("\n" + "* " + key + "\n");
				while (i < args.length) {
					writer.write(i + LIST_SPLITOR + args[i] + "\n");
					writer.flush();
					i++;
				}
				writer.write("* endif " + key);
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

	public static Stream<String> reader(char[] fileName) throws IOException {
		FReader reader = new FReader(new String(fileName));
		reader.runTaskAsynchronously(instance);
		return reader.getCompletedLNK().stream();
	}

	private static void setPropertyProcess(String key, String value, String fileName) {
		try (FileWriter writer = new FileWriter(fileName, true)) {

			writer.write("\n" + key + SPLITOR + value + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setProperty(String key, String value, String fileName) throws IOException {
		FReader reader = new FReader(fileName);
		reader.runTaskAsynchronously(instance);
		ConcurrentLinkedQueue<String> lnk = reader.getCompletedLNK();

		if (!lnk.isEmpty()) {
			if (lnk.contains(key + SPLITOR + value)) {
				Iterator<String> iterate = lnk.iterator();
				while (iterate.hasNext()) {
					String nexti = iterate.next();
					if (nexti.equals(key + SPLITOR + value)) {
						removeProperty(nexti, fileName);
					}
					setPropertyProcess(key, value, fileName);
				}
			} else {
				setPropertyProcess(key, value, fileName);
			}
		}
	}

	public static void removeProperty(String key, String fileName) {
		try {
			removePropertyProcess(key, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void removePropertyProcess(String key, String fileName) throws IOException {
		FReader reader = new FReader(fileName);
		reader.runTaskAsynchronously(instance);
		ConcurrentLinkedQueue<String> lnk = reader.getCompletedLNK();

		if (Files.exists(Paths.get(fileName))) {
			Iterator<String> iterate = lnk.iterator();
			while (iterate.hasNext()) {
				String nexti = iterate.next();
				if (nexti.contains(key)) {
					lnk.remove(nexti);
					break;
				}
			}
		}
		try {
			Files.delete(Paths.get(fileName));
			Files.createFile(Paths.get(fileName));
			try (FileWriter writer = new FileWriter(fileName)) {
				for (String line : lnk) {
					writer.write(line);
					writer.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void showProperty(Player player, String key, String defaultValue, String fileName) {
		player.sendMessage(PlaceholderAPI.setPlaceholders(player,
				ChatColor.translateAlternateColorCodes('&', getProperty(key, defaultValue, fileName))));
	}

	public static void showProperties(Player player, String key, String fileName,
			String... defaultValues) throws IOException {
		FReader producer = new FReader(fileName);
		producer.runTaskAsynchronously(instance);
		bgetListPropertiesProcess(null, key, fileName, producer.getCompletedLNK(), defaultValues).stream().forEach((x) -> {
			player.sendMessage(PlaceholderAPI.setPlaceholders(player,
					ChatColor.translateAlternateColorCodes('&', getProperty(key, x, fileName))));
		});

	}

	public static String getProperty(String key, String defaultValue, String fileName) {
		FReader producer = new FReader(fileName);
		producer.runTaskAsynchronously(instance);
		Optional<String> retrn = producer.getCompletedLNK().stream()
				.filter(x -> x.contains(key + SPLITOR) && x.split(SPLITOR).length == 2).findFirst();
		if (retrn.isPresent()) {
			return retrn.get().split(SPLITOR)[1];
		} else {
			return defaultValue;
		}
    }

	public static String getProperty(final String SPLITOR, String key, String defaultValue, String fileName) {
			FReader reader = new FReader(fileName);
			reader.runTaskAsynchronously(instance);
			Optional<String> retrn = reader.getCompletedLNK().stream()
					.filter(x -> x.contains(key + SPLITOR) && x.split(SPLITOR).length == 2).findFirst();
			if (retrn.isPresent()) {
				return retrn.get().split(SPLITOR)[1];
			} else {
				return defaultValue;
			}
	}

	public static ConcurrentLinkedQueue<String> getProperties(String key, String fileName, String... defaultValues) {
		try {
			FReader reader = new FReader(fileName);
			reader.runTask(instance);
			return bgetListPropertiesProcess(null, key, fileName, reader.getCompletedLNK(), defaultValues);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ConcurrentLinkedQueue<String> getProperties(final char splitor[], String key, String fileName,
			String... defaultValues) {
		try {
			FReader reader = new FReader(fileName);
			reader.runTaskAsynchronously(instance);

			return bgetListPropertiesProcess(new String(splitor), key, fileName, reader.getCompletedLNK(), defaultValues);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static int indexOf(ConcurrentLinkedQueue<String> lnk, String str) {
		int index = 0;
		Iterator<String> iterate = lnk.iterator();
		while (iterate.hasNext()) {
			String next = iterate.next();
			index += 1;
			if(next.equals(str))
				break;
		}
		return index;
	}

	public static ConcurrentLinkedQueue<String> bgetListPropertiesProcess(String splitor, String key, String fileName,
			ConcurrentLinkedQueue<String> allLines, String... defaultValues) throws IOException {

		FReader reader = new FReader(fileName);
		reader.runTask(instance);
		ConcurrentLinkedQueue<String> lsi = new ConcurrentLinkedQueue<>();

		int start = indexOf(reader.getCompletedLNK(), "* " + key) + 1;

		int end = indexOf(reader.getCompletedLNK(), "* endif " + key) - 1;

			while (start <= end) {
				int finalStart = start;
				reader.getCompletedLNK().stream().filter((x) -> indexOf(reader.getCompletedLNK(), x) == finalStart).forEach(lsi::add);
				start++;
			}

		return new ConcurrentLinkedQueue<>(
				lsi.stream().map((x) -> x.split(splitor != null ? splitor : " - ")[1]).collect(Collectors.toList()));
	}

}

class FReader extends BukkitRunnable {

	private ConcurrentLinkedQueue<String> lnk = new ConcurrentLinkedQueue<>();
	private String fileName;

	public FReader(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void run() {
        try {
            for(String line : Files.readAllLines(Paths.get(fileName))) {
				lnk.add(line);
            }
        } catch (IOException e) {
			e.printStackTrace();
        }
    }

	public ConcurrentLinkedQueue<String> getCompletedLNK() {
		return lnk;
	}
}