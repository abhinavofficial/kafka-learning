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
Topic as a logical entity is represented by one of more physical log file, called partition.
