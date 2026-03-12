import java.util.*;

public class RealTimeAnalyticsDashboard {

    // pageUrl -> total visits
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // pageUrl -> unique users
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // traffic source -> count
    private HashMap<String, Integer> trafficSources = new HashMap<>();


    // Event class
    static class Event {
        String url;
        String userId;
        String source;

        Event(String url, String userId, String source) {
            this.url = url;
            this.userId = userId;
            this.source = source;
        }
    }


    // Process incoming page view
    public void processEvent(Event event) {

        // Update page views
        pageViews.put(event.url, pageViews.getOrDefault(event.url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // Track traffic sources
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }


    // Get Top 10 pages
    public List<String> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        List<String> topPages = new ArrayList<>();

        int count = 0;
        while (!pq.isEmpty() && count < 10) {

            Map.Entry<String, Integer> entry = pq.poll();

            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            topPages.add(url + " - " + views + " views (" + unique + " unique)");
            count++;
        }

        return topPages;
    }


    // Traffic source stats
    public void printTrafficSources() {

        int total = 0;
        for (int count : trafficSources.values())
            total += count;

        System.out.println("Traffic Sources:");

        for (String source : trafficSources.keySet()) {

            int count = trafficSources.get(source);
            double percent = (count * 100.0) / total;

            System.out.println(source + ": " +
                    String.format("%.2f", percent) + "%");
        }
    }


    // Dashboard output
    public void getDashboard() {

        System.out.println("Top Pages:");

        List<String> pages = getTopPages();
        int rank = 1;

        for (String p : pages) {
            System.out.println(rank + ". " + p);
            rank++;
        }

        System.out.println();
        printTrafficSources();
    }


    public static void main(String[] args) {

        RealTimeAnalyticsDashboard dashboard = new RealTimeAnalyticsDashboard();

        dashboard.processEvent(new Event("/article/breaking-news", "user_123", "google"));
        dashboard.processEvent(new Event("/article/breaking-news", "user_456", "facebook"));
        dashboard.processEvent(new Event("/sports/championship", "user_789", "google"));
        dashboard.processEvent(new Event("/sports/championship", "user_999", "direct"));
        dashboard.processEvent(new Event("/article/breaking-news", "user_123", "google"));

        dashboard.getDashboard();
    }
}
