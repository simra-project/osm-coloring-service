package de.mcc.simra.services.osm.coloring.repositories;

import de.mcc.simra.services.osm.coloring.model.ElasticSegment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticRepository extends ElasticsearchRepository<ElasticSegment, String> {

}
