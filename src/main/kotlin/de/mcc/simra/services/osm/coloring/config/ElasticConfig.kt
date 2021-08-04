package de.mcc.simra.services.osm.coloring.config

import org.elasticsearch.client.RestHighLevelClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories


@Configuration
@EnableElasticsearchRepositories(basePackages = ["de.mcc.simra.services.osm.coloring.repositories"])
class ElasticConfig(val osmColoringConfigData: OsmColoringConfigData) {

    @Bean
    fun client(): RestHighLevelClient? {
        val clientConfiguration = ClientConfiguration.builder()
            .connectedTo(osmColoringConfigData.elasticHostAndPort)
            .withSocketTimeout(60000) // 60 second timeout
            .build()
        return RestClients.create(clientConfiguration).rest()
    }

    @Bean
    fun elasticsearchTemplate(): ElasticsearchOperations? {
        return ElasticsearchRestTemplate(client()!!)
    }


}