package arena;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ImmutableList;

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
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
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

	public static void setProperties_NS(String key, boolean check, String fileName, List<String> args) {
		List<String> allLines = null;
		if (check) {
			if (Files.notExists(Paths.get(fileName))) {
				try {
					Files.createFile(Paths.get(fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			allLines = Files.readAllLines(Paths.get(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (allLines.contains("* " + key)) {
			int ini = getByID_NS("* " + key, fileName) + 1;
			int ini2 = getByID_NS("* endif " + key, fileName) - 1;
			while (ini <= ini2) {
				allLines.remove(ini);
				ini++;
			}
		} else {
			int i = 0;
			try (FileWriter writer = new FileWriter(fileName, true)) {
				writer.write("\n" + "* " + key + "\n");
				while (i < args.size()) {
					writer.write(i + LIST_SPLITOR + args.get(i) + "\n");
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

	public static void setProperties_NS(boolean check, String key, String fileName, String... args) {
		List<String> allLines = null;
		if (check) {
			if (Files.notExists(Paths.get(fileName))) {
				try {
					Files.createFile(Paths.get(fileName));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			allLines = Files.readAllLines(Paths.get(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (allLines.contains("* " + key)) {
			int ini = getByID_NS("* " + key, fileName) + 1;
			int ini2 = getByID_NS("* endif " + key, fileName) - 1;
			while (ini <= ini2) {
				removeProperty_NS(allLines.get(ini), fileName);
				ini++;
			}
			setPropertiesProcess(key, args, fileName);
		} else {
			setPropertiesProcess(key, args, fileName);
		}
	}

	private static void setPropertiesProcess(String key, String args[], String fileName) {
		int i = 0;
		try (FileWriter writer = new FileWriter(fileName, true)) {
			writer.write("\n" + "* " + key + "\n");
			while (i < args.length) {
				if (args[i] == null)
					continue;
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

	private static void setPropertiesProcessList(String key, List<String> args, String fileName) {
		int i = 0;
		try (FileWriter writer = new FileWriter(fileName, true)) {
			writer.write("\n" + "* " + key + "\n");
			while (i < args.size()) {
				if (args.get(i) == null)
					continue;
				writer.write(i + LIST_SPLITOR + args.get(i) + "\n");
				writer.flush();
				i++;
			}
			writer.write("* endif " + key);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setProperties(Plugin instance, boolean check, String key, String fileName, List<String> args) {

		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			List<String> allLines = null;
			if (check) {
				if (Files.notExists(Paths.get(fileName))) {
					try {
						Files.createFile(Paths.get(fileName));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				allLines = Files.readAllLines(Paths.get(fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (allLines.contains("* " + key)) {
				int ini = getByID_NS("* " + key, fileName) + 1;
				int ini2 = getByID_NS("* endif " + key, fileName) - 1;
				while (ini <= ini2) {
					removeProperty_NS(allLines.get(ini), fileName);
					ini++;
				}
				setPropertiesProcessList(key, args, fileName);
			} else {
				setPropertiesProcessList(key, args, fileName);
			}

		});
	}

	public static void setProperty_NS(String key, String value, String fileName) {
		List<String> allLines = null;
		try {
			allLines = Files.readAllLines(Paths.get(fileName));
		} catch (IOException e) {
			allLines = null;
		}
		if (allLines != null) {
			if (allLines.contains(key + SPLITOR + value)) {
				int ini = getByID_NS(key + SPLITOR + value, fileName);
				removeProperty_NS(allLines.get(ini), fileName);
				setPropertyProcess(key, value, fileName);
			}
		} else {
			setPropertyProcess(key, value, fileName);
		}
	}

	private static void setPropertyProcess(String key, String value, String fileName) {
		try (FileWriter writer = new FileWriter(fileName, true)) {

			writer.write("\n" + key + SPLITOR + value + "\n");
			writer.flush();
		} catch (IOException e) {
			if (Files.notExists(Paths.get(fileName))) {
				try {
					Files.createFile(Paths.get(fileName));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (Files.exists(Paths.get(fileName))) {
					setPropertyProcess(key, value, fileName);
				}
			}
		}
	}

	public static void setProperty(Plugin instance, String key, String value, String fileName) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			List<String> allLines = null;
			try {
				allLines = Files.readAllLines(Paths.get(fileName));
			} catch (IOException e) {
				allLines = null;
			}
			if (allLines != null) {
				if (allLines.contains(key + SPLITOR + value)) {
					int ini = getByID_NS(key + SPLITOR + value, fileName);
					removeProperty(instance, allLines.get(ini), fileName);
					setPropertyProcess(key, value, fileName);
				}
			} else {
				setPropertyProcess(key, value, fileName);
			}
		});
	}

	public static void removeProperty_NS(String key, String fileName) {
		removePropertyProcess(key, fileName);
	}

	public static void removeProperty(Plugin instance, String key, String fileName) {
		Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
			removePropertyProcess(key, fileName);
		});
	}

	private static void removePropertyProcess(String key, String fileName) {
		List<String> allLines = null;
		if (Files.exists(Paths.get(fileName))) {
			try {
				allLines = Files.readAllLines(Paths.get(fileName));
			} catch (IOException e) {
				throw new IllegalStateException(
						"File is not exist or something else \n" + Arrays.toString(e.getStackTrace()));
			}
			int i = 0;
			while (i < allLines.size()) {
				if (allLines.get(i).contains(key)) {
					allLines.remove(i);
					break;
				}
				i++;
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
		CompletableFuture<ValueGetter> result = CompletableFuture.supplyAsync(() -> {
			String prc = getPropertiesProcess(key, defaultValue, fileName);
			ValueGetter getter = new ValueGetter();
			getter.setValue(prc);
			return getter;
		});

		result.exceptionally(exp -> {
			throw new IllegalStateException("Couldn't show property");
		});

		result.thenAccept((property) -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', property.getValue()));
			});
		});
	}

	public static void showProperties(Player player, Plugin instance, String key, String fileName,
			String... defaultValues) {

		CompletableFuture<ValueGetter> result = CompletableFuture.supplyAsync(() -> {
			List<String> allLines = null;
			try {
				allLines = Files.readAllLines(Paths.get(fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}
			ValueGetter getter = new ValueGetter();
			if (allLines.size() == 0 && defaultValues != null) {
				getter.setLValue(Arrays.asList(defaultValues));
			} else {
				List<String> prc = getListPropertiesProcess(key, fileName, allLines);
				getter.setLValue(prc);
			}
			return getter;
		});

		result.exceptionally((exp) -> {
			throw new IllegalStateException("Problem with showProperties\n" + exp);
		});

		result.thenAccept((x) -> {
			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				for (String msg : x.getLValue()) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
				}
			});
		});
	}

	@Deprecated
	public static CompletableFuture<List<String>> getProperties(String key, String fileName, String... defaultValues) {
		CompletableFuture<List<String>> result = CompletableFuture.supplyAsync(() -> {
			List<String> allLines = null;
			try {
				allLines = Files.readAllLines(Paths.get(fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (allLines.size() == 0 && defaultValues != null) {
				return Arrays.asList(defaultValues);
			}

			return getListPropertiesProcess(key, fileName, allLines, defaultValues);

		});

		result.exceptionally((exp) -> {
			throw new IllegalStateException("getListProperties() \n" + exp);
		});

		return result;
	}

	public static ConcurrentSkipListSet<String> getProperties_C(String key, String fileName, String... defaultValues) {
		ConcurrentSkipListSet<String> lsls = new ConcurrentSkipListSet<>();
		try {

			lsls = new ConcurrentSkipListSet<>(Files.readAllLines(Paths.get(fileName)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (lsls.size() == 0 && defaultValues != null) {
			return new ConcurrentSkipListSet<>(Arrays.asList(defaultValues));
		}

		try {
			return bgetListPropertiesProcess(key, fileName, lsls, defaultValues);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static CompletableFuture<List<String>> getProperties(String key, String fileName,
			List<String> defaultValues) {
		CompletableFuture<List<String>> result = CompletableFuture.supplyAsync(() -> {
			List<String> allLines = null;
			try {
				allLines = Files.readAllLines(Paths.get(fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (allLines.size() == 0 && defaultValues != null) {
				return defaultValues;
			}

			return getListPropertiesProcess(key, fileName, allLines, defaultValues);

		});

		result.exceptionally((exp) -> {
			throw new IllegalStateException("getListProperties() \n" + exp);
		});

		return result;
	}

	public static List<String> getProperties_NS(String key, String fileName, @Nullable List<String> defaultValues) {
		List<String> allLines = null;
		try {
			allLines = Files.readAllLines(Paths.get(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (allLines.size() == 0 && defaultValues != null) {
			return defaultValues;
		}
		return getListPropertiesProcess(key, fileName, allLines, defaultValues);
	}

	public static List<String> getProperties_NNS(String key, String fileName, String... defaultValues) {
		List<String> allLines = null;
		System.out.println(1);
		try {
			allLines = Files.readAllLines(Paths.get(fileName));
			if (allLines == null) {
				allLines = new ArrayList<>();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(2);
		if (allLines != null && allLines.size() == 0 && defaultValues != null) {
			return Arrays.asList(defaultValues);
		}
		System.out.println(3);
		return getListPropertiesProcess(key, fileName, allLines, defaultValues);
	}

	private static List<String> getListPropertiesProcess(String key, String fileName, List<String> allLines,
			String... defaultValues) {

		List<String> ls = new ArrayList<String>();
		int ini = getByID_NS("* " + key, fileName) + 1;
		int ini2 = getByID_NS("* endif " + key, fileName) - 1;
		while (ini <= ini2) {
			ls.add(allLines.get(ini).split(LIST_SPLITOR)[1]);
			ini++;
		}
		if (ls.size() == 0 || ls == null) {
			return Arrays.asList(defaultValues);
		}

		return ls;
	}

	public static CompletableFuture<List<String>> reader(String fileName) {
		CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
			try {
				return Files.readAllLines(Paths.get(fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		});
		future.handle((result, exp) -> {
			if (result == null) {
				throw new IllegalStateException(Arrays.toString(exp.getStackTrace()));
			} else {
				exp.printStackTrace();
				throw new IllegalStateException("Problem with reading");
			}
		});
		return null;
	}

	public static int getIntByString(ConcurrentSkipListSet<String> lines, String str) {
		Optional<String> value = lines.stream().filter((x) -> x.equals(str)).findFirst();
		if (value.isPresent()) {
			List<String> ls = new ArrayList<>(lines);
			return ls.indexOf(value.get());
		}
		return -1;
	}

	public static ConcurrentSkipListSet<String> bgetListPropertiesProcess(String key, String fileName,
			ConcurrentSkipListSet<String> allLines, String... defaultValues) throws IOException {

		ConcurrentSkipListSet<String> ls = new ConcurrentSkipListSet<>();
		ImmutableList<String> imm = ImmutableList.copyOf(allLines);
		List<String> linesToProcess = new ArrayList<>(imm);

		for (String line : linesToProcess) {
			if (line.equals(imm.get(getIntByString(allLines, "* " + key)))) {
				String storedFirstString = line;
				Iterator<String> iterate = allLines.iterator();
				while (iterate.hasNext()) {
					String currentLine = iterate.next();
					if (currentLine.equals(storedFirstString)) {
						while (getIntByString(allLines, storedFirstString) < getIntByString(allLines,
								"* endif " + key)) {
							ls.add(currentLine.split(LIST_SPLITOR)[1]);
							if (!iterate.hasNext()) {
								break;
							}
							storedFirstString = iterate.next();
						}
					}
				}
			}
		}

		if (ls.isEmpty()) {
			return new ConcurrentSkipListSet<>(Arrays.asList(defaultValues));
		}

		return ls;
	}

	public static ConcurrentSkipListSet<String> getListPropertiesProcess(String key, String fileName,
			ConcurrentSkipListSet<String> allLines, String... defaultValues) throws IOException {

		ConcurrentSkipListSet<String> ls = new ConcurrentSkipListSet<>();
		ImmutableList<String> imm = ImmutableList.copyOf(allLines);
		List<String> linesToProcess = new ArrayList<>(imm);

		for (String line : linesToProcess) {
			if (line.equals(imm.get(getIntByString(allLines, "* " + key)))) {
				String storedFirstString = line;
				Iterator<String> iterate = allLines.iterator();
				while (iterate.hasNext()) {
					String currentLine = iterate.next();
					if (currentLine.equals(storedFirstString)) {
						while (getIntByString(allLines, storedFirstString) < getIntByString(allLines,
								"* endif " + key)) {
							ls.add(currentLine.split(LIST_SPLITOR)[1]);
							if (!iterate.hasNext()) {
								break;
							}
							storedFirstString = iterate.next();
						}
					}
				}
			}
		}

		if (ls.isEmpty()) {
			return new ConcurrentSkipListSet<>(Arrays.asList(defaultValues));
		}

		return ls;
	}

	private static List<String> getListPropertiesProcess(String key, String fileName, List<String> allLines,
			List<String> defaultValues) {

		List<String> ls = new ArrayList<String>();
		int ini = getByID_NS("* " + key, fileName) + 1;
		int ini2 = getByID_NS("* endif " + key, fileName) - 1;
		while (ini <= ini2) {
			ls.add(allLines.get(ini).split(LIST_SPLITOR)[1]);
			ini++;
		}
		if (ls.size() == 0 || ls == null) {
			return defaultValues;
		}

		return ls;
	}

	@Deprecated
	public static CompletableFuture<String> getProperty(String key, String defaultValue, String fileName) {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			List<String> lines = null;
			try {
				lines = Files.readAllLines(Paths.get(fileName));
			} catch (IOException e) {
				lines = null;
			}

			if (lines == null) {
				return defaultValue;
			} else if ((lines.size() == 0)) {
				return defaultValue;
			}

			Optional<String> retrn = lines.stream()
					.filter(x -> x.contains(key + SPLITOR) && x.split(SPLITOR).length == 2).findAny();
			if (retrn.isPresent()) {
				return retrn.get().split(SPLITOR)[1];
			} else {
				return defaultValue;
			}
		});

		future.exceptionally((exp) -> {
			throw new IllegalStateException("Problem with getProperty()\n" + exp);
		});
		return future;
	}

	public static String getProperty_C(String key, String defaultValue, String fileName) {
		ConcurrentSkipListSet<String> cLines = null;
		try {
			cLines = new ConcurrentSkipListSet<>(Files.readAllLines(Paths.get(fileName)));
		} catch (IOException e) {
			cLines = null;
		}

		if (cLines == null) {
			return defaultValue;
		} else if (cLines.size() == 0) {
			return defaultValue;
		}

		Optional<String> retrn = cLines.parallelStream()
				.filter(x -> x.contains(key + SPLITOR) && x.split(SPLITOR).length == 2).findFirst();
		if (retrn.isPresent()) {
			return retrn.get().split(SPLITOR)[1];
		} else {
			return defaultValue;
		}
	}

	@Deprecated
	public static String getProperty_NS(String key, String defaultValue, String fileName) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ((lines.size() == 0)) {
			return defaultValue;
		}

		Optional<String> retrn = lines.stream().filter(x -> x.contains(key + SPLITOR) && x.split(SPLITOR).length == 2)
				.findAny();
		if (retrn.isPresent()) {
			return retrn.get().split(SPLITOR)[1];
		} else {
			return defaultValue;
		}
	}

	private static String getPropertiesProcess(String key, String defaultValue, String fileName) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ((lines.size() == 0)) {
			return defaultValue;
		}

		for (String i : lines) {
			if (i.contains(key + SPLITOR)) {
				String gotten[] = i.split(SPLITOR);
				if (gotten.length == 2 && gotten[1] != null) {
					String in_s = gotten[1];
					return in_s;
				} else {
					return defaultValue;
				}
			}
		}
		return defaultValue;
	}

}
