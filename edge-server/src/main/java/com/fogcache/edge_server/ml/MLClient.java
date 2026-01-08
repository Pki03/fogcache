package com.fogcache.edge_server.ml;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fogcache.edge_server.ml.PredictionResult;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Component
public class MLClient {

    private final RestTemplate rest = new RestTemplate();

    @Value("${fogcache.ml.url}")
    private String mlUrl;

    public PredictionResult predict(FeatureVector features) {
        try {
            return rest.postForObject(
                    mlUrl + "/predict",
                    features,
                    PredictionResult.class
            );
        } catch (Exception e) {
            System.out.println("⚠️ ML unavailable — skipping inference");
            return null;
        }
    }
}


