package com.fogcache.edge_server.ml;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fogcache.edge_server.ml.PredictionResult;

import java.util.Map;


@Component
public class MLClient {

    private final RestTemplate rest = new RestTemplate();

    public PredictionResult predict(FeatureVector features) {
        return rest.postForObject(
                "http://localhost:5001/predict",
                features,
                PredictionResult.class
        );
    }
}

