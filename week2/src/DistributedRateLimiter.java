import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedRateLimiter {

    // Token Bucket class
    static class TokenBucket {

        int maxTokens;
        double refillRate; // tokens per second
        double tokens;
        long lastRefillTime;

        public TokenBucket(int maxTokens, double refillRate) {
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // Refill tokens based on elapsed time
        private void refill() {

            long now = System.currentTimeMillis();
            double seconds = (now - lastRefillTime) / 1000.0;

            double tokensToAdd = seconds * refillRate;

            tokens = Math.min(maxTokens, tokens + tokensToAdd);
            lastRefillTime = now;
        }

        // Try to consume a token
        public synchronized boolean allowRequest() {

            refill();

            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }

            return false;
        }

        public int remainingTokens() {
            return (int) tokens;
        }
    }

    // clientId -> token bucket
    private ConcurrentHashMap<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS = 1000;
    private static final int WINDOW_SECONDS = 3600;

    private double refillRate = MAX_REQUESTS / (double) WINDOW_SECONDS;


    // Rate limit check
    public String checkRateLimit(String clientId) {

        clientBuckets.putIfAbsent(clientId,
                new TokenBucket(MAX_REQUESTS, refillRate));

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket.allowRequest()) {

            return "Allowed (" + bucket.remainingTokens() + " requests remaining)";
        } else {

            return "Denied (0 requests remaining)";
        }
    }


    // Get client status
    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) {
            System.out.println("No requests yet");
            return;
        }

        int used = MAX_REQUESTS - bucket.remainingTokens();

        System.out.println("{used: " + used +
                ", limit: " + MAX_REQUESTS +
                ", remaining: " + bucket.remainingTokens() + "}");
    }


    public static void main(String[] args) {

        DistributedRateLimiter limiter = new DistributedRateLimiter();

        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));

        limiter.getRateLimitStatus("abc123");
    }
}