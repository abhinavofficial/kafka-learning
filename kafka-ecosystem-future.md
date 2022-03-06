# Kafka Ecosystem

## Primary Use Cases for Apache Kafka
* Connecting disparate sources of data
* Large-scale data movement pipelines
* Big Data integration with Hadoop and Spark

## Challenges Remain

### Governance and Data Evolution
With divergent producer, there is an immense pressure on consumer to understand the change. Kafka does not have a common means of cataloging, registering and reconciling the disparate message specification and compatibility mapping between serializing producer and deserializing consumer.
Confluent contributed to Kafka Schema Registry - Apache Avro serialization format. With Avro, producer can serialize their messages in Avro version and self describing format and expect them to deserialize seamlessly by the consumer. As the name suggests, the schema used by both producers and consumers can be registered and version managed centrally within the Kafka Cluster environment allowing for easy RESTful service based discovery and version compatibility reconciliation.

### Consistency and Productivity
Kafka does not have a common framework integrating data sources and targets. It was always left to individual engineers to create their own solutions using Generic Consumer and Producer Client APIs.
With 0.10.0 release of Apache Kafka, a new framework was introduced Apache Kafka Connect and Connector Hub.

### Big and Fast Data
Predictive Deep Learning, Machine Learning, Predictive Analytics

With 0.10.0, Kafka Streams, a client library leverages existing Kafka machinery. It can be a single infrastructure solution at least for streaming-based processing. It is embeddable within existing applications.

## Next Steps
Course on **Kafka Schema Registry**, **Kafka Connect and Connector Hub** and **Kafka Streams**