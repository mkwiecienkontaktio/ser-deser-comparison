package pl.com.boono;

import org.apache.commons.lang3.RandomStringUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.com.boono.entity.EventEntity;
import pl.com.boono.entity.EventType;
import pl.com.boono.entity.PacketEntity;
import pl.com.boono.entity.SourceType;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Runner implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(Runner.class);
    private static final Random RAND = new Random(new Date().getTime());

    @Autowired
    private Map<String, ISerializerDeserializer<PacketEntity>> serializerDeserializerMap;

    private List<PacketEntity> testData;

    @Override public void run(String... strings) throws Exception {
        int iterationCount = Integer.parseInt(strings[0]);
        if(iterationCount < 1000) {
            throw new IllegalArgumentException("Iteration count cannot be less than 1k");
        }

        testData = buildTestData(iterationCount);
        warmup(testData);
        for(Map.Entry<String, ISerializerDeserializer<PacketEntity>> serde: serializerDeserializerMap.entrySet()) {
            StopWatch sw = new Slf4JStopWatch(LOG);
            List<byte[]> serialized = testData
                            .stream()
                            .map(serde.getValue()::serialize)
                            .collect(Collectors.toList());
            sw.stop("Serialize using " + serde.getKey());
            LOG.info("Bytes after serialization: " + humanReadableByteCount(serialized.stream().mapToInt(a -> a.length).sum(), false));
            StopWatch sw2 = new Slf4JStopWatch(LOG);
            List<PacketEntity> deserialized = serialized
                            .stream()
                            .map(serde.getValue()::deserialize)
                            .collect(Collectors.toList());
            sw2.stop("Deserialize using " + serde.getKey());
            assert serialized.size() == deserialized.size();
        }
    }

    private void warmup(List<PacketEntity> testData) {
        StopWatch warmup = new Slf4JStopWatch(LOG);
        int thousanthOfSize = testData.size() / 1000;
        List<PacketEntity> shortTestData = testData
                        .stream()
                        .limit(thousanthOfSize)
                        .collect(Collectors.toList());
        for(Map.Entry<String, ISerializerDeserializer<PacketEntity>> serde: serializerDeserializerMap.entrySet()) {
            List<byte[]> ser = shortTestData
                            .stream()
                            .map(serde.getValue()::serialize)
                            .collect(Collectors.toList());
            List<PacketEntity> deser = ser
                            .stream()
                            .map(serde.getValue()::deserialize)
                            .collect(Collectors.toList());
            assert ser.size() == deser.size();
        }
        warmup.stop("Warmup");
    }

    private List<PacketEntity> buildTestData(int iterationCount) {
        List<PacketEntity> lst = new ArrayList<>();
        for(int i = 0; i < iterationCount; i++) {
            PacketEntity pe = new PacketEntity();
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

    private List<EventEntity> buildTestEvents() {
        List<EventEntity> lst = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            EventEntity ee = new EventEntity();
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
