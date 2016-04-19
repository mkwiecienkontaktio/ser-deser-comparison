package pl.com.boono;

public interface ISerializerDeserializer<T> {
    byte[] serialize(T obj);
    T deserialize(byte[] data);
}
