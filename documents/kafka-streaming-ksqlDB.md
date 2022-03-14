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
State is information that needs to be persisted between tasks.

There are two categories of processors based on their association with state - 
* Stateless. They do not require state to perform their jobs, and they can process each event independently without worrying about the previous state. Stateless process do work as it comes in and then forget it.
* Stateful. They require a state store (like RocksDB, a persistent key-value store for high performance storage environment) in order to perform their operations. When state need to be managed, recovery and scaling out being more difficult.

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
* Tables: 
* Custom Processor: by using lower-level API.

[More stateful transformations](https://kafka.apache.org/documentation/streams/developer-guide/dsl-api.html#stateful-transformations)

## Managing time: Windowing

Before we jump into windowing, let's understand the three types of timestamps we encounter in Kafka.
* First is when the event is born or created, **event time**.
* Second is when the event is ingested in Kafka, **ingestion time**
* Finally, we have **processing time**. This is when Kafka stream or ksqlDB processes the data and usually, we want this to be as close as possible to the ingestion time.

Windowing is the process of grouping records based on time and provides us ability to work within time window. We often need to perform some operations in time boxes.

* Tumbling: We will have non-overlapping 30 mins time window.
* Hopping: Every 10 mins (hop), we will have a window of 30 mins created. This kind of windows is useful while addressing queries like something in last 30 mins.
* Session: We define a period/duration of inactivity or lack of events. The events between these matching inactivity are grouped together into dynamically sized windows.
* Sliding: Sliding window only applies to **stream-to-stream joins**. Joined events have to be somewhat close in time. Example, when you have an issue with application 1, and you expect the impact on application 2, it can be established with joined using sliding window.

While dealing with windows, the follow concepts become important -
**Grace Period** - the period of time a system will wait for late or out of order events.
**Retention Period** - the period of time a system will keep, or retain data. This value should be greater than length of window, and the grace period combined.

## Consistency Management
Issues - Read Never and Read Multiple

Ideal - Read ONCE and ONLY ONCE.

We discussed this topic while learning about [Consumer](kafka-consumer.md) briefly. Lets discuss it again - this is really a problem of any distributed streaming system.

### Avoiding Never Read scenario
Ideally we want some sort of state store that can be migrated or reassigned to another process if one of them fails. In Kafka streams, you have granular control over what type of state store you use such as RocksDB. But in addition to that, we need a way to rebuild that state store if replication of copies of the state is not already enabled. For streams and ksqlDB this means reading from a compacted change log topic that tracks all the state changes for a long as they are relevant. Additionally, as part of recovery process, the work needs to be assigned to that other processor, so it can finish it. ksqlDB and Kafka streams both handle this for you automatically.

### Avoiding multiple reads
Let's understand one concept before diving into the solution.
**Idempotent**: A mathematical operation that produces the same result if repeated. In computer science, this would translate to **an action that is safe to repeat**. In Stream world it would mean - If your system receives the same event multiple times, it can be handled properly.

This can be handled through duplicate detection, unique IDs, or sequence IDs, depending on the scenario.

We can actually choose which of these scenarios we want ksqlDB or Kafka streams to operate in. We just need to confgure **Processing Guarantee setting**. We touched upon this in previous learning on producer and consumer. Specifically the two options are called **exactly-once** and **at-least-once** (default). **exactly-once** semantics comes with small performance and storage cost - your processor, in this case in ksqlDB or Kafka streams, needs to be set as idempotent. It means, Kafka knows how to get rid of any duplicate events that have been produced multiple times by using a sequence number that's added to the event, similar to how TCP works for the TCP protocol. This would increase the size of your events by a small amount since we are adding information. Additionally, Kafka Streams and ksqlDB have to use **Kafka Transaction API** in order to track changes to state, track changes to the topic offset and output the transformative events atomically. Atomically means they happen all at once or not at all.

## Exercise 1: Fraud Detection System
Let's learn the concepts of Kafka stream by implementing Fraud detection system - we will keep it simple and may not reflect the actual rules as implementing in a BFSI system.
**Rules for our Fraud Detection Systems**
* The first rule - Analyzing the order and reject any transaction that does not have a UserId filled in.
* The second rule - Allow only orders with the number of items lower than 1000.
* The third rules - Reject if the total amount is greater than 10000

Payment Service (creating and transmitting transactions) --> Kafka Cluster (**payment** topic)  --> Fraud Detection (Consumer) || Fraud Detection (Producer) --> Kafka Cluster (**validated payment** topic)--> Payment Processor (processing all the transactions).

Fraud Detection Business Layer will execute Rule 1, 2 and 3 on messages from Payment topic.

Streaming is implemented in a SQL way using [ksqlDB](ksqlDB.md)