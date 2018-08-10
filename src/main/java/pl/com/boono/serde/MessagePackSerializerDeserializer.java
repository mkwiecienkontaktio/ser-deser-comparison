package pl.com.boono.serde;

import org.msgpack.MessagePack;
import pl.com.boono.ISerializerDeserializer;
import pl.com.boono.model.EventType;
import pl.com.boono.model.PacketModel;
import pl.com.boono.model.SourceType;

import java.io.IOException;

public class MessagePackSerializerDeserializer implements ISerializerDeserializer<PacketModel> {
    private static final MessagePack MSGPACK = new MessagePack();

    public MessagePackSerializerDeserializer() {
        MSGPACK.register(EventType.class);
        MSGPACK.register(SourceType.class);
    }

    @Override
    public byte[] serialize(PacketModel obj) {
        try {
            return MSGPACK.write(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PacketModel deserialize(byte[] data) {
        try {
            return MSGPACK.read(data, PacketModel.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
