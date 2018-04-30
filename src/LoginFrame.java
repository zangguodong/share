import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class LoginFrame extends JFrame {
    public Client me;


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
                c.setHost(me);
                me.setClusterName(c.getClusterName());
                me.setCluster(c);
                c.setCreateTime(LocalDateTime.now());
                List<Client> list = Arrays.asList(me);
                c.getLoginList().add(me);
                try {
                    HostListJFrame hf = new HostListJFrame();
                    SwingUtilities.invokeLater(() -> {
                        try {
                            hf.showClusterContent(list);
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

    class checkClusterAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Cluster c;
            if ((c = me.getCluster()) == null) {
                JOptionPane.showMessageDialog(null, "当前无集群");
                return;
            }
            List<Client> list = c.getLoginList();
            try {
                HostListJFrame hf = new HostListJFrame();
                SwingUtilities.invokeLater(() -> {
                    hf.showClusterContent(list);
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    class searchClusterAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String ip = JOptionPane.showInputDialog("输入目标节点IP");
            try {
                InetAddress target=InetAddress.getByName(ip);
                //ServerSocket socket=me.getServerSocket();
                Socket socket=new Socket(target.getHostAddress(),7500);
                if(socket.isConnected()) {
                    socket.getOutputStream().write("连接请求".getBytes());
                }
            } catch (UnknownHostException e1) {
                JOptionPane.showMessageDialog(null, "IP格式错误");
                actionPerformed(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}



