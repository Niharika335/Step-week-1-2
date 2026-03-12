import java.util.*;

public class DNSCache {

    // Entry class
    class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, int ttl) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttl * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private int capacity = 5;

    // LRU Cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache =
            new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                    return size() > capacity;
                }
            };

    private int hits = 0;
    private int misses = 0;

    // Resolve domain
    public synchronized String resolve(String domain) {

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                System.out.println("Cache HIT → " + entry.ipAddress);
                return entry.ipAddress;
            } else {
                cache.remove(domain);
                System.out.println("Cache EXPIRED");
            }
        }

        // Cache miss
        misses++;

        String ip = queryUpstreamDNS(domain);

        DNSEntry newEntry = new DNSEntry(domain, ip, 5); // TTL = 5 seconds
        cache.put(domain, newEntry);

        System.out.println("Cache MISS → Query upstream → " + ip);
        return ip;
    }

    // Simulate upstream DNS query
    private String queryUpstreamDNS(String domain) {
        Random rand = new Random();
        return "172.217.14." + rand.nextInt(255);
    }

    // Cache statistics
    public void getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);

        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) throws InterruptedException {

        DNSCache dns = new DNSCache();

        dns.resolve("google.com"); // MISS
        dns.resolve("google.com"); // HIT

        Thread.sleep(6000); // wait for TTL expiration

        dns.resolve("google.com"); // EXPIRED → MISS

        dns.getCacheStats();
    }
}
