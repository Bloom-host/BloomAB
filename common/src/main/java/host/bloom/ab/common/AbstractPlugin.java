package host.bloom.ab.common;

import host.bloom.ab.common.config.Config;
import host.bloom.ab.common.managers.CounterManager;
import host.bloom.ab.common.utils.Scheduler;

import java.net.URL;
import java.util.logging.Logger;

public interface AbstractPlugin {

    CounterManager getManager();

    Scheduler getScheduler();

    String getVersion();

    Logger getLogger();

    Config getConfig();
}

