package pl.com.boono.serde;

import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import pl.com.boono.ISerializerDeserializer;
import pl.com.boono.avro.Event;
import pl.com.boono.avro.EventType;
import pl.com.boono.avro.Packet;
import pl.com.boono.avro.SourceType;
import pl.com.boono.model.EventModel;
import pl.com.boono.model.PacketModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

public class AvroSerializerDeserializer implements ISerializerDeserializer<PacketModel> {
    DatumWriter<Packet> DATUM_WRITER = new SpecificDatumWriter<>(Packet.getClassSchema());
    DatumReader<Packet> DATUM_READER = new SpecificDatumReader<>(Packet.getClassSchema());

    private EventModel deserializeEvent(Event event) {
        EventModel ee = new EventModel();
        ee.setType(pl.com.boono.model.EventType.valueOf(event.getType().name()));
        ee.setUniqueId(event.getUniqueId().toString());
        ee.setRssi(event.getRssi().shortValue());
        ee.setBatteryLevel(event.getBatteryLevel().shortValue());
        ee.setTimestamp(event.getTimestamp());
        return ee;
    }

    @Override public byte[] serialize(PacketModel obj) {
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
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(baos, null);
            DATUM_WRITER.write(packet, encoder);
            encoder.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public PacketModel deserialize(byte[] data) {
        try {
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            Packet packet = DATUM_READER.read(null, decoder);
            PacketModel pe = new PacketModel();
            pe.setSourceType(pl.com.boono.model.SourceType.valueOf(packet.getSourceType().name()));
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
