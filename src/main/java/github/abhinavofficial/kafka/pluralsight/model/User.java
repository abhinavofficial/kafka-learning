package github.abhinavofficial.kafka.pluralsight.model;

import github.abhinavofficial.kafka.pluralsight.enums.UserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UserId userId;
    private String userName;
    private Date dataOfBirth;
    private List<PreferredProduct> preferences;
    private List<String> suggestions;

    public User(UserId userId) {
        this.userId = userId;
        this.preferences = new ArrayList<>();
        this.suggestions = new ArrayList<>();
    }
}
