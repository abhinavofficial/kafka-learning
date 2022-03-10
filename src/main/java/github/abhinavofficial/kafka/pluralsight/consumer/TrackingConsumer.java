package github.abhinavofficial.kafka.pluralsight.consumer;

import github.abhinavofficial.kafka.pluralsight.model.Product;
import github.abhinavofficial.kafka.pluralsight.model.User;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Properties;

import static java.util.Arrays.asList;

public class TrackingConsumer {

    public static void main(String[] args) {
        SuggestionEngine suggestionEngine = new SuggestionEngine();

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092, localhost:9093, localhost:9091");
        properties.put("group.id", "user-tracking-consumer"); //Consumer group
        //properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        properties.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        //Added
        properties.put("schema.registry.url", "http://localhost:8081");
        properties.put("specific.avro.ready", "true");

        KafkaConsumer<User, Product> consumer = new KafkaConsumer<User, Product>(properties);
        consumer.subscribe(asList("user-tracking-avro"));

        ConsumerRecords<User, Product> records = consumer.poll(Duration.ofMillis(100));
        while (true) {
            for (ConsumerRecord<User, Product> record : records) {
                suggestionEngine.processSuggestions(record.key(), record.value());
            }
        }
    }

}
