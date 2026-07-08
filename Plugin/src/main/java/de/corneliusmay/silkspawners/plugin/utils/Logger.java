package de.corneliusmay.silkspawners.plugin.utils;

import org.bukkit.Bukkit;

import java.util.Arrays;

public class Logger {

    private final String prefix;

    public Logger(String prefix) {
        this.prefix = prefix;
    }

    public void info(String msg) {
        Bukkit.getConsoleSender().sendMessage(prefix + " \u00A78[\u00A72INFO\u00A78]\u00A77: " + msg);
    }

    public void warn(String msg) {
        Bukkit.getConsoleSender().sendMessage(prefix + " \u00A78[\u00A7eWARN\u00A78]\u00A77: " + msg);

    }

    public void error(String msg) {
        Bukkit.getConsoleSender().sendMessage(prefix + " \u00A78[\u00A7cERROR\u00A78]\u00A77: " + msg);

    }

    public void error(String msg, Throwable ex) {
        error(msg + ": \u00A7c" + ex.getMessage() + "\n\u00A77" + Arrays.toString(ex.getStackTrace()));
    }
}
