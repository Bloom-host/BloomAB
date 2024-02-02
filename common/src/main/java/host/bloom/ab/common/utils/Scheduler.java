package host.bloom.ab.common.utils;

import host.bloom.ab.common.AbstractPlugin;

import javax.print.attribute.standard.RequestingUserName;
import java.util.concurrent.TimeUnit;

public interface Scheduler {
    void runAsync(Runnable runnable);
    void schedule(Runnable runnable, long l1, TimeUnit unit);
    void schedule(Runnable runnable, long l1, long l2, TimeUnit unit);
}
