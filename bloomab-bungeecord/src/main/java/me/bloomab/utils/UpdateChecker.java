package me.bloomab.utils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private final Plugin plugin;
    private final URL url;

    public UpdateChecker(Plugin plugin, String url) throws MalformedURLException, MalformedURLException {
        this.plugin = plugin;
        this.url = new URL(url);
    }

    public void getVersion(final Consumer<String> consumer) {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            try (InputStream inputStream = url.openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }
}
