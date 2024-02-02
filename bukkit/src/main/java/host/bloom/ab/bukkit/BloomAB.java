package host.bloom.ab.bukkit;

import host.bloom.ab.common.BloomABPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BloomAB extends JavaPlugin implements BloomABPlugin {
    @Override
    public void onEnable() {
        this.test();
    }

    @Override
    public void test() {
        this.getLogger().info("success");
    }
}
