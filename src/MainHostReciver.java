import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class MainHostReciver {
    public final static int SecondToOffline = 5;
    private static Map<InetAddress, LocalDateTime> aliveMap = new HashMap<>();
    //private final static Timer timer = new Timer();

    public static void main(String[] args) throws Exception {
//        Client me = new Client(Util.getLocalHostLANAddress().getHostAddress(), "you can paste any str here");
//        DatagramSocket socket = new DatagramSocket(5000);
//        ListenProbeThread change = new ListenProbeThread(me, socket);
//        new Thread(change).start();
//        timer.scheduleAtFixedRate(timerCheckState, 0, 1000 * SecondToOffline);
    }

    public static void queryCluster() {

    }

    public static TimerTask timerCheckState = new TimerTask() {
        Set<InetAddress> set = new HashSet<>();

        @Override
        public void run() {
            System.out.println("now we scan aliveMap");
            for (Map.Entry<InetAddress, LocalDateTime> entry : aliveMap.entrySet()) {
                if (LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")) - entry.getValue().toEpochSecond(ZoneOffset.of("+8")) > SecondToOffline * 2) {
                    set.add(entry.getKey());
                }
            }
            set.forEach(e -> {
                aliveMap.remove(e);
            });
            set.clear();
        }
    };

    static class ListenProbeThread implements Runnable {
        Client me;
        DatagramSocket socket;
        List<Cluster> cluster;

        ListenProbeThread(Client c, DatagramSocket socket, List<Cluster> list) {
            this.me = c;
            this.socket = socket;
            this.cluster = list;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    DatagramPacket pac = new DatagramPacket(new byte[200], 200);
                    socket.receive(pac);
                    String content = new String(pac.getData()).trim();
                    aliveMap.put(pac.getAddress(), LocalDateTime.now());
                    System.out.println("we add ip to map " + pac.getAddress() + " and " + content);
                    if (content.equals(EFunction.probe.name())) {
                        System.out.println("we call this cause " + content);
                        HandleWithProbe(pac);
                        //接下来我们应该用NIO来写，不然不能精确同步
                    } else if (content.startsWith(EFunction.probeRes.name())) {
                        System.out.println("we call this cause " + content);
                        String name = HandleWithProbeRes(pac, content);
                        if (name != null || name.trim().length() > 0) {
                            Cluster c = new Cluster(name);
                            c.setCreateTime(LocalDateTime.now());
                            cluster.add(c);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void HandleWithProbe(DatagramPacket src) throws Exception {
            byte[] probeRes = (EFunction.probeRes.name() + "#" + me.getClusterName()).getBytes();
            DatagramPacket res = new DatagramPacket(probeRes, probeRes.length, src.getAddress(), src.getPort());
            //avoid loop handle
            if (!src.getAddress().getHostAddress().equals(Util.getLocalHostLANAddress().getHostAddress())) {
                System.out.println("we send data to "+res.getAddress().getHostAddress()+":"+new String(res.getData()).trim());
                socket.send(res);
            }
        }

        public String HandleWithProbeRes(DatagramPacket src, String content) throws IOException {
            String[] strs = content.split("#");
            return strs[1];
        }
    }
}
