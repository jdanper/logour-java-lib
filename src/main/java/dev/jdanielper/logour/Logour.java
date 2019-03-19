package dev.jdanielper.logour;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.ResourceBundle;

public class Logour {
    private static EventSender sender;

    private static Logger log;

    private final ObjectMapper objectMapper;

    private enum EventTypes {
        DEBUG,
        INFO,
        WARNING,
        ERROR
    }

    private Logour() {
        objectMapper = new ObjectMapper();
    }

    public static void init(final String client) {
        sender = new EventSender(client);
    }

    public static <T> Logour getLogger(Class<T> tClass) {
        log = LogManager.getLogger(tClass);

        if(sender == null) {
            final ResourceBundle bundle = ResourceBundle.getBundle("logour");
            final String client = bundle.getString("client");

            sender = new EventSender(client);
        }

        return new Logour();
    }

    public void debug(String msg) {
        log.debug(msg);
        sendEvent(msg, EventTypes.DEBUG);
    }

    public void debug(String msg, Throwable throwable) {
        log.debug(msg, throwable);
        sendEvent(msg, EventTypes.DEBUG, throwable);
    }

    public void info(final String msg) {
        sendEvent(msg, EventTypes.INFO);
        log.info(msg);
    }

    public void info(String msg, Throwable throwable) {
        log.info(msg, throwable);
        sendEvent(msg, EventTypes.INFO);
    }

    public void warn(String msg) {
        log.warn(msg);
        sendEvent(msg, EventTypes.WARNING);
    }

    public void warn(String msg, Throwable throwable) {
        log.warn(msg, throwable);
        sendEvent(msg, EventTypes.WARNING);
    }

    public void error(String msg) {
        log.error(msg);
        sendEvent(msg, EventTypes.ERROR);
    }

    public void error(String msg, Throwable throwable) {
        log.error(msg, throwable);
        sendEvent(msg, EventTypes.ERROR, throwable);
    }

    private void sendEvent(final String msg, final EventTypes type) {
        sender.send(new Event().message(msg).type(type.name()));
    }

    private void sendEvent(final String msg, final EventTypes type, final Throwable throwable) {
        Map<String, Object> errDetails = objectMapper.convertValue(throwable, Map.class);

        sender.send(new Event()
                .message(msg)
                .type(type.name())
                .custom(errDetails));
    }
}