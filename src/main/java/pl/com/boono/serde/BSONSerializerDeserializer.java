package pl.com.boono.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import pl.com.boono.ISerializerDeserializer;
import pl.com.boono.model.PacketModel;

import java.io.IOException;

public class BSONSerializerDeserializer implements ISerializerDeserializer<PacketModel> {
    private static final TypeReference<PacketModel> REFERENCE = new TypeReference<PacketModel>() {
    };
    private static final ObjectMapper MAPPER = new ObjectMapper(new BsonFactory());

    @Override
    public byte[] serialize(PacketModel obj) {
        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PacketModel deserialize(byte[] data) {
        try {
            return MAPPER.readValue(data, REFERENCE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
