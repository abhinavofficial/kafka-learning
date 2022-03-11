package github.abhinavofficial.kafka.pluralsight.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.protocol.types.Field;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;

public class ConsumerSubscribeExampleApp {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092, localhost:9092, localhost:9091");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "test");

        KafkaConsumer<String, String> myConsumer = new KafkaConsumer<String, String>(properties);
        //subscribe is not incremental
        //myConsumer.subscribe(Arrays.asList("my-new-topic"));
        ArrayList<String> topics = new ArrayList<>();
        topics.add("my_other_topic");
        topics.add("my_consumer_topic");
        myConsumer.subscribe(topics);

        try {
            while (true) {
                ConsumerRecords<String, String> records = myConsumer.poll(Duration.parse("PT10S"));
                records.forEach(rec -> System.out.println(String.format("Topic: %s, Partition: %s, Offset: %s, Key: %s, Value: %s",
                         rec.topic(), rec.partition(), rec.offset(), rec.key(), rec.value().toUpperCase()))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            myConsumer.close();
        }
    }
}
