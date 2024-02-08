package host.bloom.ab.waterfall;

import host.bloom.ab.common.commands.Handler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class WaterfallCommandHandler extends Command implements TabExecutor {

    private final Handler handler;

    public WaterfallCommandHandler(WaterfallPlugin plugin) {
        super(Handler.getCommandName(), null, Handler.getAliases());
        this.handler = new Handler(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.handler.execute(new WaterfallSender(sender), args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return this.handler.onTabComplete(new WaterfallSender(sender), args);
    }

}
