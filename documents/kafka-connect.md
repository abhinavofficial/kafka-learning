#Kafka Connect

## Source
[Kafka Connect Fundamentals](https://app.pluralsight.com/library/courses/kafka-connect-fundamentals/table-of-contents)

* Big data: A field that treats ways to deal with data sets that are too large or complex to be dealt with traditional processing applications.
* Fast data: A field that treats ways to deal with streaming data (data in motion) by allowing **instant** processing as it arrives in the system.

## Why?
The first reason for choosing Kafka connect is code re-usability - with 0 code and just configuration files.

Document Based DB - MongoDB
Graph DB - Neo4J
Search engines - Elasticsearch or Solr
Blob storage - S3, 
Social Media Interfaces - Twitter

Kafka connect is a suitable tool for transferring the data for both to and from Kafka.

The second reason is that you are also gaining benefit by not re-inventing the wheel. Kafka connect keep receiving the data and keeps it in its buffer pool. When the buffer is full, the connection to the database is opened. This micro-batching helps reduce the overhead significantly.

The third reasons - Scalability. Kafka connect solves this problem by having two modes that you can run it on. The first mode is standalone (a single instance of Kafka connect) - recommended for development mode. The second mode is distributed mode (multiple instances on separate machines).

## Architecture
Kafka connect is represented on the application level by a worker. A worker is nothing more than a process running on the operating system which is responsible for executing different tasks. 

In a standalone mode, there is just one work who is going the run the process on its own. In a distributed mode (multiple VMs are made available), a different worker will run on different machines. The interesting fact about this distributed configuration is that the workers are using Kafka to synchronize one with others - this means if one worker goes down, the workload is shared across other workers. The problem with workers is that they are meaningless. They are just some processes waiting to do some job. By starting up a worker, we haven't really achieved anything of value. In order to give meaning to them, we added connectors. Each application has its own way of connecting and transferring data. So, for each time of application, we will need a separate connector. Connector though is only a blueprint on how we can connect to a specific application. Then we need a task which actually defines the behavior (putting the message from a specific topic to specific table in a database). A task is a runtime behavior of a task. For each connector we can have multiple tasks with a different configuration.

## Connectors, Converters, Transforms
If the data is transferred from another application to Kafka, then we need to use a so-called **source connector**. On the other hand, if you transfer some data from Kafka, we would need a **sink connector**. 

DB --> JDBC Source Connector --> Kafka Broker
Kafka Broker --> AWS S3 sink Connector --> AWS S3

One of things that a task does is - converters. String Converter, for example which will transform it into from Kafka Connect Internal format to the format that will likely store it in the topic. There are other converters like JSON, AVRO or even Protobuf converters.

Kafka connect can also do some processing, but, first the message is ingested by the source connector, it is then converted into Kafka internal formatting. And then we can use the transforms to do some processing over it - full name of this feature is SMT **simple message transforms**. 

## Implementing Kafka Connect

* **Install confluent-hub-client:** /opt/confluent-hub-client. Added /opt/confluent-hub-client/bin in PATH.

* **Install kafka-connect-jdbc:** confluent-hub install confluentinc/kafka-connect-jdbc:10.3.3 --component-dir ~/kafka-connectors --working-configs /opt/confluent/etc/kafka/connect-standalone.properties 

* **Install myssql-driver**
mysql-driver and kafka-connect-jdbc are mentioned in connect-standalone.properties under plugin path.

* create mysql-connector.properties

Now start connect standalone
```
bin/connect-standalone etc/kafka/connect-standalone.properties etc/kafka/mysql-connector.properties
``` 

Now produce message
```
bin/kafka-console-producer --broker-list localhost:9092 --topic orders
> {"schema": {"type": "struct","optional": false,"name": "orders","fields": [{"type": "int64","optional": false,"field": "nb_items"}, {"type": "int64","optional": false,"field": "total_amount"}, {"type": "string","optional": false,"field": "user_id"}]},"payload": {"nb_items": 2,"total_amount": 140,"user_id": "ABC123"} } 
```

Note that we have put the schema and payloads since we resorted to ```JsonConverter```