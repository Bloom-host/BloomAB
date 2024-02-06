package host.bloom.ab.common.config;

public enum Location {
    ASHBURN("Ashburn", "https://abapi-ash.bloom.host"),
    LOS_ANGELES("Los Angeles", "https://abapi-la.bloom.host"),
    GERMANY("Germany", "https://abapi-fsn.bloom.host");

    private final String displayName;
    private final String endpoint;

    Location(String displayName, String endpoint) {
        this.displayName = displayName;
        this.endpoint = endpoint;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEndpoint() {
        return endpoint;
    }

}
