package host.bloom.ab.common.commands;

public interface Sender {

    void sendMessage(String message);

    boolean hasPermission(String permission);

}
