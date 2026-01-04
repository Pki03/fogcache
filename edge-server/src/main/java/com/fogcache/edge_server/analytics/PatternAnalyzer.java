package com.fogcache.edge_server.analytics;

import com.fogcache.edge_server.logging.RequestLog;
import com.fogcache.edge_server.logging.RequestLogger;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PatternAnalyzer {

    private final RequestLogger logger;

    public PatternAnalyzer(RequestLogger logger) {
        this.logger = logger;
    }

    public List<RequestPattern> analyze() {

        List<RequestLog> logs = logger.getLogs();

        Map<String, List<RequestLog>> grouped =
                logs.stream().collect(Collectors.groupingBy(RequestLog::getKey));

        List<RequestPattern> patterns = new ArrayList<>();

        for (Map.Entry<String, List<RequestLog>> entry : grouped.entrySet()) {

            String key = entry.getKey();
            List<RequestLog> list = entry.getValue();

            long count = list.size();
            long hits = list.stream().filter(RequestLog::isHit).count();

            long minTime = list.stream().mapToLong(RequestLog::getTimestamp).min().orElse(0);
            long maxTime = list.stream().mapToLong(RequestLog::getTimestamp).max().orElse(0);

            double hitRatio = count == 0 ? 0 : (double) hits / count;
            double accessRate = (maxTime == minTime) ? count : (count * 1000.0) / (maxTime - minTime);

            String lastValue = list.get(list.size() - 1).getValue();

            patterns.add(new RequestPattern(key, count, hitRatio, accessRate, lastValue));

        }

        patterns.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        return patterns;
    }
}
