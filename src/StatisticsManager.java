import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class StatisticsManager {
    private AtomicInteger totalClients = new AtomicInteger(0);
    private AtomicInteger totalOperations = new AtomicInteger(0);
    private Map<String, AtomicInteger> operationCounts = new ConcurrentHashMap<>();
    private AtomicInteger errorOperations = new AtomicInteger(0);
    private AtomicInteger resultSum = new AtomicInteger(0);

    private Map<String, AtomicInteger> recentOperationCounts = new ConcurrentHashMap<>();
    private AtomicInteger recentOperations = new AtomicInteger(0);
    private AtomicInteger recentErrorOperations = new AtomicInteger(0);
    private AtomicInteger recentResultSum = new AtomicInteger(0);

    public StatisticsManager() {
        for (String op : Arrays.asList("ADD", "SUB", "MUL", "DIV")) {
            operationCounts.put(op, new AtomicInteger(0));
            recentOperationCounts.put(op, new AtomicInteger(0));
        }
    }

    public void recordClientConnection() {
        totalClients.incrementAndGet();
    }

    public void recordOperation(String operation, int result) {
        totalOperations.incrementAndGet();
        operationCounts.get(operation).incrementAndGet();
        resultSum.addAndGet(result);

        recentOperations.incrementAndGet();
        recentOperationCounts.get(operation).incrementAndGet();
        recentResultSum.addAndGet(result);
    }

    public void recordErrorOperation() {
        errorOperations.incrementAndGet();
        recentErrorOperations.incrementAndGet();
    }

    public void printStatistics() {
        System.out.println("\n--- Statystyki całkowite ---");
        System.out.println("Liczba klientów: " + totalClients.get());
        System.out.println("Całkowita liczba operacji: " + totalOperations.get());
        System.out.println("Liczba błędnych operacji: " + errorOperations.get());
        System.out.println("Suma wyników: " + resultSum.get());

        for (Map.Entry<String, AtomicInteger> entry : operationCounts.entrySet()) {
            System.out.println("Operacje " + entry.getKey() + ": " + entry.getValue().get());
        }

        System.out.println("\n--- Statystyki z ostatnich 10 sekund ---");
        System.out.println("Liczba operacji: " + recentOperations.get());
        System.out.println("Liczba błędnych operacji: " + recentErrorOperations.get());
        System.out.println("Suma wyników: " + recentResultSum.get());

        for (Map.Entry<String, AtomicInteger> entry : recentOperationCounts.entrySet()) {
            System.out.println("Operacje " + entry.getKey() + ": " + entry.getValue().get());
        }

        resetRecentStatistics();
    }

    private void resetRecentStatistics() {
        recentOperations.set(0);
        recentErrorOperations.set(0);
        recentResultSum.set(0);
        for (AtomicInteger count : recentOperationCounts.values()) {
            count.set(0);
        }
    }
}