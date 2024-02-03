package host.bloom.ab.velocity;

import host.bloom.ab.common.utils.Logger;

public class VelocityLogger implements Logger {

    private final org.slf4j.Logger logger;

    public VelocityLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String message) {
        this.logger.debug(message);
    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void warning(String message) {
        this.logger.warn(message);
    }

    @Override
    public void error(String message) {
        this.logger.error(message);
    }

}
