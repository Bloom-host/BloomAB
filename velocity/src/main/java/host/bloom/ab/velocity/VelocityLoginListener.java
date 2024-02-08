package host.bloom.ab.velocity;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
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

}
