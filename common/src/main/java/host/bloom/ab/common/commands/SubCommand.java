package host.bloom.ab.common.commands;

import java.util.Collections;
import java.util.List;

public interface SubCommand {

    String getPermission();

    void run(Sender sender, String[] args);

    default List<String> getTabCompletion(String[] args) {
        return Collections.emptyList();
    }

}
