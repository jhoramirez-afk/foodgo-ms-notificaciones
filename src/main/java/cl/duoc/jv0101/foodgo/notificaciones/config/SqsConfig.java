package cl.duoc.jv0101.foodgo.notificaciones.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "foodgo.cola.habilitada", havingValue = "true")
public class SqsConfig {
    @Bean(destroyMethod = "close")
    public SqsClient sqsClient(@Value("${foodgo.cola.region:us-east-1}") String region) {
        return SqsClient.builder().region(Region.of(region))
                .httpClientBuilder(UrlConnectionHttpClient.builder()
                        .connectionTimeout(Duration.ofSeconds(3)).socketTimeout(Duration.ofSeconds(5)))
                .overrideConfiguration(c -> c.apiCallTimeout(Duration.ofSeconds(12))
                        .apiCallAttemptTimeout(Duration.ofSeconds(6)))
                .build();
    }
}
