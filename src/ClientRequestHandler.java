import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ClientRequestHandler {
    private final StatisticsManager statisticsManager;
    private final Socket clientSocket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final MathOperationFactory operationFactory;
    private final Lock ioLock = new ReentrantLock(); // Blokada dla operacji IO

    public ClientRequestHandler(StatisticsManager statisticsManager, Socket clientSocket) throws IOException {
        this.statisticsManager = statisticsManager;
        this.clientSocket = clientSocket;

        // Bezpieczna inicjalizacja strumieni
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);

        this.operationFactory = new MathOperationFactory();
    }

    public void handleConnection() {
        // Obsługuje połączenie z klientem, odbierając i przetwarzając polecenia.
        try {
            String inputLine;
            while ((inputLine = readMessage()) != null) {
                inputLine = inputLine.trim();
                if (inputLine.isEmpty()) continue;

                // Przetwarza polecenie i wysyła wynik do klienta.
                String result = processCommand(inputLine);
                sendMessage(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Zamyka połączenie z klientem po zakończeniu obsługi.
            closeConnection();
        }
    }

    private String readMessage() {
        // Odczytuje wiadomość od klienta, blokując operacje IO dla bezpieczeństwa.
        ioLock.lock();
        try {
            return in.readLine();
        } catch (SocketException e) {
            // Informuje o rozłączeniu klienta.
            System.out.println("Klient rozłączony: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " - " + e.getMessage());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            ioLock.unlock();
        }
    }

    private void sendMessage(String message) {
        // Wysyła wiadomość do klienta, blokując operacje IO dla bezpieczeństwa.
        ioLock.lock();
        try {
            out.println(message);
            out.flush();
        } finally {
            ioLock.unlock();
        }
    }

    private void closeConnection() {
        // Zamyka strumienie i gniazdo połączenia z klientem.
        ioLock.lock();
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ioLock.unlock();
        }
    }

    private String processCommand(String command) {
        // Przetwarza polecenie matematyczne i zwraca wynik lub komunikat o błędzie.
        try {
            String[] parts = command.split("\\s+");
            if (parts.length != 3) {
                return handleError("INVALID_FORMAT");
            }

            String operation = parts[0];
            int arg1 = Integer.parseInt(parts[1]);
            int arg2 = Integer.parseInt(parts[2]);

            // Wykonuje operację matematyczną i aktualizuje statystyki.
            int result = operationFactory.performOperation(operation, arg1, arg2);
            synchronized (statisticsManager) {
                statisticsManager.recordOperation(operation, result);
            }

            System.out.println("Operacja: " + command + ", Wynik: " + result);
            return String.valueOf(result);
        } catch (NumberFormatException e) {
            return handleError("INVALID_NUMBERS");
        } catch (ArithmeticException e) {
            return handleError("DIVIDE_BY_ZERO");
        } catch (Exception e) {
            return handleError("INVALID_OPERATION");
        }
    }

    private String handleError(String errorType) {
        // Rejestruje błąd operacji i zwraca komunikat o błędzie.
        synchronized (statisticsManager) {
            statisticsManager.recordErrorOperation();
        }
        return "ERROR:" + errorType;
    }
}