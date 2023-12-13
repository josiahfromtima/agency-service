package com.tima.platform.util;

import java.util.logging.Logger;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
public class LoggerHelper {
    Logger logger;
    private LoggerHelper(String className) {
        logger = Logger.getLogger(className);
    }

    public static LoggerHelper newInstance(String className) {
        return new LoggerHelper(className);
    }
    private String parse(Object... args) {
        return String.format("%s".repeat(args.length), args);
    }

    public void info(Object... args) {
        logInfo(parse( args));
    }

   public void trace(Object... args) {
        traceInfo(parse( args));
    }

   public void error(Object... args) {
        errrorInfo(parse( args));
    }

    private void logInfo(String message) {
        logger.info(message);
    }

    private void traceInfo(String message) {
        logger.warning(message);
    }
    private void errrorInfo(String message) {
        logger.severe(message);
    }

}
