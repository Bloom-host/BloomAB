package host.bloom.ab.common.commands;

import java.util.Collections;

public interface SubCommand {

    String getPermission();

    void run(Sender sender, String[] args);

    default Iterable<String> getTabCompletion(String[] args) {
        return Collections.emptyList();
    }

}
