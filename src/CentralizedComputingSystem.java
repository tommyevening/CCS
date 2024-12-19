import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CentralizedComputingSystem {
    private int port;
    private StatisticsManager statisticsManager;
    private ExecutorService clientThreadPool;
    private ServerSocket tcpServerSocket;
    private UDPDiscoveryServer udpServer;
    private volatile boolean isRunning = true;

    public CentralizedComputingSystem(int port) throws IOException {
        this.port = port;
        this.statisticsManager = new StatisticsManager();
        int processors = Runtime.getRuntime().availableProcessors();
        this.clientThreadPool = Executors.newFixedThreadPool(processors * 4);
        this.tcpServerSocket = new ServerSocket(port);
        this.udpServer = new UDPDiscoveryServer(port);

        startStatisticsReportingThread();
    }

    public void start() {
        Thread udpThread = new Thread(udpServer);
        udpThread.start();

        try {
            while (isRunning) {
                Socket clientSocket = tcpServerSocket.accept();
                statisticsManager.recordClientConnection();

                clientThreadPool.submit(() -> {
                    try (Socket socket = clientSocket) {
                        ClientRequestHandler requestHandler = new ClientRequestHandler(statisticsManager, socket);
                        requestHandler.handleConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (SocketException se) {
            // Socket zamknięty podczas shutdown - ignorujemy
            if (isRunning) {
                se.printStackTrace();
            }
        } catch (IOException e) {
            if (isRunning) {
                e.printStackTrace();
            }
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        isRunning = false;
        try {
            if (tcpServerSocket != null) {
                tcpServerSocket.close();
            }
            udpServer.stopServer(); // Zmieniona nazwa metody
            clientThreadPool.shutdown();
            clientThreadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startStatisticsReportingThread() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
                () -> statisticsManager.printStatistics(),
                10, 10, TimeUnit.SECONDS
        );
    }
}