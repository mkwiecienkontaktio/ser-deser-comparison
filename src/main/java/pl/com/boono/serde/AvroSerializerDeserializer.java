package pl.com.boono.serde;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.com.boono.ISerializerDeserializer;
import pl.com.boono.avro.Event;
import pl.com.boono.avro.EventType;
import pl.com.boono.avro.Packet;
import pl.com.boono.avro.SourceType;
import pl.com.boono.entity.EventEntity;
import pl.com.boono.entity.PacketEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
@Profile("!noavro")
public class AvroSerializerDeserializer implements ISerializerDeserializer<PacketEntity> {
    DatumWriter<Packet> DATUM_WRITER = new SpecificDatumWriter<>(Packet.getClassSchema());
    DatumReader<Packet> DATUM_READER = new SpecificDatumReader<>(Packet.getClassSchema());

    private EventEntity deserializeEvent(Event event) {
        EventEntity ee = new EventEntity();
        ee.setType(pl.com.boono.entity.EventType.valueOf(event.getType().name()));
        ee.setUniqueId(event.getUniqueId().toString());
        ee.setRssi(event.getRssi().shortValue());
        ee.setBatteryLevel(event.getBatteryLevel().shortValue());
        ee.setTimestamp(event.getTimestamp());
        return ee;
    }

    @Override public byte[] serialize(PacketEntity obj) {
        try {
            Packet packet = new Packet(
                            SourceType.valueOf(obj.getSourceType().name()),
                            obj.getSourceId(),
                            obj.getTimestamp(),
                            obj.getContextHash(),
                            obj.getAppId(),
                            obj.getEvents().stream().map(ee -> new Event(
                                            ee.getRssi().intValue(),
                                            ee.getBatteryLevel().intValue(),
                                            EventType.valueOf(ee.getType().name()),
                                            ee.getTimestamp(),
                                            ee.getUniqueId())
                            ).collect(Collectors.toList()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataFileWriter<Packet> dfw = new DataFileWriter<>(DATUM_WRITER);
            dfw = dfw.create(packet.getSchema(), baos);
            dfw.append(packet);
            dfw.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public PacketEntity deserialize(byte[] data) {
        try {
            DataFileReader<Packet> dfr = new DataFileReader<>(new SeekableByteArrayInput(data), DATUM_READER);
            Packet packet = dfr.next();
            PacketEntity pe = new PacketEntity();
            pe.setSourceType(pl.com.boono.entity.SourceType.valueOf(packet.getSourceType().name()));
            pe.setSourceId(packet.getSourceId().toString());
            pe.setTimestamp(packet.getTimestamp());
            pe.setAppId(packet.getAppId().toString());
            pe.setContextHash(packet.getContextHash());
            pe.setEvents(packet.getEvents().stream().map(this::deserializeEvent).collect(Collectors.toList()));
            return pe;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
