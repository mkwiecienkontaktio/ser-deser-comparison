package pl.com.boono.serde;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.com.boono.ISerializerDeserializer;
import pl.com.boono.entity.EventEntity;
import pl.com.boono.entity.EventType;
import pl.com.boono.entity.PacketEntity;
import pl.com.boono.entity.SourceType;
import pl.com.boono.protobuf.PacketOuterClass;

import java.util.stream.Collectors;

@Component
@Profile("!noprotobuf")
public class ProtobufSerializerDeserializer implements ISerializerDeserializer<PacketEntity> {

    private PacketOuterClass.Packet.Event serializeEvent(EventEntity event) {
        return PacketOuterClass.Packet.Event.newBuilder()
                        .setRssi(event.getRssi())
                        .setBatteryLevel(event.getBatteryLevel())
                        .setTimestamp(event.getTimestamp())
                        .setUniqueId(event.getUniqueId())
                        .setType(PacketOuterClass.Packet.Event.EventType.valueOf(event.getType().name()))
                        .build();
    }

    private EventEntity deserializeEvent(PacketOuterClass.Packet.Event event) {
        EventEntity ee = new EventEntity();
        ee.setType(EventType.valueOf(event.getType().name()));
        ee.setUniqueId(event.getUniqueId());
        ee.setRssi((short)event.getRssi());
        ee.setBatteryLevel((short)event.getBatteryLevel());
        ee.setTimestamp(event.getTimestamp());
        return ee;
    }

    @Override public byte[] serialize(PacketEntity obj) {
        PacketOuterClass.Packet.Builder builder = PacketOuterClass.Packet.newBuilder()
                        .setAppId(obj.getAppId())
                        .setSourceId(obj.getSourceId())
                        .setSourceType(PacketOuterClass.Packet.SourceType.valueOf(obj.getSourceType().name()))
                        .setContextHash(obj.getContextHash())
                        .setTimestamp(obj.getTimestamp());
        obj.getEvents().forEach(e -> builder.addEvent(serializeEvent(e)));
        return builder.build().toByteArray();
    }

    @Override public PacketEntity deserialize(byte[] data) {
        try {
            PacketOuterClass.Packet packet = PacketOuterClass.Packet.parseFrom(data);
            PacketEntity pe = new PacketEntity();
            pe.setSourceType(SourceType.valueOf(packet.getSourceType().name()));
            pe.setSourceId(packet.getSourceId());
            pe.setTimestamp(packet.getTimestamp());
            pe.setAppId(packet.getAppId());
            pe.setContextHash(packet.getContextHash());
            pe.setEvents(packet.getEventList().stream().map(this::deserializeEvent).collect(Collectors.toList()));
            return pe;
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
