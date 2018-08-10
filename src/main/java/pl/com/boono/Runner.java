package pl.com.boono;

import org.apache.commons.lang3.RandomStringUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.com.boono.model.EventModel;
import pl.com.boono.model.EventType;
import pl.com.boono.model.PacketModel;
import pl.com.boono.model.SourceType;

import java.util.*;
import java.util.stream.Collectors;

public class Runner {
    private static final Logger LOG = LoggerFactory.getLogger(Runner.class);
    private static final Random RAND = new Random(new Date().getTime());

    private List<PacketModel> testData;

    private final Map<String, ISerializerDeserializer<PacketModel>> serializerDeserializerMap;

    public Runner(Map<String, ISerializerDeserializer<PacketModel>> serializerDeserializerMap) {
        this.serializerDeserializerMap = serializerDeserializerMap;
    }

    public void run(int iterationCount) throws Exception {
        if(iterationCount < 1000) {
            throw new IllegalArgumentException("Iteration count cannot be less than 1k");
        }

        testData = buildTestData(iterationCount);
        warmup(testData);
        for(Map.Entry<String, ISerializerDeserializer<PacketModel>> serde: serializerDeserializerMap.entrySet()) {
            System.gc();
            StopWatch sw = new Slf4JStopWatch(LOG);
            List<byte[]> serialized = testData
                            .stream()
                            .map(serde.getValue()::serialize)
                            .collect(Collectors.toList());
            sw.stop("Serialize using " + serde.getKey());
            LOG.info("Bytes after serialization: " + humanReadableByteCount(serialized.stream().mapToInt(a -> a.length).sum(), false));
            StopWatch sw2 = new Slf4JStopWatch(LOG);
            List<PacketModel> deserialized = serialized
                            .stream()
                            .map(serde.getValue()::deserialize)
                            .collect(Collectors.toList());
            sw2.stop("Deserialize using " + serde.getKey());
            assert serialized.size() == deserialized.size();
        }
    }

    private void warmup(List<PacketModel> testData) {
        StopWatch warmup = new Slf4JStopWatch(LOG);
        int thousanthOfSize = testData.size() / 1000;
        List<PacketModel> shortTestData = testData
                        .stream()
                        .limit(thousanthOfSize)
                        .collect(Collectors.toList());
        for(Map.Entry<String, ISerializerDeserializer<PacketModel>> serde: serializerDeserializerMap.entrySet()) {
            List<byte[]> ser = shortTestData
                            .stream()
                            .map(serde.getValue()::serialize)
                            .collect(Collectors.toList());
            List<PacketModel> deser = ser
                            .stream()
                            .map(serde.getValue()::deserialize)
                            .collect(Collectors.toList());
            assert ser.size() == deser.size();
        }
        warmup.stop("Warmup");
    }

    private List<PacketModel> buildTestData(int iterationCount) {
        List<PacketModel> lst = new ArrayList<>();
        for(int i = 0; i < iterationCount; i++) {
            PacketModel pe = new PacketModel();
            pe.setEvents(buildTestEvents());
            pe.setContextHash(RAND.nextInt());
            pe.setTimestamp(new Date().getTime());
            pe.setAppId(RandomStringUtils.random(10, true, true));
            pe.setSourceId(RandomStringUtils.random(5, true, true));
            pe.setSourceType(SourceType.APPLICATION);
            lst.add(pe);
        }
        return lst;
    }

    private List<EventModel> buildTestEvents() {
        List<EventModel> lst = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
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

    private String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
