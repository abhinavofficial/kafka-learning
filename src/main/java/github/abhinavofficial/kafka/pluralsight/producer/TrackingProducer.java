package github.abhinavofficial.kafka.pluralsight.producer;

import github.abhinavofficial.kafka.pluralsight.model.Event;
import github.abhinavofficial.kafka.pluralsight.model.Product;
import github.abhinavofficial.kafka.pluralsight.model.User;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.Properties;
import static java.lang.Thread.sleep;

public class TrackingProducer {
    public static void main(String[] args) throws InterruptedException {
        EventGenerator eventGenerator = new EventGenerator();
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092, localhost:9093, localhost:9091");
        //properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        properties.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        //Added
        properties.put("schema.registry.url", "http://localhost:8081");

        //KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        KafkaProducer<User, Product> producer = new KafkaProducer<User, Product>(properties);
        
        try {
            for (int i = 0; i < 10; i++) {
                System.out.println("Generating event #" + i);

                Event event = eventGenerator.generateEvent();

                //String key = event.getUser().getUserId().toString();
                //String value = String.format("%s, %s, %s", event.getProduct().getType(), event.getProduct().getColor(), event.getProduct().getDesignType());

                User key = extractKey(event);
                Product value = extractValue(event);

                // New topic user-tracking-avro
                ProducerRecord<User, Product> producerRecord = new ProducerRecord<>("user-tracking-avro", key, value);

                System.out.println("Producing to Kafka the record: " + key + "" + value);
                producer.send(producerRecord);

                sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }

    private static Product extractValue(Event event) {
        return Product.builder().build();
    }

    private static User extractKey(Event event) {
        return User.builder().build();
    }
}
