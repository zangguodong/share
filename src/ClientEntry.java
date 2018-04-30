import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class ClientEntry {
    static final Timer timer = new Timer();
    String hostName = Util.getLocalHostLANAddress().getHostAddress();

    public ClientEntry() throws Exception {
    }

    public static void main(String arg[]) throws Exception {
        //5000 port to probe
        ClientEntry en = new ClientEntry();
        Client me = en.initClient();
        me.setBaseContent("are you ok?");
        Cluster fakeC = new Cluster("maybe good");
        Client ck = new Client("192.168.1.103", "what do you want");
        ck.setCluster(fakeC);
        ck.setClusterName(fakeC.getClusterName());
        ck.setHost(false);
        List<Client> fakeList = Arrays.asList(ck,
                new Client("192.168.1.120", "i dont want anything"), me);
        fakeC.getLoginList().addAll(fakeList);
        fakeC.setHost(me.getIpAddr());
        me.setCluster(fakeC);
        me.setClusterName(fakeC.getClusterName());
        LoginFrame lf = new LoginFrame(me);
        me.setLf(lf);
        SwingUtilities.invokeLater(() -> {
            lf.showLoginFrame();
        });

    }

    public Client initClient() throws Exception {
        Client c = new Client();
        c.setIpAddr(Util.getLocalHostLANAddress().getHostAddress());
        // c.setCreateTime(LocalDateTime.now());
        c.setPort(5000);
        c.init();
        return c;
    }

    public void queryHost(DatagramSocket socket, String hostName) throws IOException {
        byte[] bs = (EFunction.probe.name() + "#").getBytes();
        DatagramPacket pc = new DatagramPacket(bs, bs.length);
        pc.setPort(5000);
        pc.setAddress(InetAddress.getByName(hostName));
        socket.send(pc);
    }
}
