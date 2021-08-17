# README

This project is part of [simra-services](https://github.com/simra-project/simra-services). Please consult this parent
repository for information on the general architecture.

This service parses colored osm files and indexes them in Elasticsearch.
Later on, this service should create the colored osm files directly, se parent repository for further information.

## Startup

Start a single Elasticsearch node and Kibana with docker-compose:
```bash
cd docker-compose
docker-compose up
```

Then run *OsmColoringApplication.kt*. Based on the standard configuration (defined in *application.yml*), this will 
index all data from the geo-jsons directory.
Kibana is then available at *localhost:5601*.

To see coroutine names in log, supply the `-Dkotlinx.coroutines.debug` VM option.

## Architecture

This osm-coloring-service currently comprises three internal services that communicate via Kotlin channels:
ReaderService -> TransformerService -> ElasticIndexService

The reader service reads in geo-json files, the transformer service creates ElasticSegments that then can be indexed
by the ElasticIndexService.
