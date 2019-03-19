package dev.jdanielper.logour;

import java.util.Map;

public class Event {
    private String client;
    private long createdAt;
    private String hostname;
    private String type;
    private String message;

    private Map<String, Object> custom;

    public String getClient() {
        return client;
    }

    public Event client(String client) {
        this.client = client;
        return this;
    }

    public Event createdAt(long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Event hostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public Event type(String type) {
        this.type = type;
        return this;
    }

    public Event message(String message) {
        this.message = message;
        return this;
    }

    public Event custom(Map<String, Object> custom) {
        this.custom = custom;
        return this;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getHostname() {
        return hostname;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getCustom() {
        return custom;
    }
}
