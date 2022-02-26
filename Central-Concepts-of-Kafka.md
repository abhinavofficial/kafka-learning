# Central Concepts - Topics, Partitions and Brokers

## Topic
Central Kafka abstraction. It is just a named feed or category of messages. Think of this as an inbox, a location where producers produce a message to and consumers consume the message from. In Kafka, it is a logical entity - something that spans across the entire cluster. Consumers and Producers hence do not care about where it is actually located. However, behind the scene, Kafka cluster is maintain one or more physical log files per topic.
When a producer produces a messages, it is put in ordered sequences (by time). Each message represents immutatable facts as events. This style of maintaining data as event is called **Event Sourcing**. It is an architectural style or approach to maintaining an application's state by capturing all changes as a sequence of time-ordered, immutable events.

### Message Content
Each message has a -
* timestamp - time when the broker received the message.
* Referenceable identifier - the message received gets a unique identifier.
* Payload (binary) - the payload of data which is what the producers and consumers really care about.
