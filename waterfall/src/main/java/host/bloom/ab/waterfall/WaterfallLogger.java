package host.bloom.ab.waterfall;

import host.bloom.ab.common.utils.Logger;

public class WaterfallLogger implements Logger {

    private final java.util.logging.Logger logger;

    public WaterfallLogger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String message) {
        this.info(message);
    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void warning(String message) {
        this.logger.warning(message);
    }

    @Override
    public void error(String message) {
        this.logger.severe(message);
    }

}
