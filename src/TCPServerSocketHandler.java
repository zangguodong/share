import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class TCPServerSocketHandler implements Runnable {
    private Client me;
    private Socket socket;
    private HostListJFrame hf;

    public TCPServerSocketHandler(Client me, Socket socket) {
        this.me = me;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder bu = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) bu.append(str).append("\n");
            bu.deleteCharAt(bu.length() - 1);
            System.out.println("get data ------------------>" + bu.toString());
            EFunction ef = checkState(bu.toString());
            String ip = socket.getInetAddress().getHostAddress();
            socket.shutdownInput();
            switch (ef) {
                case update: {
                    String content = bu.substring(7);
                    String src = socket.getInetAddress().getHostAddress().trim();
                    //非主机，仅将自己同步
                    me.getCluster().getLoginList().stream().filter(e -> e.getIpAddr().equals(src)).forEach(a -> a.setBaseContent(content));
                    me.getLf().updateHostListPanel(src, content);
                    if (me.isHost()) {
                        for (Client c : me.getCluster().getLoginList()) {
                            if (c.getIpAddr().equals(me.getIpAddr()) ||
                                    c.getIpAddr().equals(src)) continue;
                            Socket st = new Socket(InetAddress.getByName(c.getIpAddr()), 5000);
                            st.getOutputStream().write((EFunction.update.name() + "#" + content).getBytes());
                            st.getOutputStream().flush();
                            st.shutdownOutput();
                            st.close();
                        }
                    }
                    break;
                }
                case login: {
                    String content = bu.substring(6);
                    Client tmp = new Client(ip, content);
                    tmp.setCluster(me.getCluster());
                    List<Client> list = me.getCluster().getLoginList();
                    boolean exist = list.stream().anyMatch(e -> e.getIpAddr().equals(ip));
                    if (!exist) {
                        System.out.println("we add data to List");
                        list.add(tmp);
                    }
                    //synchronize other machine
                    me.getLf().hf.updatePanel(ip, content);
                }
                case SearchGroup: {
//                    if (!me.isHost())
//                        sendString(socket, "not host,do you want to check on " +
//                                me.getCluster().getHost());
                    //TODO make some date
                    String toSend = Util.serializeClientList(me.getCluster().getLoginList());
                    socket.getOutputStream().write(toSend.getBytes());
                    socket.getOutputStream().flush();
                    socket.shutdownOutput();
                    System.out.println("send data -------------->" + toSend);
                    break;
                }
                case ping: {
                    socket.getOutputStream().write(EFunction.pong.name().getBytes());
                    socket.getOutputStream().flush();
                    System.out.println("we wll send pong");
                    break;
                }
                case pong: {

                    break;
                }
                case rechoose: {
                    String content = bu.substring(9);
                    System.out.println("accept ip " + content + " we will set it as server");
                    if (content.equals(me.getIpAddr())) me.setHost(true);
                    String rawIp = me.getCluster().getHost();
                    me.getCluster().setHost(content);
                    int i = -1;
                    List<Client> list = me.getCluster().getLoginList();
                    for (int k = 0; k < list.size(); k++) {
                        if (list.get(k).getIpAddr().equals(rawIp))
                            i = k;
                    }
                    if (i >= 0) {
                        list.remove(i);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendString(Socket s, String str) throws IOException {
        s.getOutputStream().write(str.getBytes());
        s.getOutputStream().flush();
    }

    public EFunction checkState(String s) {
        if (s.startsWith(EFunction.update.name())) return EFunction.update;
        else if (s.startsWith(EFunction.ping.name())) return EFunction.ping;
        else if (s.startsWith(EFunction.pong.name())) return EFunction.pong;
        else if (s.startsWith(EFunction.SearchGroup.name())) return EFunction.SearchGroup;
        else if (s.startsWith(EFunction.login.name() + "#")) return EFunction.login;
        else if (s.startsWith(EFunction.loginDone.name() + "#")) return EFunction.loginDone;
        else if (s.startsWith(EFunction.rechoose.name() + "#")) return EFunction.rechoose;
        else return EFunction.Unknown;
    }

    public HostListJFrame getHf() {
        return hf;
    }

    public void setHf(HostListJFrame hf) {
        this.hf = hf;
    }
}
