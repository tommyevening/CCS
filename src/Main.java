import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Użycie: java CentralizedComputingSystem <port>");
            System.exit(1);
        }

        try {
            int port = Integer.parseInt(args[0]);
            CentralizedComputingSystem server = new CentralizedComputingSystem(port);
            server.start();
        } catch (NumberFormatException e) {
            System.err.println("Nieprawidłowy numer portu");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Nie można uruchomić serwera: " + e.getMessage());
            System.exit(1);
        }
    }
}
