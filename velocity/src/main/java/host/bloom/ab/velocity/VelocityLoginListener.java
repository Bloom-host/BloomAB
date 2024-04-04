package host.bloom.ab.velocity;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.client.LoginInboundConnection;

public class VelocityLoginListener {

    private final VelocityPlugin plugin;

    public VelocityLoginListener(VelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLoginInbound(ConnectionHandshakeEvent event) {
        if (event.getConnection() instanceof LoginInboundConnection) {
            plugin.getManager().incrementConnectionCount();
        }
    }

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        Player player = event.getPlayer();

        this.plugin.getManager().removeSeer(player.getUniqueId());
    }

    @Subscribe
    public void onJoin(LoginEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("bab.admin.actionbar.autoenable")) {
            this.plugin.getManager().addSeer(player.getUniqueId());
        }
    }
}
