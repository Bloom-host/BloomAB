package host.bloom.ab.common.utils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GitHubRelease {
        @SerializedName("tag_name") public String tagName;
        @SerializedName("html_url") public String htmlUrl;
        @SerializedName("assets") public ArrayList<Asset> assets;

	public class Asset {
	        @SerializedName("name") public String name;
	        @SerializedName("browser_download_url") public String downloadUrl;
	}
}
