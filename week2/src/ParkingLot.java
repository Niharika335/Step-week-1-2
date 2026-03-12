import java.util.*;

public class ParkingLot {

    static class ParkingSpot {
        String licensePlate;
        long entryTime;
        Status status;

        ParkingSpot() {
            status = Status.EMPTY;
        }
    }

    enum Status {
        EMPTY, OCCUPIED, DELETED
    }

    private ParkingSpot[] table;
    private int capacity = 500;
    private int occupied = 0;
    private int totalProbes = 0;

    public ParkingLot() {
        table = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
    }

    // Hash function
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // Park vehicle using linear probing
    public void parkVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (table[index].status == Status.OCCUPIED) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = licensePlate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = Status.OCCUPIED;

        occupied++;
        totalProbes += probes;

        System.out.println("Assigned spot #" + index + " (" + probes + " probes)");
    }

    // Vehicle exit
    public void exitVehicle(String licensePlate) {

        int index = hash(licensePlate);

        while (table[index].status != Status.EMPTY) {

            if (table[index].status == Status.OCCUPIED &&
                    table[index].licensePlate.equals(licensePlate)) {

                long durationMillis =
                        System.currentTimeMillis() - table[index].entryTime;

                double hours = durationMillis / (1000.0 * 60 * 60);
                double fee = hours * 5.5; // $5.5 per hour

                table[index].status = Status.DELETED;
                occupied--;

                System.out.println("Spot #" + index + " freed");
                System.out.printf("Duration: %.2f hours\n", hours);
                System.out.printf("Fee: $%.2f\n", fee);
                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found");
    }

    // Find nearest available spot
    public int findNearestSpot() {

        for (int i = 0; i < capacity; i++) {
            if (table[i].status != Status.OCCUPIED)
                return i;
        }

        return -1;
    }

    // Parking statistics
    public void getStatistics() {

        double occupancyRate = (occupied * 100.0) / capacity;

        double avgProbes = occupied == 0 ? 0 :
                (double) totalProbes / occupied;

        System.out.println("Occupancy: " + occupancyRate + "%");
        System.out.println("Avg Probes: " + avgProbes);
        System.out.println("Peak Hour: 2-3 PM (example)");
    }

    public static void main(String[] args) throws InterruptedException {

        ParkingLot lot = new ParkingLot();

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        Thread.sleep(2000); // simulate parking time

        lot.exitVehicle("ABC-1234");

        System.out.println("Nearest spot: #" + lot.findNearestSpot());

        lot.getStatistics();
    }
}