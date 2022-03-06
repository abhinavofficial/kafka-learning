package github.abhinavofficial.kafka.pluralsight.consumer;

import github.abhinavofficial.kafka.pluralsight.model.PreferredProduct;
import github.abhinavofficial.kafka.pluralsight.model.User;
import github.abhinavofficial.kafka.pluralsight.service.UserDB;

import java.util.Arrays;
import java.util.List;

public class SuggestionEngine {

    private final UserDB userDB = new UserDB();

    public void processSuggestions(String userId, String product) {
        String[] valueSplit = product.split(",");
        String productType = valueSplit[0];
        String productColor = valueSplit[1];
        String productDesign = valueSplit[2];

        System.out.println("User with user ID:" + userId +
                "showed interest over " + productType + " " +
                "of color " + productColor + " and design " + productDesign);

        // Retrieve preferences from the database
        User user = userDB.findByID(userId);

        // Update user preferences
        user.getPreferences().add(new PreferredProduct(productColor, productType, productType));

        // Generate list of suggestions
        user.setSuggestions(generateSuggestion(user.getPreferences()));

        // Store suggestions in the database
        userDB.save(user);
    }

    private List<String> generateSuggestion(List<PreferredProduct> preferences) {
        return Arrays.asList("T-SHIRT, BLUE", "DESIGN, ORANGE, ROCKET", "T-SHIRT, PURPLE, CAR");
    }
}
