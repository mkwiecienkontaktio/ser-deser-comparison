package pl.com.boono.serde;

import com.google.protobuf.InvalidProtocolBufferException;
import pl.com.boono.ISerializerDeserializer;
import pl.com.boono.model.EventModel;
import pl.com.boono.model.EventType;
import pl.com.boono.model.PacketModel;
import pl.com.boono.model.SourceType;
import pl.com.boono.protobuf.PacketOuterClass;

import java.util.stream.Collectors;

public class ProtobufSerializerDeserializer implements ISerializerDeserializer<PacketModel> {

    private PacketOuterClass.Packet.Event serializeEvent(EventModel event) {
        return PacketOuterClass.Packet.Event.newBuilder()
                .setRssi(event.getRssi())
                .setBatteryLevel(event.getBatteryLevel())
                .setTimestamp(event.getTimestamp())
                .setUniqueId(event.getUniqueId())
                .setType(PacketOuterClass.Packet.Event.EventType.valueOf(event.getType().name()))
                .build();
    }

    private EventModel deserializeEvent(PacketOuterClass.Packet.Event event) {
        EventModel ee = new EventModel();
        ee.setType(EventType.valueOf(event.getType().name()));
        ee.setUniqueId(event.getUniqueId());
        ee.setRssi((short) event.getRssi());
        ee.setBatteryLevel((short) event.getBatteryLevel());
        ee.setTimestamp(event.getTimestamp());
        return ee;
    }

    @Override
    public byte[] serialize(PacketModel obj) {
        PacketOuterClass.Packet.Builder builder = PacketOuterClass.Packet.newBuilder()
                .setAppId(obj.getAppId())
                .setSourceId(obj.getSourceId())
                .setSourceType(PacketOuterClass.Packet.SourceType.valueOf(obj.getSourceType().name()))
                .setContextHash(obj.getContextHash())
                .setTimestamp(obj.getTimestamp());
        obj.getEvents().forEach(e -> builder.addEvent(serializeEvent(e)));
        return builder.build().toByteArray();
    }

    @Override
    public PacketModel deserialize(byte[] data) {
        try {
            PacketOuterClass.Packet packet = PacketOuterClass.Packet.parseFrom(data);
            PacketModel pe = new PacketModel();
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
