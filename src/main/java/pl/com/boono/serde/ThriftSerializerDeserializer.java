package pl.com.boono.serde;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TMemoryBuffer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.com.boono.ISerializerDeserializer;
import pl.com.boono.entity.EventEntity;
import pl.com.boono.entity.PacketEntity;
import pl.com.boono.thrift.Event;
import pl.com.boono.thrift.EventType;
import pl.com.boono.thrift.Packet;
import pl.com.boono.thrift.SourceType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.stream.Collectors;

@Component
@Profile("!nothrift")
public class ThriftSerializerDeserializer implements ISerializerDeserializer<PacketEntity> {
    private EventEntity deserializeEvent(Event event) {
        EventEntity ee = new EventEntity();
        ee.setType(pl.com.boono.entity.EventType.valueOf(event.getType().name()));
        ee.setUniqueId(event.getUniqueId());
        ee.setRssi(event.getRssi());
        ee.setBatteryLevel(event.getBatteryLevel());
        ee.setTimestamp(event.getTimestamp());
        return ee;
    }

    @Override public byte[] serialize(PacketEntity obj) {

        Packet packet = new Packet(SourceType.valueOf(obj.getSourceType().name()),
                        obj.getSourceId(),
                        obj.getTimestamp(),
                        obj.getContextHash(),
                        obj.getAppId(),
                        obj.getEvents().stream().map(e -> new Event(
                                        e.getRssi(),
                                        e.getBatteryLevel(),
                                        EventType.valueOf(e.getType().name()),
                                        e.getTimestamp(),
                                        e.getUniqueId())).collect(Collectors.toList()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TIOStreamTransport buf = new TIOStreamTransport(baos);
        try {
            packet.write(new TBinaryProtocol(buf));
            return baos.toByteArray();
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public PacketEntity deserialize(byte[] data) {
        try {
            TMemoryBuffer buf = new TMemoryBuffer(data.length);
            buf.write(data);
            Packet packet = new Packet();
            packet.read(new TBinaryProtocol(buf));
            PacketEntity pe = new PacketEntity();
            pe.setEvents(packet.getEvents().stream().map(this::deserializeEvent).collect(Collectors.toList()));
            pe.setSourceType(pl.com.boono.entity.SourceType.valueOf(packet.getSourceType().name()));
            pe.setSourceId(packet.getSourceId());
            pe.setAppId(packet.getAppId());
            pe.setTimestamp(packet.getTimestamp());
            pe.setContextHash(packet.getContextHash());
            return pe;
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }
}
