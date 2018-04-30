import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketTest {
    public static void main(String arg[]) throws IOException {
        ServerSocket socket=new ServerSocket(5000);
        Socket s;
        while (true){
            if((s=socket.accept())!=null){
                InputStream in = s.getInputStream();
                BufferedReader bf=new BufferedReader(new InputStreamReader(in));

            }
        }

    }
}
