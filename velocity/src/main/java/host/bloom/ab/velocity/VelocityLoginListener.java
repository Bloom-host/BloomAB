package host.bloom.ab.velocity;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.client.LoginInboundConnection;
import host.bloom.ab.common.config.ConfigKeys;
import host.bloom.ab.common.managers.ConfigManager;

public class VelocityLoginListener {

    private final VelocityPlugin plugin;

    public VelocityLoginListener(VelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLoginInbound(ConnectionHandshakeEvent event) {
        if (!ConfigManager.getConfig().getProperty(ConfigKeys.catch_raw_connections)) {
            if (event.getConnection() instanceof LoginInboundConnection) {
                this.plugin.getManager().incrementConnectionCount();
            }
        }
    }

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        Player player = event.getPlayer();

        // Always remove it just in case as they might not have the permission.
        this.plugin.getManager().removeSeer(player.getUniqueId());
    }

    @Subscribe
    public void onJoin(LoginEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("bab.admin.actionbar.onjoin")) {
            this.plugin.getManager().addSeer(player.getUniqueId());
        }
    }
}
