package host.bloom.ab.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import host.bloom.ab.common.BloomABPlugin;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "@id@", name = "@name@", version = "@version@", description = "@description@")
public class BloomAB implements BloomABPlugin {

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path configDirectory;

    @Inject
    public BloomAB(ProxyServer proxy, Logger logger, @DataDirectory Path configDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.configDirectory = configDirectory;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onEnable(ProxyInitializeEvent e) {
        this.test();
    }

    @Override
    public void test() {
        this.logger.info("success");
    }

}
