package de.mcc.simra.services.osm.coloring.model

import org.springframework.data.elasticsearch.annotations.Document

/** Document as JSON
{
    "segment": "636b43e019a733f8ef7ae23bbe5a4275", // this is the id of the street or segment
    "ride": "VM2_398092568",
    "date": "17/03/2021 21:39:32",
    "location": [7.852136883884668,48.00895228050649], // longitude -> latitude
    "scary": false,
    "meta": { // the meta object can contain all kinds of text fields
        "type": "Einem Hindernis ausweichen (z.B. Hund)",
        "description": "Auto auf Gehweg/ Radweg geparkt"
    }
}
 */
@Document(indexName = "markers")
class ElasticMarker {
}