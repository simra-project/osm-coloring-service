# README

This service parses colored osm files and indexes them in elastic.
Later on, this service will create the colored osm files directly.

## Startup

Start a single elastic node and kibana with docker-compose:
```bash
cd docker-compose
docker-compose up
```

To see coroutine names in log, supply the `-Dkotlinx.coroutines.debug` VM option.