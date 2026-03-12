import java.util.*;

public class MultiLevelCache {

    static class VideoData {
        String videoId;
        String content;

        VideoData(String videoId, String content) {
            this.videoId = videoId;
            this.content = content;
        }
    }

    // L1 Cache: In-memory with access-order LinkedHashMap
    private LinkedHashMap<String, VideoData> l1Cache;
    private final int L1_CAPACITY = 10000;

    // L2 Cache: SSD-backed simulation (HashMap with access counts)
    private HashMap<String, VideoData> l2Cache;
    private final int L2_CAPACITY = 100000;
    private HashMap<String, Integer> l2AccessCount;

    // L3 Database: simulation
    private HashMap<String, VideoData> database;

    // Statistics
    private int l1Hits = 0, l2Hits = 0, l3Hits = 0, totalRequests = 0;
    private double l1Time = 0, l2Time = 0, l3Time = 0;

    // L2 promotion threshold
    private final int PROMOTION_THRESHOLD = 3;

    public MultiLevelCache() {

        l1Cache = new LinkedHashMap<>(L1_CAPACITY, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > L1_CAPACITY;
            }
        };

        l2Cache = new HashMap<>();
        l2AccessCount = new HashMap<>();

        database = new HashMap<>();
    }

    // Simulate adding video to database
    public void addVideoToDB(String videoId, String content) {
        database.put(videoId, new VideoData(videoId, content));
    }

    // Get video
    public VideoData getVideo(String videoId) {
        totalRequests++;

        // L1 Cache
        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            l1Time += 0.5;
            return l1Cache.get(videoId);
        }

        // L2 Cache
        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            l2Time += 5;

            // Increase access count
            int count = l2AccessCount.getOrDefault(videoId, 0) + 1;
            l2AccessCount.put(videoId, count);

            // Promote to L1 if threshold met
            if (count >= PROMOTION_THRESHOLD) {
                l1Cache.put(videoId, l2Cache.get(videoId));
            }

            return l2Cache.get(videoId);
        }

        // L3 Database
        if (database.containsKey(videoId)) {
            l3Hits++;
            l3Time += 150;

            VideoData video = database.get(videoId);

            // Add to L2
            if (l2Cache.size() >= L2_CAPACITY) {
                // Evict random (simplified)
                String toRemove = l2Cache.keySet().iterator().next();
                l2Cache.remove(toRemove);
                l2AccessCount.remove(toRemove);
            }

            l2Cache.put(videoId, video);
            l2AccessCount.put(videoId, 1);

            return video;
        }

        return null;
    }

    // Get statistics
    public void getStatistics() {

        double l1HitRate = totalRequests == 0 ? 0 : (l1Hits * 100.0 / totalRequests);
        double l2HitRate = totalRequests == 0 ? 0 : (l2Hits * 100.0 / totalRequests);
        double l3HitRate = totalRequests == 0 ? 0 : (l3Hits * 100.0 / totalRequests);

        double overallTime = (l1Time + l2Time + l3Time) / totalRequests;
        System.out.printf("L1: Hit Rate %.2f%%, Avg Time: %.2fms\n", l1HitRate, l1Time / (l1Hits == 0 ? 1 : l1Hits));
        System.out.printf("L2: Hit Rate %.2f%%, Avg Time: %.2fms\n", l2HitRate, l2Time / (l2Hits == 0 ? 1 : l2Hits));
        System.out.printf("L3: Hit Rate %.2f%%, Avg Time: %.2fms\n", l3HitRate, l3Time / (l3Hits == 0 ? 1 : l3Hits));
        System.out.printf("Overall: Hit Rate %.2f%%, Avg Time: %.2fms\n",
                (l1Hits + l2Hits + l3Hits) * 100.0 / totalRequests, overallTime);
    }

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        // Populate database
        for (int i = 1; i <= 5; i++) {
            cache.addVideoToDB("video_" + i, "Content of video " + i);
        }

        // Access videos
        cache.getVideo("video_1"); // L1 miss, L2 miss, L3 hit
        cache.getVideo("video_1"); // L1 miss, L2 hit
        cache.getVideo("video_1"); // L1 promotion
        cache.getVideo("video_1"); // L1 hit
        cache.getVideo("video_2"); // L3 hit
        cache.getVideo("video_3"); // L3 hit

        cache.getStatistics();
    }
}