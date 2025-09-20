package com.wolfhouse.wolfhouseblog.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author linexsong
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "custom.elasticsearch")
public class ElasticSearchConfig {
    public final String API_KEY = System.getenv("ELASTIC_SEARCH_API_KEY");
    public String host;

    @Bean(destroyMethod = "close")
    public ElasticsearchClient elasticsearchClient(ObjectMapper defaultObjectMapper) {
        RestClient restClient = RestClient.builder(HttpHost.create(host))
                                          .setDefaultHeaders(new Header[]{new BasicHeader("Authorization",
                                                                                          "ApiKey " + API_KEY)})
                                          .build();
        return new ElasticsearchClient(new RestClientTransport(restClient,
                                                               new JacksonJsonpMapper(defaultObjectMapper)));
    }
}
