package arena;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.PlaceholderAPI;

/* 
 * @author shayegan8
 * 
 */
public class PropertiesAPI {

	public static String alphabets[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "k", "j", "l", "m", "n", "o", "p",
			"q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
	public static final String SPLITOR = "@";
	public static final String LIST_SPLITOR = " - ";

	public static int getByID_NS(String str, String fileName) {
		int n = 0;

		try {
			while (n < Files.readAllLines(Paths.get(fileName)).size()) {
				if (Files.readAllLines(Paths.get(fileName)).get(n).equalsIgnoreCase(str)) {
					return n;
				}
				n++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static String[] getAlphabets() {
		return PropertiesAPI.alphabets;
	}

	public static void setProperties(Plugin instance, boolean check, String key, String fileName, String... args) {
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
		ConcurrentLinkedQueue<String> lnk = new ConcurrentLinkedQueue<>(
				Files.readAllLines(Paths.get(new String(fileName))));
		return lnk.stream();
	}

	private static void setPropertyProcess(String key, String value, String fileName) {
		try (FileWriter writer = new FileWriter(fileName, true)) {

			writer.write("\n" + key + SPLITOR + value + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setProperty(Plugin instance, String key, String value, String fileName) throws IOException {
		ConcurrentLinkedQueue<String> allLines = new ConcurrentLinkedQueue<>(Files.readAllLines(Paths.get(fileName)));

		if (!allLines.isEmpty()) {
			if (allLines.contains(key + SPLITOR + value)) {
				Iterator<String> iterate = allLines.iterator();
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
		ConcurrentLinkedQueue<String> allLines = new ConcurrentLinkedQueue<>(Files.readAllLines(Paths.get(fileName)));

		if (Files.exists(Paths.get(fileName))) {
			Iterator<String> iterate = allLines.iterator();
			while (iterate.hasNext()) {
				String nexti = iterate.next();
				if (nexti.contains(key)) {
					allLines.remove(nexti);
					break;
				}
			}
		}
		try {
			Files.delete(Paths.get(fileName));
			Files.createFile(Paths.get(fileName));
			try (FileWriter writer = new FileWriter(fileName)) {
				for (String line : allLines) {
					writer.write(line);
					writer.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void showProperty(Player player, Plugin instance, String key, String defaultValue, String fileName) {
		String property = PlaceholderAPI.setPlaceholders(player,
				ChatColor.translateAlternateColorCodes('&', getProperty(key, defaultValue, fileName)));
		player.sendMessage(property);
	}

	public static void showProperties(Player player, Plugin instance, String key, String fileName,
			String... defaultValues) throws IOException {

		ConcurrentLinkedQueue<String> lsk = new ConcurrentLinkedQueue<>(Files.readAllLines(Paths.get(fileName)));
		bgetListPropertiesProcess(null, key, fileName, lsk, defaultValues).stream().forEach((x) -> {
			String property = PlaceholderAPI.setPlaceholders(player,
					ChatColor.translateAlternateColorCodes('&', getProperty(key, x, fileName)));
			player.sendMessage(property);
		});

	}

	public static String getProperty(String key, String defaultValue, String fileName) {
		try {
			ConcurrentLinkedQueue<String> cLines = new ConcurrentLinkedQueue<>(Files.readAllLines(Paths.get(fileName)));

			Optional<String> retrn = cLines.stream()
					.filter(x -> x.contains(key + SPLITOR) && x.split(SPLITOR).length == 2).findFirst();
			if (retrn.isPresent()) {
				return retrn.get().split(SPLITOR)[1];
			} else {
				return defaultValue;
			}
		} catch (IOException e) {
			return defaultValue;
		}
	}

	public static String getProperty(final String SPLITOR, String key, String defaultValue, String fileName) {
		try {
			ConcurrentLinkedQueue<String> cLines = new ConcurrentLinkedQueue<>(Files.readAllLines(Paths.get(fileName)));

			Optional<String> retrn = cLines.stream()
					.filter(x -> x.contains(key + SPLITOR) && x.split(SPLITOR).length == 2).findFirst();
			if (retrn.isPresent()) {
				return retrn.get().split(SPLITOR)[1];
			} else {
				return defaultValue;
			}
		} catch (IOException e) {
			return defaultValue;
		}
	}

	public static ConcurrentLinkedQueue<String> getProperties(String key, String fileName, String... defaultValues) {
		try {
			ConcurrentLinkedQueue<String> lsls = new ConcurrentLinkedQueue<>(Files.readAllLines(Paths.get(fileName)));

			return bgetListPropertiesProcess(null, key, fileName, lsls, defaultValues);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ConcurrentLinkedQueue<String> getProperties(final char splitor[], String key, String fileName,
			String... defaultValues) {
		try {
			ConcurrentLinkedQueue<String> lsls = new ConcurrentLinkedQueue<>(Files.readAllLines(Paths.get(fileName)));

			return bgetListPropertiesProcess(new String(splitor), key, fileName, lsls, defaultValues);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ConcurrentLinkedQueue<String> bgetListPropertiesProcess(String splitor, String key, String fileName,
			ConcurrentLinkedQueue<String> allLines, String... defaultValues) throws IOException {
		final CopyOnWriteArrayList<String> ls = new CopyOnWriteArrayList<>(allLines);
		ConcurrentLinkedQueue<String> lsi = new ConcurrentLinkedQueue<>();

		CompletableFuture<Integer> startFuture = CompletableFuture.supplyAsync(() -> {
			return ls.indexOf("* " + key) + 1;
		});

		CompletableFuture<Integer> endFuture = CompletableFuture.supplyAsync(() -> {
			return ls.indexOf("* endif " + key) - 1;
		});

		CompletableFuture<Void> allFutures = CompletableFuture.allOf(startFuture, endFuture);

		allFutures.thenAcceptAsync((x) -> {
			int start = startFuture.join();
			int end = endFuture.join();
			while (start <= end) {
				lsi.add(ls.get(start));
				start++;
			}
		});

		return new ConcurrentLinkedQueue<>(
				lsi.stream().map((x) -> x.split(splitor != null ? splitor : " - ")[1]).collect(Collectors.toList()));
	}

}
