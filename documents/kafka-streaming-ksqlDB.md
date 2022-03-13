# Kafka Stream
Originally, Kafka was not designed for streaming processing (modify, transform or analyze data) - It was an event streaming platform. However, this has changed now.
Streaming is processing data events one by one as they arrive in our system. Internet video streaming is just one implementation (vertical). The video player executes actions (play video) or processes execution when each segment (a video segment) of event is fetched to the video player. Another example could be, when a person enters his credit card information on a payment app, we need to process the transaction. Data Analytics team would want to see these events as they are generated. 
Kafka Stream is a client library that lets you speak in streams, not events.

* **New Nouns**: We now have ```tables and streams``` against ```events```.
* **New Verbs**: We will talk about these in Operations sections later.

Kafka streams allow you to **scale** by running multiple copies of your processing application, known as application instances. It also provides you **reliability** with a way to move work between instances as they scale up and scale down or if there is a failure.

## Architecture

Goal of Kafka Streams is to save us all the trouble of setting up producers and consumers and abstract it all away in a compact format which is very each to understand. During the streaming process, the event will undergo a series of transformations (or processes). This chain of operation (or processor) is called **topology**, which is formally defined as acyclic graph of sources, processors and sinks.

### Topology
Comes from Graph theory.

**Topology**, is formally defined as acyclic graph of sources, processors and sinks. In a graph we have nodes and edges. The nodes are represented by what are called **processors** while the edges represent the line between them allowing the messages to go from one processor to another when the previous is finished. It is acyclic because we do not want to process the message over and over again.

The **Consumer** represent a special kind of processor called **source**. It specifies whether the stream will extract the data from in order to process it. On the other side, the **producer** that is placed at the end of the stream processing is called a **sink**. A sink processor will send all the data to the specified location. It is mandatory to have at least one source processor but the number of stream and sink processor may vary.

Each stream processor can perform a specific task, and it can be chained to achieve the desired result. Sometimes, one of these processor may require to know the state it has already achieved so that it can process incrementally. Example, a counter processor need to know what its current count is to increment on the arrival of new message. It does it via **state store**. It can be ephemeral (meaning when application goes down, data stored in it goes down as well) or fault-tolerant by persisting the data in an external data source. The default implementation is a fault-tolerant one by using an internal topic on the Kafka cluster as a storage area.

### Duality of Streams and Tables
In a stream application, we will most likely use a database as well to store the data. That is why, when we are working with streams, we can perceive a stream from two different perspectives. Firstly, stream can be perceived as a stream. When we are talking about streaming, we are actually processing independent events with no connection whatsoever - we store these type of events in topic that uses **delete** as a cleanup policy (delete topic). Secondly, stream can be perceived as a table where we persist only the latest state for some specific information. So, perceiving streams as tables is all about processing evolving events. In Kafka, we are storing these events in **compaction** topics. The best part of having this duality in place is that we can always transform a stream into a table and vice versa. In order to transform a stream into a table, we can perform various operations like ```aggregate()```, ```reduce()``` or ```count()```. To achieve the reverse effect, we would just need to iterate over all the events from the beginning of time and store them as independent events (```toStream()```).

## Stream Processors
There are two categories of processors - 
* Stateless. They do not require state to perform their jobs, and they can process each event independently without worrying about the previous state.
* Stateful. They require a state store in order to perform their operations.

Kafka streams offers a large number of stateless operations.
* Branch: We can use it to split messages into multiple branches based on some business logic.
* Filter: For rejecting messaging based on a condition(s).
* Inverse Filter: Opposite of a filter.
* Map: Can be used for transforming a message from one type to another.
* FlatMap: Useful for transforming one event to one or a multitude of events of the same or different types.
* Foreach: useful to iterate over each event. **This is a terminal process. So, if a Foreach is used, then we will not be able to use a sink processor anymore.**
* Peek: If you would just want to inspect the elements passing through the stream, peek can be used.
* GroupBy: Is super useful when we want to group the event based on some elements like key of the message or an attribute from the value.
* Merge: Which can combine two streams in a single one.

[More stateless Transformations](https://kafka.apache.org/documentation/streams/developer-guide/dsl-api.html#stateless-transformations)

Kafka streams offers a large number of stateful operations.
* Aggregations: Like calculating the sum of all the events posted in a topic.
* Count: Like counting messages with the same key.
* Joins: Joining streams or tables can be very useful when we would like to enhance some messages with information on different topics.
* Windowing: It can work with intervals of time on which we can perform various operations.
* Custom Processor: by using lower-level API.

[More stateful transformations](https://kafka.apache.org/documentation/streams/developer-guide/dsl-api.html#stateful-transformations)

## Windowing
Ability to work within time window. We often need to perform some operations in time boxes.

* Tumbling: We will have non-overlapping 30 mins time window.
* Hopping: Every 10 mins, we will have a window of 30 mins created. This kind of windows is useful while addressing queries like something in last 30 mins.

## Exercise 1: Fraud Detection System
Let's learn the concepts of Kafka stream by implementing Fraud detection system - we will keep it simple and may not reflect the actual rules as implementing in a BFSI system.
**Rules for our Fraud Detection Systems**
* The first rule - Analyzing the order and reject any transaction that does not have a UserId filled in.
* The second rule - Allow only orders with the number of items lower than 1000.
* The third rules - Reject if the total amount is greater than 10000

Payment Service (creating and transmitting transactions) --> Kafka Cluster (**payment** topic)  --> Fraud Detection (Consumer) || Fraud Detection (Producer) --> Kafka Cluster (**validated payment** topic)--> Payment Processor (processing all the transactions).

Fraud Detection Business Layer will execute Rule 1, 2 and 3 on messages from Payment topic.


## ksqlDB
It is under Confluent Community License. It is an event streaming database purpose-built to help developers create stream processing application on top of Apache Kafka. It was formerly known as ksql. Roughly, it is a sql variant of Kafka stream with some custom functions.

| Traditional DB   | ksqlDB                                                                                                                |
|------------------|-----------------------------------------------------------------------------------------------------------------------|
| GUI or CLI       | **Webserver**: HTTPS </br> **CLI**: ksql                                                                              |
| Query Engine     | ksqlDB                                                                                                                |
| Execution Engine | Kafka Streams                                                                                                         |
| Storage Engine   | * for persistent storage, use **Kafka** </br> * for more transient storage say state information, it uses **RocksDB** |

### Why?
```svg
Dev
Friendly
 ^             KSQL                  |
 |      -------------------          |
 |         Kafka Stream API          |
 |      -----------------------      |
 |      Producer and Consumer API    V
                                   Capabilities
```
For simple operations like filtering, KSQL is quite good but for complex aggregations Kafka Streams may be the right solution. The main point is to find the balance between ease of development and flexibility for your application.

### How?
It has a very similar architecture as a traditional database. Everything starts with the Kafka Cluster since it is the core of Streaming platform.The streaming application will be built inside the KSQL server. This server connects to the Kafka Cluster and consumes and produces messages based on the instructions provided by the User. In Non-Prod, the user can use KSQL CLI to interact with KSQL server. The CLI does not necessarily have to be on the same machine as server because the two are using REST APIs to interact with each other. CLI statement would be passed to Server via REST API where the instruction will be parsed, and the streaming engine runs it. Each query represents a different streaming application.

Every query that would run on KSQL server would be parsed to a topology and then run.

```
CREATE STREAM pipeapple_pizza AS   | <- .to("pineapple_pizza")  - Step 4
    SELECT crust, size, topping    | <- .mapValues( pizza -> pizza.getCrust() + ", " + pizza.getSize() + ", " + pizza.getTopping()) - Step 3
    FROM pizza                     | <- .stream("pizza") - Step 1
    WHERE type = 'pineapple';      | <- .filter( pizza -> pizza.getType().equals("pineapple")) - Step 2
```

### When?
* Streaming Operations (Data Analytics, Monitoring, IOT, etc.)
* Viewing data - showing the content of a topic
* Very easily enhance and manipulate the data stored in a topic.

We can combine two data streams, change fields, or even eliminate them.

### Syntax

| Data Definition Language                                                                                                       | Data Manipulation Language                                                                                   |
|--------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------|
| CREATE STREAM </br> CREATE TABLE </br> DROP STREAM </br> DROP TABLE </br> CREATE STREAM AS SELECT </br> CREATE TABLE AS SELECT | SELECT </br> INSERT <br/> UPDATE ? </br> DELETE ? </br> CREATE STREAM AS SELECT </br> CREATE TABLE AS SELECT |

We can create a stream or table from an underlying Kafka topic. While running one of these two statements, DDL will update its internal metadata with no effect whatsoever on any actual topic. Things evolve over time, so some streams or tables may become redundant. Drop are used for the same.

## Installing ksqlDB

### Using Docker Compose
Two images that are required for installing ksqlDB
* ksql CLI
* ksqlDB (web interface or headless mode)

If kafka is not configured, you would need images for
* kafka
* zookeeper
* optionally schema registry

Containers are modular and standardized. They are same sized and are interchangeable. They are isolated from each other. Containers are built on images. Images are templates for creating containers and are built in layers. Then we have Docker which is a container service that manages all the logistics and details of building and running Docker containers. Finally, we have Docker compose which is a tool for running applications that depend on many Docker containers. Docker compose allows us to keep all configuration information in one place, as well as starting all the containers in the right order and taking into consideration different dependencies between them.

### Using Confluent Platform

### Manual Build from GitHub

## Exercise 2: Alerts in Fraud Detection Application
Let's now learn another some concepts using Alerts in Fraud Detection Application.

Topic once registered is visible in KSQL Server. To register a topic into KSQL metadata Stream, we use create table or stream.

```sql
create stream <stream name> 
    with (kafka_topic='topic??', value_format='AVRO');

create table warnings 
as select userId, count(*) 
    from <stream name> 
    window hopping (size 10 minutes, advance by 1 minute) 
    group by userID 
    having count(*) > 1
```