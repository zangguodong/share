import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Test {
    public static void main(String ar[]) throws IOException {
        ServerSocket server = new ServerSocket(7500);
        while (true) {
            Socket s = server.accept();
            new Thread(new TCPServerSocketHandler(null,s)).start();
        }
    }

    public Test() throws IOException {
    }
}
