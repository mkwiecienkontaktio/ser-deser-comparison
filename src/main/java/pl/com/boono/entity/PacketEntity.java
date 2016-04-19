package pl.com.boono.entity;

import java.util.List;

public class PacketEntity {
    private SourceType sourceType;
    private String sourceId;
    private Long timestamp;
    private int contextHash;
    private String appId;
    private List<EventEntity> events;

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

    public List<EventEntity> getEvents() {
        return events;
    }

    public void setEvents(List<EventEntity> events) {
        this.events = events;
    }
}
