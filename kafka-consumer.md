# Kafka Consumer

## Architecture

![Kafka Consumer Internals](images/kafka-consumer-internals.png)

## Objects

Three properties to be created:
1. Bootstrap servers: Cluster membership: partition leaders, etc.
2. Key Serializer: Classes used for message serialization and deserialization
3. Value Serializer: Classes used for message serialization and deserialization

```java
class ProducerApp {
    java.util.Properties props = new java.util.Properties();
    props.put("bootstrap.servers","BROKER-1:9092, BROKER-2:9093");
    props.put("key.serializer","org.apache.kafka.common.serializer.StringDeserializer");
    props.put("value.serializer","org.apache.kafka.common.serializer.StringDeserializer");
}
```

## Subscription
It can be done by calling ```subscribe()``` and passing it a list of topics. For these topics, you are essentially requesting for dynamic or automatic partition assignment. This is in essence say that you are asking consumer to pull from 
* One topic, one or many partitions
* Many topics, many more partitions

Beside subscribe, there is another option - subscribing to particular partition(s). This is done through ```assign()``` which is used to subscribe to a list of ```TopicPartitions``` as below
```java
TopicPartition partition0 = new TopicPartition("myTopic", 0);
ArrayList<TopicPartition> partitions = new ArrayList<TopicPartition>();
partitions.add(partition0);
myConsumer.assign(partitions);
```

There is key difference between subscribe and assign method. By asking for specific partition, you are taking the responsibility of assigning yourself for those partitions. Once assigned these topic partitions, consumer will then start pulling the message from those partitions, regardless of topic those partitions are part of. In general, these assignments are managed for us by Kafka. As such, assign is an advanced case (Manual, self-administering mode) and must be worked carefully.

### Single Consumer Topic Subscriptions
Partition management is auto managed.

### Single Consumer Partition Assignments
If a new partition is added, it does not care.

## The Poll Loop
Primary function of the Kafka Consumer
* Poll - continuously polling the brokers for data
Single API for handling all Consumer-Broker interactions
* A lot of interactions beyond message retrieval
```java
// Set the topic subscription or partition assignments
try {
    while (true) {
        ConsumerRecords<String, String> record = myConsumer.poll(100);
        //Processing logic here
        }
    final {
        myConsumer.close()
        }
}
```