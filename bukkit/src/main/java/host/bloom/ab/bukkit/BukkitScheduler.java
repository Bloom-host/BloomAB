package host.bloom.ab.bukkit;

import host.bloom.ab.common.utils.Scheduler;

import java.util.concurrent.TimeUnit;

public class BukkitScheduler implements Scheduler {

    private final BukkitPlugin plugin;
    private final org.bukkit.scheduler.BukkitScheduler scheduler;

    public BukkitScheduler(BukkitPlugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    @Override
    public void runAsync(Runnable runnable) {
        this.scheduler.runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void schedule(Runnable runnable, long l1, TimeUnit unit) {
        l1 = this.convertTimeUnitToTicks(l1, unit);
        this.scheduler.runTaskLater(plugin, runnable, l1);
    }

    @Override
    public void schedule(Runnable runnable, long l1, long l2, TimeUnit unit) {
        l1 = this.convertTimeUnitToTicks(l1, unit);
        l2 = this.convertTimeUnitToTicks(l2, unit);
        this.scheduler.runTaskTimer(plugin, runnable, l1, l2);
    }

    /**
     * Convert a time unit to ticks
     * @param l The number of unit
     * @param unit The type of unit
     * @return The time in ticks
     */
    private long convertTimeUnitToTicks(long l, TimeUnit unit) {
        return unit.toSeconds(l) * 20;
    }

}
