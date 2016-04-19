package pl.com.boono.serde;

import org.msgpack.MessagePack;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.com.boono.ISerializerDeserializer;
import pl.com.boono.entity.EventType;
import pl.com.boono.entity.PacketEntity;
import pl.com.boono.entity.SourceType;

import java.io.IOException;

@Component
@Profile("!nomsgpack")
public class MessagePackSerializerDeserializer implements ISerializerDeserializer<PacketEntity> {
    private static final MessagePack MSGPACK = new MessagePack();

    public MessagePackSerializerDeserializer() {
        MSGPACK.register(EventType.class);
        MSGPACK.register(SourceType.class);
    }
    @Override public byte[] serialize(PacketEntity obj) {
        try {
            return MSGPACK.write(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public PacketEntity deserialize(byte[] data) {
        try {
            return MSGPACK.read(data, PacketEntity.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
