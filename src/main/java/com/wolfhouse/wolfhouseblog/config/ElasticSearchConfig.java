package com.wolfhouse.wolfhouseblog.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public static final String API_KEY = System.getenv("ELASTIC_SEARCH_API_KEY");
    public static final String USERNAME = System.getenv("ELASTIC_SEARCH_USERNAME");
    public static final String PASSWORD = System.getenv("ELASTIC_SEARCH_PASSWORD");
    public String host;
    public Integer maxResultWindow;


    @Bean(destroyMethod = "close")
    public ElasticsearchClient elasticsearchClient(@Qualifier("esObjectMapper") ObjectMapper esObjectMapper) {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(USERNAME, PASSWORD));
        RestClient restClient = RestClient.builder(HttpHost.create(host))
                                          .setHttpClientConfigCallback(httpClientBuilder ->
                                                                           httpClientBuilder.setDefaultCredentialsProvider(
                                                                               credentialsProvider))
                                          .build();
        return new ElasticsearchClient(new RestClientTransport(restClient,
                                                               new JacksonJsonpMapper(esObjectMapper)));
    }
}
