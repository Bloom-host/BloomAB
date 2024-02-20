package host.bloom.ab.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import host.bloom.ab.common.AbstractPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

public class UpdateChecker {

    static Gson gson = new GsonBuilder().create();

    public static void handle(AbstractPlugin plugin) {
        if (!plugin.getABConfig().checkForUpdates) return;

        plugin.getScheduler().runAsync(() -> {

            // Get the latest release from GitHub and parse the JSON
            GitHubRelease release;
            try (InputStream inputStream = new URL("https://api.github.com/repos/bloom-host/bloomab/releases/latest").openStream()) {
                release = gson.fromJson(new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n")), GitHubRelease.class);
            } catch (IOException exception) {
                plugin.getABLogger().info("Unable to check for new GitHub releases: " + exception.getMessage());
                return;
            }

            // Check if the version has changed
            if (release.tagName != null && plugin.getVersion().equalsIgnoreCase(release.tagName)) {
                plugin.getABLogger().info("You are running the latest version!");
                return;
            }

            // Find the right asset. If there isn't a JAR, we will just
            // offer the release link itself
            String downloadLink = release.htmlUrl;
            for (GitHubRelease.Asset asset : release.assets) {
                if (asset.name.contains(".jar")) {
                    downloadLink = asset.downloadUrl;
                    break;
                }
            }

            plugin.getABLogger().info("There is a new update available! Please download it: " + downloadLink);
        });
    }
}

