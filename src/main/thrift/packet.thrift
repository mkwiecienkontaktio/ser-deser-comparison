namespace java pl.com.boono.thrift

enum SourceType {
    APPLICATION = 1,
    CLOUD_BEACON = 2
}

enum EventType {
    SCAN = 1,
    MONITORING = 2
}

struct Event {
    1: i16 rssi,
    2: i16 batteryLevel,
    3: EventType type,
    4: i64 timestamp,
    5: string uniqueId
}

struct Packet {
    1: SourceType sourceType,
    2: string sourceId,
    3: i64 timestamp,
    4: i32 contextHash,
    5: string appId,
    6: list<Event> events
}
