package arena;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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

		int i = 0;

		Object obProcess = new Object();

		synchronized (obProcess) {
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
		}
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
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', getProperty(key, defaultValue, fileName)));
	}

	public static void showProperties(Player player, Plugin instance, String key, String fileName,
			String... defaultValues) throws IOException {

		ConcurrentLinkedQueue<String> lsk = new ConcurrentLinkedQueue<>(Files.readAllLines(Paths.get(fileName)));
		bgetListPropertiesProcess(key, fileName, lsk, defaultValues).stream().forEach((x) -> {
			player.sendMessage(x);
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

			return bgetListPropertiesProcess(key, fileName, lsls, defaultValues);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ConcurrentLinkedQueue<String> bgetListPropertiesProcess(String key, String fileName,
			ConcurrentLinkedQueue<String> allLines, String... defaultValues) throws IOException {
		final CopyOnWriteArrayList<String> ls = new CopyOnWriteArrayList<>(allLines);
		ConcurrentLinkedQueue<String> lsi = new ConcurrentLinkedQueue<>();
		int startKey = Collections.synchronizedList(ls).indexOf("* " + key) + 1;
		int endKey = Collections.synchronizedList(ls).indexOf("* endif " + key) - 1;

		while (startKey <= endKey) {
			lsi.add(ls.get(startKey));
			startKey++;
		}

		return new ConcurrentLinkedQueue<>(lsi.stream().map((x) -> x.split(" - ")[1]).collect(Collectors.toList()));
	}

}
