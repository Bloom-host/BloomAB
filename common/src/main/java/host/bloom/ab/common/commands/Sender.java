package host.bloom.ab.common.commands;

import java.util.UUID;

public interface Sender {

    void sendMessage(String message);

    boolean hasPermission(String permission);

    void actionbar(String message);

    boolean isPlayer();

    UUID getUUID();

}
