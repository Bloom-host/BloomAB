package host.bloom.ab.common.config;

public enum BlockNewJoins {
    ENABLED("Enabled"),
    NEW_PLAYERS_ONLY("NewPlayersOnly"),
    DISABLED("Disabled");

    private final String raw;

    BlockNewJoins(String raw) {
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }

}
