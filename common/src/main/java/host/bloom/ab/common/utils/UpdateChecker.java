package host.bloom.ab.common.utils;

import host.bloom.ab.common.AbstractPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    private static final String UPDATE_URL = "https://abapi.lowhosting.org/bloom_version/";
    private static final String DOWNLOAD_URL = "https://abapi.lowhosting.org/bloom_downloadUrl/";

    public static void handle(AbstractPlugin plugin) {
        plugin.getScheduler().runAsync(() -> {
            String newVersion = null;
            try (InputStream inputStream = new URL(UPDATE_URL).openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) newVersion = scanner.next();
            } catch (IOException exception) {
                plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
                return;
            }

            if (newVersion == null || plugin.getVersion().equalsIgnoreCase(newVersion)) {
                plugin.getLogger().info("There isn't a newer version available!");
                return;
            }

            try {
                URL url = new URL(DOWNLOAD_URL);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                String downloadUrl = in.readLine();  // read first line
                in.close();

                // Checking if downloadUrl is not null before logging the message
                if (downloadUrl != null) {
                    plugin.getLogger().info("There is a new update available. Please download it at: " + downloadUrl);
                } else {
                    plugin.getLogger().severe("Download URL is null. Please check the source.");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("An error occurred while fetching the download URL: " + e.getMessage());
            }
        });
    }
}
