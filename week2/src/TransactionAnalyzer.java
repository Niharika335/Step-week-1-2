import java.util.*;

public class TransactionAnalyzer {

    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        int time; // minutes from start of day

        Transaction(int id, int amount, String merchant, String account, int time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.time = time;
        }
    }

    List<Transaction> transactions = new ArrayList<>();


    // Add transaction
    public void addTransaction(Transaction t) {
        transactions.add(t);
    }


    // Classic Two Sum
    public void findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction match = map.get(complement);

                System.out.println("Two-Sum Match: (" +
                        match.id + ", " + t.id + ")");
            }

            map.put(t.amount, t);
        }
    }


    // Two Sum within time window (1 hour = 60 min)
    public void findTwoSumTimeWindow(int target, int window) {

        for (int i = 0; i < transactions.size(); i++) {

            Transaction a = transactions.get(i);

            for (int j = i + 1; j < transactions.size(); j++) {

                Transaction b = transactions.get(j);

                if (Math.abs(a.time - b.time) <= window &&
                        a.amount + b.amount == target) {

                    System.out.println("Time Window Match: (" +
                            a.id + ", " + b.id + ")");
                }
            }
        }
    }


    // Duplicate detection
    public void detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                System.out.println("Duplicate Detected: ");

                for (Transaction t : list) {
                    System.out.println("Transaction ID: " + t.id +
                            " Account: " + t.account);
                }
            }
        }
    }


    // K-Sum
    public void findKSum(int k, int target) {
        kSumHelper(0, k, target, new ArrayList<>());
    }

    private void kSumHelper(int start, int k, int target, List<Integer> current) {

        if (k == 0 && target == 0) {
            System.out.println("K-Sum Match: " + current);
            return;
        }

        if (k == 0 || start >= transactions.size())
            return;

        for (int i = start; i < transactions.size(); i++) {

            Transaction t = transactions.get(i);

            current.add(t.id);

            kSumHelper(i + 1, k - 1, target - t.amount, current);

            current.remove(current.size() - 1);
        }
    }


    public static void main(String[] args) {

        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        analyzer.addTransaction(new Transaction(1, 500, "Store A", "acc1", 600));
        analyzer.addTransaction(new Transaction(2, 300, "Store B", "acc2", 615));
        analyzer.addTransaction(new Transaction(3, 200, "Store C", "acc3", 630));
        analyzer.addTransaction(new Transaction(4, 500, "Store A", "acc4", 640));

        System.out.println("Two-Sum target 500:");
        analyzer.findTwoSum(500);

        System.out.println("\nTwo-Sum within 60 min:");
        analyzer.findTwoSumTimeWindow(500, 60);

        System.out.println("\nDuplicate detection:");
        analyzer.detectDuplicates();

        System.out.println("\nK-Sum (k=3, target=1000):");
        analyzer.findKSum(3, 1000);
    }
}
