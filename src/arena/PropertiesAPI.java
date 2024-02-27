package arena;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/* 
 * @author shayegan8
 * 
 */
public class PropertiesAPI {

	private static List<String> secretList;
	private static String alphabets[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "k", "j", "l", "m", "n", "o",
			"p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
	private static final String SPLITOR = "@";
	private static final String LIST_SPLITOR = " - ";

	public static List<String> getSecretList() {
		return secretList;
	}

	public static void setSecretList(List<String> ls) {
		secretList = ls;
	}

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

	public static void setProperties_NS(boolean check, String key, String fileName, String... args) {
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
	}

	public static void setProperties(Plugin instance, boolean check, String key, String fileName, List<String> args) {
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

		});
	}

	public static void setProperty_NS(String key, String value, String fileName) {
		try (FileWriter writer = new FileWriter(fileName, true)) {
			writer.write("\n" + key + SPLITOR + value + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setProperty(Plugin instance, boolean check, String key, String value, String fileName) {
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
			try (FileWriter writer = new FileWriter(fileName, true)) {
				writer.write("\n" + key + SPLITOR + value + "\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
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
			ValueGetter getter = new ValueGetter();
			if (getSecretList().size() == 0 && defaultValues != null) {
				getter.setLValue(Arrays.asList(defaultValues));
			} else {
				List<String> prc = getListPropertiesProcess(key, fileName);
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

	public static CompletableFuture<List<String>> getProperties(String key, String fileName, String... defaultValues) {
		CompletableFuture<List<String>> result = CompletableFuture.supplyAsync(() -> {
			if (getSecretList().size() == 0 && defaultValues != null) {
				return Arrays.asList(defaultValues);
			}

			return getListPropertiesProcess(key, fileName);

		});

		result.exceptionally((exp) -> {
			throw new IllegalStateException("getListProperties() \n" + exp);
		});

		return result;
	}

	public static CompletableFuture<List<String>> getProperties(String key, String fileName,
			List<String> defaultValues) {
		CompletableFuture<List<String>> result = CompletableFuture.supplyAsync(() -> {
			if (getSecretList().size() == 0 && defaultValues != null) {
				return defaultValues;
			}

			return getListPropertiesProcess(key, fileName);

		});

		result.exceptionally((exp) -> {
			throw new IllegalStateException("getListProperties() \n" + exp);
		});

		return result;
	}

	public static List<String> getProperties_NS(String key, String fileName, @Nullable List<String> defaultValues) {
		if (getSecretList().size() == 0 && defaultValues != null) {
			return defaultValues;
		}
		return getListPropertiesProcess(key, fileName);
	}

	public static List<String> getProperties_NNS(String key, String fileName, String... defaultValues) {
		if (getSecretList().size() == 0 && defaultValues != null) {
			return Arrays.asList(defaultValues);
		}
		return getListPropertiesProcess(key, fileName);
	}

	private static List<String> getListPropertiesProcess(String key, String fileName) {
		List<String> ls = new ArrayList<String>();
		try {
			if (getSecretList() == null || getSecretList() != Files.readAllLines(Paths.get(fileName)))
				setSecretList(Files.readAllLines(Paths.get(fileName)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int ini = getByID_NS("* " + key, fileName) + 1;
		int ini2 = getByID_NS("* endif " + key, fileName) - 1;
		while (ini <= ini2) {
			ls.add(getSecretList().get(ini).split(LIST_SPLITOR)[1]);
			ini++;
		}
		return ls;
	}

	public static CompletableFuture<String> getProperty(boolean check, String key, String defaultValue,
			String fileName) {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			if (check) {
				try {
					if (getSecretList() == null || getSecretList() != Files.readAllLines(Paths.get(fileName)))
						setSecretList(Files.readAllLines(Paths.get(fileName)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if ((getSecretList().size() == 0)) {
				return defaultValue;
			}

			Optional<String> retrn = getSecretList().stream()
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

	public static String getProperty_NS(boolean check, String key, String defaultValue, String fileName) {
		if (check) {
			try {
				if (getSecretList() == null || getSecretList() != Files.readAllLines(Paths.get(fileName)))
					setSecretList(Files.readAllLines(Paths.get(fileName)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ((getSecretList().size() == 0)) {
			return defaultValue;
		}

		Optional<String> retrn = getSecretList().stream()
				.filter(x -> x.contains(key + SPLITOR) && x.split(SPLITOR).length == 2).findAny();
		if (retrn.isPresent()) {
			return retrn.get().split(SPLITOR)[1];
		} else {
			return defaultValue;
		}
	}

	private static String getPropertiesProcess(String key, String defaultValue, String fileName) {
		try {
			if (getSecretList() == null || getSecretList() != Files.readAllLines(Paths.get(fileName)))
				setSecretList(Files.readAllLines(Paths.get(fileName)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if ((getSecretList().size() == 0)) {
			return defaultValue;
		}
		for (String i : getSecretList()) {
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

	public static void fakeFreeSecretList() {
		secretList = null;
	}

}
