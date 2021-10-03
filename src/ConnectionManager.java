import java.net.InetAddress;
import java.net.Socket;

public class ConnectionManager extends Thread {
    private Socket socket;
    private final String host;
    private final int port;
    private boolean isTearDown;
    private int retryTimer;

    public ConnectionManager(String destHost, int destPort) {
        this.socket = null;
        this.host = destHost;
        this.port = destPort;
        this.isTearDown = false;
        retryTimer = 5000;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void run() {
        while (!isTearDown && (socket == null || socket.isClosed())) {
            try {
                InetAddress ip = InetAddress.getByName(host);
                socket = new Socket(ip, port);
                System.out.println("[CLIENT] GET CONNECTION TO : " + socket);
            } catch (Exception e) {
                try {
                    System.out.println("[CLIENT] CAN NOT MAKE CONNECTION TO : " + host + ", " + port + " (RETRY AFTER FEW SEC / " + e.getMessage() +")");
                    Thread.sleep(retryTimer);
                } catch (Exception ie) {
                    ie.printStackTrace();
                }
            }
        }
    }
}