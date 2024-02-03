package host.bloom.ab.common;

import host.bloom.ab.common.config.Config;
import host.bloom.ab.common.managers.CounterManager;
import host.bloom.ab.common.utils.Logger;
import host.bloom.ab.common.utils.Scheduler;

public interface AbstractPlugin {

    CounterManager getManager();

    Scheduler getScheduler();

    String getVersion();

    Logger getABLogger();

    Config getConfig();

}

