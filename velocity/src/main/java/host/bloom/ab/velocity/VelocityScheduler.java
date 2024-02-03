package host.bloom.ab.velocity;

import host.bloom.ab.common.utils.Scheduler;

import java.util.concurrent.TimeUnit;

public class VelocityScheduler implements Scheduler {

    private final VelocityPlugin plugin;
    private final com.velocitypowered.api.scheduler.Scheduler scheduler;

    public VelocityScheduler(VelocityPlugin plugin, com.velocitypowered.api.scheduler.Scheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    @Override
    public void runAsync(Runnable runnable) {
        this.scheduler.buildTask(plugin, runnable).schedule();
    }

    @Override
    public void schedule(Runnable runnable, long l1, TimeUnit unit) {
        this.scheduler.buildTask(plugin, runnable).delay(l1, unit).schedule();
    }

    @Override
    public void schedule(Runnable runnable, long l1, long l2, TimeUnit unit) {
        this.scheduler.buildTask(plugin, runnable).delay(l1, unit).repeat(l2, unit).schedule();
    }

}
