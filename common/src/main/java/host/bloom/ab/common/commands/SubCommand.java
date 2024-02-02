package host.bloom.ab.common.commands;

public interface SubCommand {

    String getPermission();

    void run(Sender sender, String[] args);

    default Iterable<String> getTabCompletion(String[] args) {
        return null;
    }

}
