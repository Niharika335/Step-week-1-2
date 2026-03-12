import java.util.*;

public class AutocompleteSystem {

    // Trie Node
    static class TrieNode {
        HashMap<Character, TrieNode> children = new HashMap<>();
        HashMap<String, Integer> queries = new HashMap<>();
    }

    private TrieNode root = new TrieNode();

    // Global frequency map
    private HashMap<String, Integer> frequencyMap = new HashMap<>();


    // Insert query
    public void insertQuery(String query, int freq) {

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + freq);

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.queries.put(query, frequencyMap.get(query));
        }
    }


    // Search suggestions
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c))
                return new ArrayList<>();

            node = node.children.get(c);
        }

        // Min heap for Top 10
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());

        for (Map.Entry<String, Integer> entry : node.queries.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10)
                pq.poll();
        }

        // Build result
        List<String> result = new ArrayList<>();

        while (!pq.isEmpty()) {
            Map.Entry<String, Integer> entry = pq.poll();
            result.add(entry.getKey() + " (" + entry.getValue() + ")");
        }

        Collections.reverse(result);

        return result;
    }


    // Update frequency
    public void updateFrequency(String query) {
        insertQuery(query, 1);
    }


    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.insertQuery("java tutorial", 1234567);
        system.insertQuery("javascript", 987654);
        system.insertQuery("java download", 456789);
        system.insertQuery("java 21 features", 1);

        System.out.println("Suggestions for 'jav':");

        List<String> suggestions = system.search("jav");

        int rank = 1;
        for (String s : suggestions) {
            System.out.println(rank + ". " + s);
            rank++;
        }

        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println("\nFrequency updated for 'java 21 features'");
    }
}