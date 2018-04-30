import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;;

public class Client {
    private String ipAddr;
    private int port;
    private String baseContent = "u can copy this";
    private String FilePrev;
    private String ClusterName = "";
    private Cluster Cluster;
    private DatagramSocket datagramSocket;
    private ServerSocket serverSocket;
    private boolean host = false;
    private LoginFrame lf;
    private Map<String, Socket> socketMap = new HashMap<>();

    public Client() throws IOException {
    }

    public void init() throws IOException {
        datagramSocket = new DatagramSocket(5000);
        serverSocket = new ServerSocket(7500);
        new Thread(() -> {
            while (true) {
                try {
                    Socket s = serverSocket.accept();
                    System.out.println("get connect from " + s.getInetAddress().getHostAddress());
                    new Thread(new TCPServerSocketHandler(Client.this, s)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public Client(String ip, String base) throws IOException {
        this.ipAddr = ip;
        this.baseContent = base;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getBaseContent() {
        return baseContent;
    }

    public void setBaseContent(String baseContent) {
        this.baseContent = baseContent;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFilePrev() {
        return FilePrev;
    }

    public void setFilePrev(String filePrev) {
        FilePrev = filePrev;
    }

    public String getClusterName() {
        return ClusterName;
    }

    public void setClusterName(String clusterName) {
        ClusterName = clusterName;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public Cluster getCluster() {
        return Cluster;
    }

    public void setCluster(Cluster cluster) {
        Cluster = cluster;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }

    public LoginFrame getLf() {
        return lf;
    }

    public void setLf(LoginFrame lf) {
        this.lf = lf;
    }

    public Map<String, Socket> getSocketMap() {
        return socketMap;
    }
}
