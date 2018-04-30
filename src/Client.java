import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.time.LocalDateTime;

public class Client {
    private String ipAddr;
    private int port;
    private String baseContent="u can copy this";
    private String FilePrev;
    private LocalDateTime createTime;
    private String ClusterName="";
    private Cluster Cluster;
    private DatagramSocket datagramSocket=new DatagramSocket(5000);
    private ServerSocket serverSocket=new ServerSocket(7500);

    public Client() throws IOException {
    }

    public Client(String ip, String base) throws IOException {
        this.ipAddr = ip;
        this.baseContent = base;
        this.createTime = LocalDateTime.now();
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
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
}
