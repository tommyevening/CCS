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
        // Inicjalizuje statystyki dla operacji
        for (String op : Arrays.asList("ADD", "SUB", "MUL", "DIV")) {
            operationCounts.put(op, new AtomicInteger(0));
            recentOperationCounts.put(op, new AtomicInteger(0));
        }
    }

    public void recordClientConnection() {
        totalClients.incrementAndGet(); // Zwiększa licznik klientów
    }

    public void recordOperation(String operation, int result) {
        //Operacje całkowite
        totalOperations.incrementAndGet(); // Zwiększa licznik operacji
        operationCounts.get(operation).incrementAndGet(); // Zwiększa licznik dla danej operacji
        resultSum.addAndGet(result); // Dodaje wynik do sumy

        //Operacje ostatnie 10 sekund
        recentOperations.incrementAndGet(); // Zwiększa licznik operacji
        recentOperationCounts.get(operation).incrementAndGet(); // Zwiększa licznik dla danej operacji
        recentResultSum.addAndGet(result); // Dodaje wynik do sumy
    }

    public void recordErrorOperation() {
        errorOperations.incrementAndGet(); // Zwiększa licznik błędnych operacji
        recentErrorOperations.incrementAndGet(); // Zwiększa licznik błędnych operacji w ostatnich 10 sekundach
    }

    public void printStatistics() {
        // Wypisuje statystyki całkowite i statystyki z ostatnich 10 sekund
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
        // Resetuje statystyki z ostatnich 10 sekund
        recentOperations.set(0);
        recentErrorOperations.set(0);
        recentResultSum.set(0);
        for (AtomicInteger count : recentOperationCounts.values()) {
            count.set(0);
        }
    }
}