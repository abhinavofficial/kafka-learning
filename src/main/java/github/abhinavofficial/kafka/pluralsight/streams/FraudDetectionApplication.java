package github.abhinavofficial.kafka.pluralsight.streams;

import github.abhinavofficial.kafka.pluralsight.model.Order;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDe;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.Schema;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Properties;

public class FraudDetectionApplication {
    private static Logger log = LoggerFactory.getLogger(FraudDetectionApplication.class);

    public static void main(String[] args) {
        // Topics:
        // "payment" -> "validated-payment"

        // Message Key: String TransactionID
        // Message Value (order): String userId, Integer noOfItems, Float totalAmount
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "fraud-detection-application");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093, localhost:9093");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        properties.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "localhost:8081");

        // Now build topology
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, Order> stream = streamsBuilder.stream("payments");

        stream.peek(FraudDetectionApplication::printOnEnter)
                .filter((transactionID, order) -> !order.getUserId().toString().equals(""))
                .filter((transactionID, order) -> order.getNoOfItems() < 1000)
                .filter((transactionID, order) -> order.getTotalAmount() < 10000)
                .mapValues((order) -> {
                   order.setUserId(String.valueOf(order.getUserId()).toUpperCase());
                   return order;
                })
                .peek(FraudDetectionApplication::printOnEnter)
                .to("validated-payments");

        Topology topology = streamsBuilder.build();

        KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);
        kafkaStreams.start(); // starts running the application in background thread

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }

    private static void printOnEnter(String transactionID, Order order) {
        log.info("\n******************************");
        log.info("Entering stream transaction with ID < " + transactionID + " >, " +
                "of user < " + order.getUserId() + " >, total amount < " + order.getTotalAmount() +
                " >, and nb of items < " + order.getNoOfItems() + " >");
    }
}
