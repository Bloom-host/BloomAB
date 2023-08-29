package me.bloomab.enums;

import lombok.Getter;
import me.bloomab.BloomAB;

public enum BungeeConfig {

    TRIGGER_DURATION("trigger_duration"),
    MAX_JOINS_PER_SECOND("max_joins_per_second"),
    IP_ADDRESS("ip_address"),
    SECRET_KEY("secret_key"),
    BLOCK_NEW_JOINS("block_new_joins")
    ;

    @Getter
    private final String path;

    private final BloomAB BloomAB = me.bloomab.BloomAB.getInstance();

    BungeeConfig(String path) {
        this.path = path;
    }

    public Integer getInt() {
        return BloomAB.getConfigFile().getConfig().getInt(getPath());
    }
    public String getString() {
        return BloomAB.getConfigFile().getConfig().getString(getPath());
    }
}
