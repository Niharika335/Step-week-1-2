import java.util.*;

public class SocialMedia {

    // Stores username -> userId
    private HashMap<String, Integer> usernameMap = new HashMap<>();

    // Tracks username attempt frequency
    private HashMap<String, Integer> attemptCount = new HashMap<>();

    // Constructor with some existing users
    public SocialMedia() {
        usernameMap.put("john_doe", 1);
        usernameMap.put("admin", 2);
        usernameMap.put("alex", 3);
    }

    // Check username availability
    public boolean checkAvailability(String username) {

        // Track attempts
        attemptCount.put(username, attemptCount.getOrDefault(username, 0) + 1);

        return !usernameMap.containsKey(username);
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!usernameMap.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        // Replace underscore with dot suggestion
        if (username.contains("_")) {
            String dotVersion = username.replace("_", ".");
            if (!usernameMap.containsKey(dotVersion)) {
                suggestions.add(dotVersion);
            }
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String mostAttempted = "";
        int max = 0;

        for (String username : attemptCount.keySet()) {
            int count = attemptCount.get(username);

            if (count > max) {
                max = count;
                mostAttempted = username;
            }
        }

        return mostAttempted + " (" + max + " attempts)";
    }

    // Test the system
    public static void main(String[] args) {

        SocialMedia sm = new SocialMedia();

        System.out.println("john_doe available: " + sm.checkAvailability("john_doe"));
        System.out.println("jane_smith available: " + sm.checkAvailability("jane_smith"));

        System.out.println("Suggestions for john_doe: " + sm.suggestAlternatives("john_doe"));

        sm.checkAvailability("admin");
        sm.checkAvailability("admin");
        sm.checkAvailability("admin");

        System.out.println("Most attempted username: " + sm.getMostAttempted());
    }
}