package pl.com.boono.serde;

import io.protostuff.ByteBufferInput;
import io.protostuff.Input;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffOutput;
import pl.com.boono.ISerializerDeserializer;
import pl.com.boono.model.EventModel;
import pl.com.boono.model.EventType;
import pl.com.boono.model.PacketModel;
import pl.com.boono.model.SourceType;
import pl.com.boono.protobuf.Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.stream.Collectors;

public class ProtostuffSerializerDeserializer implements ISerializerDeserializer<PacketModel> {

    private Packet.Event serializeEvent(EventModel event) {
        Packet.Event ser = new Packet.Event();
        ser.setRssi(event.getRssi().intValue());
        ser.setBatteryLevel(event.getBatteryLevel().intValue());
        ser.setTimestamp(event.getTimestamp());
        ser.setUniqueId(event.getUniqueId());
        ser.setType(Packet.Event.EventType.valueOf(event.getType().name()));
        return ser;
    }

    private EventModel deserializeEvent(Packet.Event event) {
        EventModel ee = new EventModel();
        ee.setType(EventType.valueOf(event.getType().name()));
        ee.setUniqueId(event.getUniqueId());
        ee.setRssi(event.getRssi().shortValue());
        ee.setBatteryLevel(event.getBatteryLevel().shortValue());
        ee.setTimestamp(event.getTimestamp());
        return ee;
    }

    @Override
    public byte[] serialize(PacketModel obj) {
        try {
            Packet packet = new Packet();
            packet.setAppId(obj.getAppId());
            packet.setSourceId(obj.getSourceId());
            packet.setSourceType(Packet.SourceType.valueOf(obj.getSourceType().name()));
            packet.setContextHash(obj.getContextHash());
            packet.setTimestamp(obj.getTimestamp());
            packet.setEventList(obj.getEvents().stream().map(this::serializeEvent).collect(Collectors.toList()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ProtostuffOutput output = new ProtostuffOutput(LinkedBuffer.allocate(), baos);
            packet.writeTo(output, packet);
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PacketModel deserialize(byte[] data) {
        Packet packet = new Packet();
        ByteBuffer bb = ByteBuffer.wrap(data);
        Input input = new ByteBufferInput(bb, true);
        try {
            packet.mergeFrom(input, packet);
            PacketModel pe = new PacketModel();
            pe.setSourceType(SourceType.valueOf(packet.getSourceType().name()));
            pe.setSourceId(packet.getSourceId());
            pe.setTimestamp(packet.getTimestamp());
            pe.setAppId(packet.getAppId());
            pe.setContextHash(packet.getContextHash());
            pe.setEvents(packet.getEventList().stream().map(this::deserializeEvent).collect(Collectors.toList()));
            return pe;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
