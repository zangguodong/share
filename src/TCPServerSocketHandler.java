import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPServerSocketHandler implements Runnable {
    private Socket socket;

    public TCPServerSocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder bu = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) bu.append(str);
            System.out.println(bu.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
