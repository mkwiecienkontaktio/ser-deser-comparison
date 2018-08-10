package pl.com.boono.model;

import org.msgpack.annotation.Message;

import java.util.List;

@Message
public class PacketModel {
    private SourceType sourceType;
    private String sourceId;
    private Long timestamp;
    private int contextHash;
    private String appId;
    private List<EventModel> events;

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getContextHash() {
        return contextHash;
    }

    public void setContextHash(int contextHash) {
        this.contextHash = contextHash;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public List<EventModel> getEvents() {
        return events;
    }

    public void setEvents(List<EventModel> events) {
        this.events = events;
    }
}
