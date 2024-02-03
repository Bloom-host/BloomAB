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
    // Todo (notgeri): https://abapi.lowhosting.org/bloom_velocity_version/

    public static void handle(AbstractPlugin plugin) {
        plugin.getScheduler().runAsync(() -> {
            String newVersion = null;
            try (InputStream inputStream = new URL(UPDATE_URL).openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) newVersion = scanner.next();
            } catch (IOException exception) {
                plugin.getABLogger().info("Cannot look for updates: " + exception.getMessage());
                return;
            }

            if (newVersion == null || plugin.getVersion().equalsIgnoreCase(newVersion)) {
                plugin.getABLogger().info("There isn't a newer version available!");
                return;
            }

            try {
                URL url = new URL(DOWNLOAD_URL);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                String downloadUrl = in.readLine();  // read first line
                in.close();

                // Checking if downloadUrl is not null before logging the message
                if (downloadUrl != null) {
                    plugin.getABLogger().info("There is a new update available. Please download it at: " + downloadUrl);
                } else {
                    plugin.getABLogger().error("Download URL is null. Please check the source.");
                }
            } catch (IOException e) {
                plugin.getABLogger().error("An error occurred while fetching the download URL: " + e.getMessage());
            }
        });
    }
}
