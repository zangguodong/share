import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

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
            socket.shutdownInput();
            switch (ef) {
                case update: {
                    String content = bu.substring(7);
                    String src = socket.getInetAddress().getHostAddress().trim();
                    //非主机，仅将自己同步
                    me.getCluster().getLoginList().stream().filter(e -> e.getIpAddr().equals(src)).forEach(a -> a.setBaseContent(content));
                    me.getLf().updateHostListPanel(src, content);
                    if (me.isHost()) {

                    }
                    break;
                }
                case SearchGroup: {
                    if (!me.isHost())
                        sendString(socket, "not host,do you want to check on " +
                                me.getCluster().getHost());
                    //TODO make some date
                    String toSend = Util.serializeClientList(me.getCluster().getLoginList());
                    socket.getOutputStream().write(toSend.getBytes());
                    socket.getOutputStream().flush();
                    System.out.println("send data search group " + toSend);
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
            }
        } catch (IOException e) {
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
        else return EFunction.Unknown;
    }

    public HostListJFrame getHf() {
        return hf;
    }

    public void setHf(HostListJFrame hf) {
        this.hf = hf;
    }
}
