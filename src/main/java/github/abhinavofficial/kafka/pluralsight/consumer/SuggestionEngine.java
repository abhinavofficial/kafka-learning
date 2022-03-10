package github.abhinavofficial.kafka.pluralsight.consumer;

import github.abhinavofficial.kafka.pluralsight.model.PreferredProduct;
import github.abhinavofficial.kafka.pluralsight.model.Product;
import github.abhinavofficial.kafka.pluralsight.model.User;
import github.abhinavofficial.kafka.pluralsight.service.UserDB;

import java.util.Arrays;
import java.util.List;

public class SuggestionEngine {

    private final UserDB userDB = new UserDB();

    public void processSuggestions(User user, Product product) {
        //String[] valueSplit = product.split(",");
        String productType = String.valueOf(product.getType());
        String productColor = String.valueOf(product.getColor());
        String productDesign = String.valueOf(product.getDesignType());

        System.out.println("User with user ID:" + String.valueOf(user.getUserId()) +
                "showed interest over " + productType + " " +
                "of color " + productColor + " and design " + productDesign);

        if(user == null) return;

        // Retrieve preferences from the database
        //User user = userDB.findByID(String.valueOf(user.getUserId()));

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
