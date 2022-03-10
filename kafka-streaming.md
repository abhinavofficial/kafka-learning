# Kafka Stream

Streaming is processing data events one by one as they arrive in our system. Internet video streaming is just one implementation (vertical). The video player executes actions (play video) or processes execution when each segment (a video segment) of event is fetched to the video player. Another example could be, when a person enters his credit card information on a payment app, we need to process the transaction. Data Analytics team would want to see these events as they are generated. Let's learn the concepts of Kafka stream by implementing Fraud detection system - we will keep it simple and may not reflect the actual rules as implementing in a BFSI system.

**Rules for our Fraud Detection Systems**
* The first rule - Analyzing the order and reject any transaction that does not have a UserId filled in.
* The second rule - Allow only orders with the number of items lower than 1000.
* The third rules - Reject if the total amount is greater than 10000

## Architecture

Payment Service (creating and transmitting transactions) --> Kafka Cluster (**payment** topic)  --> Fraud Detection (Consumer) || Fraud Detection (Producer) --> Kafka Cluster (**validated payment** topic)--> Payment Processor (processing all the transactions).

Fraud Detection Business Layer will execute Rule 1, 2 and 3 on messages from Payment topic.

Goal of Kafka Streams is to save us all the trouble of setting up producers and consumers and abstract it all away in a compact format which is very each to understand. During the streaming process, the event will undergo a series of transformations. This change of operation is called **topology**, which is formally defined as acyclic graph of sources, processors and sinks.

### Topology
**Topology**, is formally defined as acyclic graph of sources, processors and sinks. In a graph we have nodes and edges. The nodes are represented by what are called **processors** while the edges represent the line between them allowing the messages to go from one processor to another when the previous is finished. It is acyclic because we do not want to process the message over and over again.

The **Consumer** represent a special kind of processor called **source**. It specifies whether the stream will extract the data from in order to process it. On the other side, the **producer** that is placed at the end of the stream processing is called a **sink**. A sink processor will send all the data to the specified location. It is mandatory to have at least one source processor but the number of stream and sink processor may vary.

Each stream processor can perform a specific task, and it can be chained to achieve the desired result. Sometimes, one of these processor may require to know the state it has already achieved so that it can process incrementally. Example, a counter processor need to know what its current count is to increment on the arrival of new message. It does it via **state store**. It can be ephemeral (meaning when application goes down, data stored in it goes down as well) or fault-tolerant by persisting the data in an external data source. The default implementation is a fault-tolerant one by using an internal topic on the Kafka cluster as a storage area.

## Duality of Streams
In a stream application, we will most likely use a database as well to store the data. That is why, when we are working with streams, we can perceive a stream from two different perspectives. Firstly, stream can be perceived as a stream. When we are talking about streaming, we are actually processing independent events with no connection whatsoever - we store these type of events in topic that uses **delete** as a cleanup policy (delete topic). Secondly, stream can be perceived as a table where we persist only the latest state for some specific information. So, perceiving streams as tables is all about processing evolving events. In Kafka, we are storing these events in **compaction** topics. The best part of having this duality in place is that we can always transform a stream into a table and vice versa. In order to transform a stream into a table, we can perform various operations like ```aggregate()```, ```reduce()``` or ```count()```. To achieve the reverse effect, we would just need to iterate over all the events from the beginning of time and store them as independent events (```toStream()```).

## Stream Processors
There are two categories of processors - 
* Stateless. They do not require state to perform their jobs and they can process each event independently without worrying about the previous state.
* Stateful. 