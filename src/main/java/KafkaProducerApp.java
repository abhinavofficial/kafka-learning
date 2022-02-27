import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProducerApp {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092, localhost:9092, localhost:9091");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> myProducer = new KafkaProducer<String, String>(properties);

        try {
            for (int i = 150; i < 300; i++) {
                myProducer.send(new ProducerRecord<String, String>("my_new_topic", Integer.toString(i), "My message" + i));
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            myProducer.close();
        }
    }
}
