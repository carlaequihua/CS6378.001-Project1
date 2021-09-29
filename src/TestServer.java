import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {
    public static void main(String[] args) throws IOException {
        int port = new Integer(args[0]);
        ServerSocket ss = new ServerSocket(port);

        while (true) {
            Socket s = null;

            try {
                s = ss.accept();

                System.out.println("A new client is connect : " + s);

                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                Thread t = new TestClientHandler(s, dis, dos);
                t. start();
            }
            catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }
}

class TestClientHandler extends Thread {
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;

    public TestClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    public void run() {
        String received;

        while(true) {
            try {
                received = dis.readUTF();
                System.out.println(received);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
