package de.mcc.simra.services.osm.coloring.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.core.geo.GeoJsonPolygon

/** Document as JSON
{
    "id": "636b43e019a733f8ef7ae23bbe5a4275", // this is the md5 hash of the geojson id
    "textId": "[3945, 7047, 7050, 7674, 13687].0", // this is the id found in the geojson (can be very long)
    "region": "Freiburg", // region, derived by filename
    "type": "street", // can only be street or junction
    "rides": 1,
    "score": 1.0,
    "length":43.66092995514574, // this field only exists if type == street
    "shape": {
        "type": "Polygon",
        "coordinates": [ // longitude -> latitude
            [[7.851693418318069,48.00775683624214],[7.851823225074674,48.00769993584033],[7.851983411811289,48.0079146860531],[7.852014038079017,48.00789957724747],[7.852045445286896,48.00796699796769],[7.85205075719911,48.008004970908566],[7.852086247121062,48.00805254955616],[7.85206731118621,48.00807070130215],[7.852105908451206,48.008202300794984],[7.852122654022001,48.008199117702816],[7.8521247268189756,48.00826646305245],[7.85213592216419,48.00830463419625],[7.85212600274331,48.00830791794237],[7.852129698361978,48.008427988906554],[7.852197087067949,48.00889945387457],[7.852248651088024,48.0090168633191],[7.852970986221411,48.00979539994138],[7.852860048951659,48.00988226856969],[7.852104361328513,48.0089643420234],[7.8520689912580375,48.00897772264598],[7.852061889834658,48.008912752305],[7.852032146831996,48.00887662376343],[7.852056665418803,48.00886495455215],[7.851999232361095,48.00833950421022],[7.8519335668717165,48.00813858006232],[7.851929468215682,48.008139628662526],[7.8519190602058515,48.00809646260399],[7.851916325919059,48.008094311031705],[7.8517396194424265,48.007873945037474],[7.851693418318069,48.00775683624214]]
        ]
    }
}
 */
@Document(indexName = "#{@osmColoringConfigData.elasticSegmentIndexName}")
data class ElasticSegment(

    @Id
    val id: String,

    @Field(type = FieldType.Keyword)
    val textId: String,

    @Field(type = FieldType.Keyword)
    val region: String,

    @Field(type = FieldType.Keyword)
    val type: String,

    @Field(type = FieldType.Integer)
    val rides: Int,

    @Field(type = FieldType.Double)
    val score: Double,

    @Field(type = FieldType.Double)
    val length: Double?,

    // TODO add markers as nested fields as done here: https://www.baeldung.com/spring-data-elasticsearch-tutorial

    val shape: GeoJsonPolygon

)