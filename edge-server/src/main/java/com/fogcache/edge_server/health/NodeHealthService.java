//package com.fogcache.edge_server.health;
//
//import com.fogcache.edge_server.cluster.NodeRegistry;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class NodeHealthService {
//
//    private final RestTemplate rest = new RestTemplate();
//    private final Map<String, Boolean> health = new ConcurrentHashMap<>();
//
//    @Scheduled(fixedDelay = 3000)
//    public void checkNodes() {
//        for (String node : NodeRegistry.getNodes()) {
//            try {
//                rest.getForObject(node + "/health", String.class);
//                health.put(node, true);
//                System.out.println("🟢 Healthy: " + node);
//            } catch (Exception e) {
//                health.put(node, false);
//                System.out.println("🔴 Dead: " + node);
//            }
//        }
//    }
//
//    public boolean isHealthy(String node) {
//        return health.getOrDefault(node, false);
//    }
//}
