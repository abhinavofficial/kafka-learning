# Serialization and Schema Registry

## Serialization
Per wikipedia, the process of translation **data structures** or **object states** into a format that be **stored** or **transmitted** and reconstructed later, possibly in a **different** computer environment.
A serializer takes the object and convert into a format that can be transferred over network. At the receiving end, a deserializer covert the transferred format into user object again. The transfer can be two-way and different data each way.
There are a couple of considerations while choosing serialization format here
* **If** the serialization format should be **binary** (data is transmitted/stored as bytes or plaintext). Usually binary data format is more compact and hence more efficient.
* **If** we should use **schemas** to enforce strict data structure

| Name     | Binary | Schema - IDL |
|----------|--------|--------------|
| JSON     | No     | No           |
| XML      | No     | Yes          |
| YAML     | No     | No           |
| AVRO     | Yes    | Yes          |
| Protobuf | Yes    | Yes          |
| Thrift   | Yes    | Yes          |

## AVRO
It is a data serialization system, and it has many use cases. It offers rich data structures that can be stored within container files. Applications may use Avro for rpc by using a simple integration with dynamic languages such as Groovy, javascript or even python. It also offers code generation and improved performance on statically typed languages such as C# or Java. Avro uses JSON-based schemas to define data structures which can be either embedded in the container file or can be transferred a separate objects.

Both the object and its schema are passed to Avro serializer which would convert the object into bytes. When required, the bytes are passed on Avro deserializer among the user schema to covert into the object. Schema is .avsc format but the context of it is JSON formatted.

### Schema
```json
#user_schema.avsc
{
  "type": "record",               # Defines a complex type
  "namespace": "com.pluralsight", # Prefix the full name
  "name": "User",                 # Name of the schame
  "fields": [                     # Declaring fields contained by record
    {
        "name": "userId",
        "type": "string"
    }, {
        "name": "username",
        "type": "string"
    }, {
        "name": "dataOfBirth",
        "type": "int",            # number of days from UNIX Epoch (1st Jan 1970) for date
        "logicalType": "date"
    }
  ]
}
```

### AVRO Types

|                                                                                                                                                                                                                                                                          Primitives | Complex                                                                                                                                                |
|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------------------------|
|  null: no value <br/> boolean: a binary value<br/> int: 32-bit signed integer<br/> long:64-bit signed integer<br/> float(32-bit) floating-point number<br/> double(64-bit) floating-point number<br/>bytes: sequence of 8-bit unsigned bytes<br/>string: unicode character sequence | records<br/>enums - {"symbols": ["BLUE","GREEN"]}<br/>arrays<br/>maps - {"values":"long"}<br/>unions - {["null", "string"]}<br/>fixed - {"size": "6"}  |

### AVRO CodeGen
We can use AVRO tool to generate code from the schema
```
java -jar /opt/avro-tools-1.11.0.jar compile schema schemas/user_schema.avsc codegenclasses/
```

## Schema Registry
Our goal is to transfer complex objects without relying on primitive types such string or number. To do that, we will need Schema Registry. **Schema Registry (SR)** is an application that handles the distribution of schemas to producers and consumers and storing them for long-term availability. The schema registry has an interesting solution for persisting the schema by using a Kafka topic to achieve that.

### Subject Name Strategy
Categorizing the schemas based on the topic they belong to. {topic-name}-key: user-tracking-key and {topic-name}-value: user-tracking-value

### How does it really work?
When producer tries to serialize (using AVRO serializer) the message into byte, the serializer would request Schema Registry for the schema for the particular topic key. SR will find it in its own cache and send it. The message can now be serialized, but it would be prepended with a unique identifier represented by the schema ID stored as well in a binary format. The consumer once gets message and recognize the schemaID. In order to get the object back, the deserializer will ask for schema for that schemaID. SR returns the schema and deserialization can take place.

### How is the schema registry done?
An administrator will have to upload them. He uploads the key and the value schemas for a specific topic into Schema Registry. The problem is that SR stores this information in memory. If SR flushes memory for some reason, the information can be lost. To handle this, SR uses an inbuilt producer which transfers the schema in a special topic in the Kafka cluster. Now if the SR crashes, a new instance can read all the schemas from Kakfa cluster using inbuilt consumer and continue its operation.

## Confluent Schema Registry
[Schema Registry Code](https://github.com/confluentinc/schema-registry)

Confluent, under Confluent Community Licensing, has one of the best implementation for AVRO, JSON and Protobuf serde.

```
schema-registry $ bin/schema-registry-start config/schema-registry.properties
```
