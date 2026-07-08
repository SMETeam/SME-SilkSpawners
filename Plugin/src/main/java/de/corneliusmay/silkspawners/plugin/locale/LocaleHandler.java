package de.corneliusmay.silkspawners.plugin.locale;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.util.*;

public class LocaleHandler {

    private static final String DEFAULT_MESSAGE = "\u00A7cNo value found for key {0} using locale {1}.\u00A77\n Use \u00A7l\u00A7n/silkspawners locale update confirm\u00A77 to update the locale files.\n \u00A7eWarning!\u00A77 Updating the locale files will overwrite all changes\u00A77.";

    private final SilkSpawners plugin;

    private final File localePath;
    private final Locale locale;

    @Getter
    private ResourceBundle resourceBundle;

    public LocaleHandler(SilkSpawners plugin, Locale locale) {
        this.plugin = plugin;
        this.locale = locale;
        this.localePath = new File(plugin.getDataFolder() + "/locale");

        try {
            copyDefaultLocales(false);
            loadLocale();
        } catch(MissingResourceException | URISyntaxException | IOException ex) {
            plugin.getLog().error("Error loading locale file", ex);
            plugin.getLog().warn("Disabling plugin due to missing locale file");
            plugin.getLog().info("Available locales: " + getAvailableLocales());
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    public void copyDefaultLocales(boolean overwrite) throws URISyntaxException, IOException {
        Path target = Paths.get(plugin.getDataFolder() + "/locale");
        URI resource = getClass().getResource("").toURI();
        FileSystem fileSystem = FileSystems.newFileSystem(resource, Collections.<String, String>emptyMap());
        final Path jarPath = fileSystem.getPath("/locales");

        Files.walkFileTree(jarPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(target.resolve(jarPath.relativize(dir).toString()));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = target.resolve(jarPath.relativize(file).toString());
                if(overwrite) Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                else if(Files.notExists(targetFile)) Files.copy(file, targetFile);
                return FileVisitResult.CONTINUE;
            }
        });
        fileSystem.close();
    }

    public void loadLocale() throws MalformedURLException {
        URL[] urls = {localePath.toURI().toURL()};
        ClassLoader loader = new URLClassLoader(urls);
        this.resourceBundle = ResourceBundle.getBundle("messages", locale, loader);
    }

    public String getAvailableLocales() {
        File localesDir = new File(plugin.getDataFolder() + "/locale");
        return Arrays.stream(localesDir.listFiles()).sorted().map((f) -> f.getName().replace("messages_", "").replace(".properties", "")).toList().toString().replace("[", "").replace("]", "");
    }

    public String getMessageClean(String key, Object... args) {
        return MessageFormat.format(resourceBundle.getString(key).replace("$", "\u00A7"), args);
    }

    public String getMessage(String key, Object... args) {
        try {
            return getPrefix() + "\u00A7f " + getMessageClean(key, args);
        } catch (MissingResourceException ex) {
            return getPrefix() + "\u00A7f " +  MessageFormat.format(DEFAULT_MESSAGE, key, locale.toString());
        }
    }

    public static String getPrefix() {
        return new ConfigValue<String>(PluginConfig.MESSAGE_PREFIX).get();
    }
}
