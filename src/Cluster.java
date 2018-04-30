import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cluster {

    private String ClusterName;

    private List<Client> loginList = new ArrayList<Client>();

    private String host;

    private LocalDateTime createTime;

    public Cluster(String clusterName) {
        this.ClusterName = clusterName;
    }

    public Cluster(String clusterName, List<Client> list) {
        this.ClusterName = clusterName;
        this.loginList = list;
    }

    public List<Client> getLoginList() {
        return loginList;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    @Override
    public String toString() {
        return getClusterName();
    }
}
