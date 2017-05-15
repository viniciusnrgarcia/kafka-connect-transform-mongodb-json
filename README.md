# kafka-connect-transform-mongodb-json
Transforma o paylod do oplog(mongodb) em json.

### Como usar
* Copiar a lib `jackson-datatype-jsr310-2.8.8.jar` para o diretório `confluent/share/java/kafka`.
* Copiar o jar para o diretório `confluent/share/java/kafka-connect-transform-mongodb-json`.
* Adicionar o transform na criação do connector.
* Para adicionar mais de uma partição com garantia de ordem é necessário informar o 
jsonPath que representa o id, considerando que o transform parte do payload, portanto não é necessário coloca-lo.

```
{
  "name": "mongodb-risk-source-connector",
  "config": {
    "connector.class": "io.debezium.connector.mongodb.MongoDbConnector",
    "mongodb.hosts": "erp-risk-mongo01-hmg.netshoes.local:27017,erp-risk-mongo02-hmg.netshoes.local:27017,erp-risk-mongo03-hmg.netshoes.local:27017",
    "mongodb.name": "risk",
    "mongodb.user": "application" ,
    "mongodb.password" : "********",
    "collection.whitelist": "risk.riskAnalytics",
    "transforms": "JsonTransformation",
    "transforms.JsonTransformation.type": "com.netshoes.kafka.connect.transform.mongodb.json.transform.JsonTransformation",
    "transforms.JsonTransformation.field.key" : "_id"
  }
}
```

Basta adicionar os campos transforms e transforms.alias.type (no exemplo acima o alias é JsonTransformation).
Para mais informações sobre os transforms do kafka: http://kafka.apache.org/documentation.html#connect_transforms

### Detalhamento

* Exemplo do payload criado no kafka sem a utilização do transform:

```
{  
   "schema":{  
      "type":"struct",
      "fields":[  
         {  
            "type":"string",
            "optional":true,
            "name":"io.debezium.data.Json",
            "version":1,
            "field":"after"
         },
         {  
            "type":"string",
            "optional":true,
            "name":"io.debezium.data.Json",
            "version":1,
            "field":"patch"
         },
         {  
            "type":"struct",
            "fields":[  
               {  
                  "type":"string",
                  "optional":false,
                  "field":"name"
               },
               {  
                  "type":"string",
                  "optional":false,
                  "field":"rs"
               },
               {  
                  "type":"string",
                  "optional":false,
                  "field":"ns"
               },
               {  
                  "type":"int32",
                  "optional":false,
                  "field":"sec"
               },
               {  
                  "type":"int32",
                  "optional":false,
                  "field":"ord"
               },
               {  
                  "type":"int64",
                  "optional":true,
                  "field":"h"
               },
               {  
                  "type":"boolean",
                  "optional":true,
                  "field":"initsync"
               }
            ],
            "optional":false,
            "name":"io.debezium.connector.mongo.Source",
            "version":1,
            "field":"source"
         },
         {  
            "type":"string",
            "optional":true,
            "field":"op"
         },
         {  
            "type":"int64",
            "optional":true,
            "field":"ts_ms"
         }
      ],
      "optional":false,
      "name":"risk.risk.riskAnalytics.Envelope"
   },
   "payload":{  
      "after":"{\"_id\" : {\"code\" : \"NS50017\",\"origin\" : \"NS\",\"country\" : \"BR\"},\"_class\" : \"com.netshoes.risk.entities.analytics.RiskAnalytics\",\"processingQuantity\" : 1,\"lastDataAnalysis\" : {\"analysisDate\" : {\"$date\" : 1494419590492},\"orderAnalysisStatus\" : \"MANUAL\",\"ruleId\" : \"boaVista\"},\"rules\" : [{\"orderAnalysisId\" : \"59130623ed74eb2b7e006ea5\",\"ruleId\" : \"velocity\",\"orderAnalysisStatus\" : \"APPROVED\"}, {\"orderAnalysisId\" : \"59130623ed74eb2b7e006ea5\",\"ruleId\" : \"boaVista\",\"orderAnalysisStatus\" : \"MANUAL\"}]}",
      "patch":null,
      "source":{  
         "name":"risk",
         "rs":"rs0",
         "ns":"risk.riskAnalytics",
         "sec":1494419589,
         "ord":3,
         "h":-632282462378252721,
         "initsync":null
      },
      "op":"c",
      "ts_ms":1494421545120
   }
}
```
* Exemplo do payload criado no kafka a partir do transform:

```
{
   "schema":null,
   "payload":{
      "_id":{
         "code":"NS36549",
         "origin":"NS",
         "country":"BR"
      },
      "_class":"com.netshoes.risk.entities.analytics.RiskAnalytics",
      "processingQuantity":12,
      "lastDataAnalysis":{
         "analysisDate":{
            "$date":1465474381095
         },
         "orderAnalysisStatus":"MANUAL",
         "ruleId":"boaVista"
      },
      "rules":[
         {
            "orderAnalysisId":"57595d2560b2b195a536be01",
            "ruleId":"historical",
            "orderAnalysisStatus":"REFUSED"
         },
         {
            "orderAnalysisId":"57595d2560b2b195a536be01",
            "ruleId":"fraudAnalysis",
            "orderAnalysisStatus":"APPROVED"
         },
         {
            "orderAnalysisId":"57595d2560b2b195a536be01",
            "ruleId":"generalRule",
            "orderAnalysisStatus":"REFUSED"
         },
         {
            "orderAnalysisId":"57595d2560b2b195a536be01",
            "ruleId":"automaticApproval",
            "orderAnalysisStatus":"REFUSED"
         },
         {
            "orderAnalysisId":"57595d2560b2b195a536be01",
            "ruleId":"emergency",
            "orderAnalysisStatus":"REFUSED"
         },
         {
            "orderAnalysisId":"57595d2560b2b195a536be01",
            "ruleId":"firstBuy",
            "orderAnalysisStatus":"REFUSED"
         },
         {
            "orderAnalysisId":"57595d2560b2b195a536be01",
            "ruleId":"boaVista",
            "orderAnalysisStatus":"MANUAL"
         }
      ],
      "source":{
         "sec":1494425913,
         "ord":1,
         "rs":"rs0",
         "ns":"risk.riskAnalytics",
         "initsync":null,
         "name":"risk",
         "h":7977200064602047920
      },
      "op":"u"
   }
}
```
