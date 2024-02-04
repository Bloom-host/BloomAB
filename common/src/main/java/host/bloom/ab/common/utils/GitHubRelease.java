package host.bloom.ab.common.utils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public record GitHubRelease(
        @SerializedName("tag_name") String tagName,
        @SerializedName("html_url") String htmlUrl,
        @SerializedName("assets") ArrayList<Asset> assets
) {
    public record Asset(
            @SerializedName("name") String name,
            @SerializedName("browser_download_url") String downloadUrl
    ) {}
}
