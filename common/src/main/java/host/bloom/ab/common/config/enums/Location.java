package host.bloom.ab.common.config.enums;

public enum Location {
    ASHBURN("Ashburn", "https://abapi.bloom.host/"),
    DALLAS("Dallas", "https://abapi.bloom.host/"),
    LOS_ANGELES("Los Angeles", "https://abapi.bloom.host/"),
    GERMANY("Germany", "https://abapi.lowhosting.org/");

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
