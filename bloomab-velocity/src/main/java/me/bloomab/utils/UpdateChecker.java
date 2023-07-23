package me.bloomab.utils;


import me.bloomab.BloomAB;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private final BloomAB plugin;
    private final URL url;

    public UpdateChecker(BloomAB plugin, String url) throws MalformedURLException {
        this.plugin = plugin;
        this.url = new URL(url);
    }

    public void getVersion(final Consumer<String> consumer) {
        plugin.getServer().getScheduler().buildTask(plugin, () -> {
            try (InputStream inputStream = url.openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        }).schedule();
    }
}
