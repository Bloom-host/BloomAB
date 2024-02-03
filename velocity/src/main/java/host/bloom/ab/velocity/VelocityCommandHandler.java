package host.bloom.ab.velocity;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.SimpleCommand;
import host.bloom.ab.common.commands.Handler;

import java.util.List;
import java.util.stream.StreamSupport;

public class VelocityCommandHandler implements SimpleCommand {

    private final Handler handler;

    public VelocityCommandHandler(VelocityPlugin plugin, CommandManager commandManager) {
        this.handler = new Handler(plugin);
        commandManager.register(commandManager
                        .metaBuilder(Handler.getCommandName())
                        .aliases(Handler.getAliases())
                        .build(),
                this
        );
    }

    @Override
    public void execute(Invocation invocation) {
        this.handler.execute(new VelocitySender(invocation.source()), invocation.arguments());
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        Iterable<String> results = this.handler.onTabComplete(new VelocitySender(invocation.source()), invocation.arguments());
        return StreamSupport.stream(results.spliterator(), false).toList();
    }

}
