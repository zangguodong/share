import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginFrame extends JFrame {
    public Client me;

    public void updateHostListPanel(String ip, String cont) {
        if (this.hf != null) hf.updatePanel(ip, cont);
    }

    public LoginFrame(Client me) {
        this.me = me;
    }

    public void showLoginFrame() {
        LoginPanel lp = new LoginPanel();
        this.add(lp);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setSize(500, 500);
        this.setVisible(true);
    }

    public class LoginPanel extends JPanel {
        public LoginPanel() {
            JButton search = new JButton("附属其他节点");
            search.addActionListener(new searchClusterAction());
            JButton create = new JButton("创建集群");
            JButton check = new JButton("自身集群");
            create.addActionListener(new createClusterAction());
            check.addActionListener(new checkClusterAction());
            setLayout(new GridLayout(0, 1, 10, 5));
            add(search);
            add(check);
            add(create);
        }
    }

    class createClusterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String Cname = JOptionPane.showInputDialog("输入新建集群名称");
            if (Cname == null) {
            } else if (Cname.trim().length() > 0) {
                Cluster c = new Cluster(Cname);
                c.setHost(me.getIpAddr());
                me.setClusterName(c.getClusterName());
                me.setCluster(c);
                c.setCreateTime(LocalDateTime.now());
                Map<String, String> map = new HashMap<>();
                map.put(me.getIpAddr(), me.getBaseContent());
                c.getLoginList().add(me);
                try {
                    HostListJFrame hf = new HostListJFrame(me);
                    SwingUtilities.invokeLater(() -> {
                        try {
                            hf.showClusterContent(map);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    });
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "名称不能为空");
                actionPerformed(e);
            }
        }
    }

    HostListJFrame hf;

    class checkClusterAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Cluster c;
            if ((c = me.getCluster()) == null) {
                JOptionPane.showMessageDialog(null, "当前无集群");
                return;
            }
            List<Client> list = c.getLoginList();
            Map<String, String> map = new HashMap<>();
            list.stream().forEach(ma -> {
                map.put(ma.getIpAddr(), ma.getBaseContent());
            });
            try {
                hf = new HostListJFrame(me);
                SwingUtilities.invokeLater(() -> {
                    hf.showClusterContent(map);
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    class searchClusterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String ip = JOptionPane.showInputDialog("输入目标节点IP", "192.168.1.104");
            try {
                InetAddress target = InetAddress.getByName(ip);
                Socket socket;
//                if ((socket = me.getSocketMap().getOrDefault(ip, null)) == null) {
                socket = new Socket(target.getHostAddress(), 7500);
//                    if (socket.isConnected()) me.getSocketMap().put(ip, socket);
//                }
                socket.getOutputStream().write(EFunction.SearchGroup.name().getBytes());
                socket.getOutputStream().flush();
                socket.shutdownOutput();
                InputStream in = socket.getInputStream();
                BufferedReader bf = new BufferedReader(new InputStreamReader(in));
                StringBuilder bu=new StringBuilder();
                String s;
                while ((s = bf.readLine()) != null) {
                    bu.append(s).append("\n");
                }
                System.out.println(bu.toString());
                bu.deleteCharAt(bu.length()-1);
                bf.close();
                Map<String,String> map=Util.derializeClientList(bu.toString());
                System.out.println(map.size());
                HostListJFrame hf=new HostListJFrame(me);
                hf.showClusterContent(map);

            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "IP格式错误");
                System.out.println(e1.getMessage());
            }
        }
    }
}