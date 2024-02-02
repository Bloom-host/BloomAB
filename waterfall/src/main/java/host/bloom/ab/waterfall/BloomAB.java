package host.bloom.ab.waterfall;

import host.bloom.ab.common.BloomABPlugin;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Level;

public class BloomAB extends Plugin implements BloomABPlugin {
    @Override
    public void onEnable() {
        this.test();
    }

    @Override
    public void test() {
        getLogger().log(Level.INFO, "success");
    }
}
