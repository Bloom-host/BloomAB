package host.bloom.ab.waterfall;

import host.bloom.ab.common.utils.Scheduler;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.util.concurrent.TimeUnit;

public class WaterfallScheduler implements Scheduler {

    private final TaskScheduler scheduler;
    private final WaterfallPlugin plugin;

    public WaterfallScheduler(WaterfallPlugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getProxy().getScheduler();
    }

    @Override
    public void runAsync(Runnable runnable) {
        this.scheduler.runAsync(plugin, runnable);
    }

    @Override
    public void schedule(Runnable runnable, long l1, TimeUnit unit) {
        this.scheduler.schedule(plugin, runnable, l1, unit);
    }

    @Override
    public void schedule(Runnable runnable, long l1, long l2, TimeUnit unit) {
        this.scheduler.schedule(plugin, runnable, l1, l2, unit);
    }

}
