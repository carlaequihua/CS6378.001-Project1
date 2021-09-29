import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        try {
            String host = args[0];
            int port = new Integer(args[1]);

            InetAddress ip = InetAddress.getByName(host);
            Socket socket = new Socket(ip, port);

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            while (true) {
                dos.writeUTF("Hi");
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}