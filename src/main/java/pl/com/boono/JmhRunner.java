package pl.com.boono;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import pl.com.boono.model.EventModel;
import pl.com.boono.model.EventType;
import pl.com.boono.model.PacketModel;
import pl.com.boono.model.SourceType;
import pl.com.boono.serde.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class JmhRunner {
    public static void main(String[] args) throws IOException, RunnerException {
        Options options = new OptionsBuilder()
                .include(".*")
                .warmupIterations(10)
                .measurementIterations(10)
                .jvmArgs("-server")
                .forks(2)
                .warmupForks(1)
                .resultFormat(ResultFormatType.TEXT)
                .build();

        new Runner(options).run();
    }

    @State(Scope.Thread)
    public static class BenchmarkState {
        private static final Random RAND = ThreadLocalRandom.current();
        public static final int EVENT_COUNT = 5;
        public PacketModel packet;
        public byte[] jacksonSerializedPacket;
        public byte[] protobufSerializedPacket;
        public byte[] avroSerializedPacket;
        public byte[] bsonSerializedPacket;
        public byte[] messagepackSerializedPacket;
        public byte[] protostuffSerializedPacket;
        public ISerializerDeserializer<PacketModel> jackson = new JacksonSerializerDeserializer();
        public ISerializerDeserializer<PacketModel> protobuf = new ProtobufSerializerDeserializer();
        public ISerializerDeserializer<PacketModel> avro = new AvroSerializerDeserializer();
        public ISerializerDeserializer<PacketModel> bson = new BSONSerializerDeserializer();
        public ISerializerDeserializer<PacketModel> messagepack = new MessagePackSerializerDeserializer();
        public ISerializerDeserializer<PacketModel> protostuff = new ProtostuffSerializerDeserializer();

        @Setup(Level.Iteration)
        public void buildTestPackets() {
            packet = new PacketModel();
            packet.setEvents(buildTestEvents());
            packet.setContextHash(RAND.nextInt());
            packet.setTimestamp(new Date().getTime());
            packet.setAppId(RandomStringUtils.random(10, true, true));
            packet.setSourceId(RandomStringUtils.random(5, true, true));
            packet.setSourceType(SourceType.APPLICATION);
            jacksonSerializedPacket = jackson.serialize(packet);
            protobufSerializedPacket = protobuf.serialize(packet);
            avroSerializedPacket = avro.serialize(packet);
            bsonSerializedPacket = bson.serialize(packet);
            messagepackSerializedPacket = messagepack.serialize(packet);
            protostuffSerializedPacket = protostuff.serialize(packet);
        }

        private List<EventModel> buildTestEvents() {
            List<EventModel> lst = new ArrayList<>();
            for(int i = 0; i < EVENT_COUNT; i++) {
                EventModel ee = new EventModel();
                ee.setTimestamp(new Date().getTime());
                ee.setUniqueId(RandomStringUtils.random(4, true, true));
                ee.setBatteryLevel((short) RAND.nextInt(100));
                ee.setRssi((short) (-100 + RAND.nextInt(50)));
                ee.setType(EventType.SCAN);
                lst.add(ee);
            }
            return lst;
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public byte[] jacksonSerialize(BenchmarkState state) {
        return state.jackson.serialize(state.packet);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public PacketModel jacksonDeserialize(BenchmarkState state) {
        return state.jackson.deserialize(state.jacksonSerializedPacket);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public byte[] protobufSerialize(BenchmarkState state) {
        return state.protobuf.serialize(state.packet);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public PacketModel protobufDeserialize(BenchmarkState state) {
        return state.protobuf.deserialize(state.protobufSerializedPacket);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public byte[] avroSerialize(BenchmarkState state) {
        return state.avro.serialize(state.packet);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public PacketModel avroDeserialize(BenchmarkState state) {
        return state.avro.deserialize(state.avroSerializedPacket);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public byte[] bsonSerialize(BenchmarkState state) {
        return state.bson.serialize(state.packet);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public PacketModel bsonDeserialize(BenchmarkState state) {
        return state.bson.deserialize(state.bsonSerializedPacket);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public byte[] messagePackSerialize(BenchmarkState state) {
        return state.messagepack.serialize(state.packet);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public PacketModel messagePackDeserialize(BenchmarkState state) {
        return state.messagepack.deserialize(state.messagepackSerializedPacket);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public byte[] protostuffSerialize(BenchmarkState state) {
        return state.protostuff.serialize(state.packet);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public PacketModel protostuffDeserialize(BenchmarkState state) {
        return state.protostuff.deserialize(state.protostuffSerializedPacket);
    }
}
