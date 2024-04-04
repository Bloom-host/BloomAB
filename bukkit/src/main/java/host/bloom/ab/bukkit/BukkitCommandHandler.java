package host.bloom.ab.bukkit;

import host.bloom.ab.common.commands.Handler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import java.util.Arrays;
import java.util.List;

public class BukkitCommandHandler implements TabExecutor {

    private final BukkitPlugin plugin;
    private final Handler handler;

    public BukkitCommandHandler(BukkitPlugin plugin) {
        this.plugin = plugin;
        this.handler = new Handler(plugin);

        PluginCommand command = plugin.getCommand(Handler.getCommandName());
        if (command == null) {
            plugin.getABLogger().error("Unable to register command!");
            return;
        }
        command.setAliases(Arrays.asList(Handler.getAliases()));
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.handler.execute(new BukkitSender(this.plugin, sender), args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return this.handler.onTabComplete(new BukkitSender(this.plugin, sender), args);
    }

}
