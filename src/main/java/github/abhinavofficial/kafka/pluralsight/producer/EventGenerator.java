package github.abhinavofficial.kafka.pluralsight.producer;

import com.github.javafaker.Faker;
import github.abhinavofficial.kafka.pluralsight.enums.Color;
import github.abhinavofficial.kafka.pluralsight.enums.DesignType;
import github.abhinavofficial.kafka.pluralsight.enums.ProductType;
import github.abhinavofficial.kafka.pluralsight.enums.UserId;
import github.abhinavofficial.kafka.pluralsight.model.Event;
import github.abhinavofficial.kafka.pluralsight.model.Product;
import github.abhinavofficial.kafka.pluralsight.model.User;

public class EventGenerator {
    private  Faker faker = new Faker();

    public Event generateEvent() {
        return Event.builder()
                .user(generateRandomUser())
                .product(generateRandomObject())
                .build();
    }

    private User generateRandomUser() {
        return User.builder()
                .userId(faker.options().option(UserId.class))
                .userName(faker.name().lastName())
                .dataOfBirth(faker.date().birthday())
                .build();
    }

    private Product generateRandomObject() {
        return Product.builder()
                .color(faker.options().option(Color.class))
                .type(faker.options().option(ProductType.class))
                .designType(faker.options().option(DesignType.class))
                .build();
    }
}
