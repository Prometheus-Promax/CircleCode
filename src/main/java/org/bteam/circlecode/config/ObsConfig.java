package org.bteam.circlecode.config;

import com.obs.services.ObsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObsConfig {
    @Value("${huawei.obs.end-point}")
    private String endpoint;
    @Value("${huawei.obs.access-key}")
    private String accessKey;
    @Value("${huawei.obs.secret-key}")
    private String secretKey;

    @Bean
    public ObsClient obsClient() {
        return new ObsClient(accessKey, secretKey, endpoint);
    }
}
