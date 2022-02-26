# Central Concepts - Topics, Partitions and Brokers

## Topic
Central Kafka abstraction. It is just a named feed or category of messages. Think of this as an inbox, a location where producers produce a message to and consumers consume the message from. In Kafka, it is a logical entity - something that spans across the entire cluster. Consumers and Producers hence do not care about where it is actually located. However, behind the scene, Kafka cluster is maintain one or more physical log files per topic.
When a producer produces a messages, it is put in ordered sequences (by time). Each message represents immutatable facts as events. This style of maintaining data as event is called **Event Sourcing**. It is an architectural style or approach to maintaining an application's state by capturing all changes as a sequence of time-ordered, immutable events.

### Message Content
Each message has a -
* timestamp - time when the broker received the message.
* Referenceable identifier - the message received gets a unique identifier.
* Payload (binary) - the payload of data which is what the producers and consumers really care about.

### Message Offset
This is a very important concept which allows consumers to read the message at their own pace reliably. Offset is a placeholder like a bookmark that maintains last read message position. It is maintained by the Kafka consumer. It corresponds to the message identifier. From beginning, is therefore nothing but ```offset: 0```. A connected consumer can be notified on arrival of a new message as an event.

### Message Retention Policy
Apache Kafka retains all published message regardless of consumption up to (in hours) as defined by message retention policy set up in configuration file. By default, it is 168hours or seven days. Post that older message would fall off to accommodate for new ones.
Retention period is defined on a per-topic basis.
Physical storage resources can contrain message retention.

## Transaction or Commit Logs
It works similar to database. Source of truth, physically stored and maintained, higher-order data structures derive from the log (tables, indexes, views, etc). It also serves as point of recovery and forms the basis of replication and distribution. At one point in time, here is how Kafka was positioning itself -
**Apache Kafka is a publish-subscribe messaging rethought as a distributed commit log.**

## Partitions
Topic as a logical entity is represented by one of more physical log file, called partition. The number of partition in a topic is configurable. A partition is the basis for which Kafka can scale out tremendously, become fault-tolerant and achieve higher level of throughput. As such, each partition is maintained on at least one or more Brokers.
Each topic has at least one partition as you can obviously realize and save the **log file along with its index file** at /tmp/kafka-logs/{topic}-{partition}. The constraint you have to work around paritition is that **each partition MUST fit entirely on one machine**.
In general, the scalability of Apache Kafka is determined by the number of partitions being managed by multiple broker nodes. The partition itself is managed by a partitioning scheme that is managed by the producer. If producer does not specify anything specific, then round-robin is invoked.

### Partitioning Trade-offs
* The more partitions the greater the zookeeper overhead. With large partition numbers, ensure proper ZK capacity. 
* Message ordering can become complex. There is no global messaging order. You can have a single partition for global order but with other understood issues. Or, you can implement a smart Consumer-handling for ordering.
* When the number of partitions are very high, the leader fail-over time can become time consuming. Typical failover time is few ms but in large cluster, these can add up. Hence, sometimes large organizations have many separate kafka cluster.

## Fault-Tolerance
Any system is bound to fail over period of time. Kafka can have Broker failure, Network failure, Disk failure and other. Zookeeper has the capability to repoint workload on any failed broker to another one. But the data may be lost sitting on that broker. To handle this, we have one more critical setting, called **Replication-factor**. This is a critical safeguard to ensure reliable work distribution. This ensures that messages are stored redundantly which make the cluster more resilient and fault-tolerance - all for the purpose of mitigating data loss.
By setting the replication-factor to N, you have guareenteed to N-1 broker failure tolerance. A minimum of 2-3 is recommended.
Replication factor can be configured per-topic basis.


https://kafka.apache.org/protocol.html
https://www.confluent.io/blog/getting-started-with-rust-and-kafka/
https://www.confluent.io/blog/kafka-scala-tutorial-for-beginners/
https://github.com/apache/kafka
https://thenewstack.io/building-a-simple-pure-rust-async-apache-kafka-client/
https://github.com/kafka-rust/kafka-rust

