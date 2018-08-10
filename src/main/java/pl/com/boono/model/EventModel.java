package pl.com.boono.model;

import org.msgpack.annotation.Message;

@Message
public class EventModel {
    private Short rssi;
    private Short batteryLevel;
    private EventType type;
    private Long timestamp;
    private String uniqueId;

    public Short getRssi() {
        return rssi;
    }

    public void setRssi(Short rssi) {
        this.rssi = rssi;
    }

    public Short getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Short batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
