package pl.com.boono.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.com.boono.ISerializerDeserializer;
import pl.com.boono.entity.PacketEntity;

import java.io.IOException;

@Component
@Profile("!nobson")
public class BSONSerializerDeserializer implements ISerializerDeserializer<PacketEntity> {
    private static final TypeReference<PacketEntity> REFERENCE = new TypeReference<PacketEntity>() {};
    private static final ObjectMapper MAPPER = new ObjectMapper(new BsonFactory());
    @Override public byte[] serialize(PacketEntity obj) {
        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public PacketEntity deserialize(byte[] data) {
        try {
            return MAPPER.readValue(data, REFERENCE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
