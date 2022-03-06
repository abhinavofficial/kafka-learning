package github.abhinavofficial.kafka.pluralsight.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;

public class ConsumerAssignExampleApp {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092, localhost:9092, localhost:9091");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer myConsumer = new KafkaConsumer(properties);

        ArrayList<TopicPartition> topics = new ArrayList<TopicPartition>();
        topics.add(new TopicPartition("my_other_topic", 0));
        topics.add(new TopicPartition("my_consumer_topic", 2));
        myConsumer.assign(topics);

        try {
            while (true) {
                ConsumerRecords<String, String> records = myConsumer.poll(Duration.parse("PT10S"));
                records.forEach(rec -> System.out.println(String.format("Topic: %s, Partition: %s, Offset: %s, Key: %s, Value: %s",
                        rec.topic(), rec.partition(), rec.offset(), rec.key(), rec.value()))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            myConsumer.close();
        }
    }
}
