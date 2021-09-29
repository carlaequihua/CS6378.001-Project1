import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler extends Thread {
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket socket;
    final Listener listener;

    public ClientHandler(Socket socket, Listener listener) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.listener = listener;
    }

    public void run() {
        String received;

        while(true) {
            try {
                //TODO: When a message is received, the listener's receive() is called.
                //Change this code to read message from InputStream (used readUTF() for testing)
                received = dis.readUTF();
                System.out.println("Message received : " + received);
                //listener.receive(message);


            } catch (SocketException e) {
                //TODO: When a connection has been terminated by a neighbor, the listener's broken() is called.
                //listener.broken(nodeid);

                System.out.println("Connection closed from "+socket);
                e.printStackTrace();
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}