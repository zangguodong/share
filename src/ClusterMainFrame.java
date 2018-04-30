import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

public class ClusterMainFrame extends JFrame {
    static String longstr = "我有一个JAVA程序，里面有3个JFrame，每一个JFrame里面都有一个关闭按钮，" +
            "点击本个里面的关闭按钮只关闭本JFrame，" +
            "而对其他两个没有影响，我应该怎么写代码。";
//    static List<Client> ClientList = Arrays.asList(new Client("192.168.1.102", "zang guo dong")
//            , new Client("192.168.1.104", "zang yong")
//            , new Client("192.168.1.107", longstr)
//            , new Client("192.168.1.103", "wang ming xiang"));
//    static Cluster c1 = new Cluster("集群1 - 有内容哦", ClientList);
//    static List<Cluster> clusterList = Arrays.asList(c1, new Cluster("集群2"),
//            new Cluster("集群3"), new Cluster("集群4"),
//            new Cluster("集群5"), new Cluster("集群6"),
//            new Cluster("集群7"), new Cluster("集群8"),
//            new Cluster("集群9"), new Cluster("集群10"),
//            new Cluster("集群11"), new Cluster("集群12"),
//            new Cluster("集群13"), new Cluster("集群14"),
//            new Cluster("集群15"), new Cluster("集群16")
//    );

    public static void main(String arg[]) throws Exception {
    }

    public void showClusterMainFrame(List<Cluster> clusterList) throws Exception {
        ClusterPanel cp = new ClusterPanel(clusterList);
        this.add(cp);
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}

class ClusterPanel extends JPanel {
    List<Cluster> list;

    public ClusterPanel(List<Cluster> list) throws Exception {
        this.list = list;
        ScrollPanel sp = new ScrollPanel(list);
        JScrollPane jsp = new JScrollPane(sp);
        jsp.setPreferredSize(new Dimension(400, 350));
        this.setLayout(new BorderLayout());
        this.add(jsp, BorderLayout.NORTH);
        JButton createCluster = new JButton("新建集群");
        createCluster.addActionListener(new CreateClusterAction());
        this.add(createCluster, BorderLayout.SOUTH);
    }

    class CreateClusterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    class ScrollPanel extends JPanel {
        List<Cluster> list;

        public ScrollPanel(List<Cluster> ls) throws Exception {
            this.list = ls;
            ScrollPanel.this.setLayout(new GridLayout(0, 2, 10, 5));
            for (Cluster c : ls) {
                //if(c.getLoginList().size()>0)System.out.println(c.getClusterName()+" has thing!");
                JButton butt = new JButton(c.getClusterName());
                JButton login = new JButton("加入集群");
                login.addActionListener(new loginClusterAction(c));
                butt.addActionListener(new expandClusterAction(c));
                ScrollPanel.this.add(butt);
                ScrollPanel.this.add(login);
            }
        }
    }

    class loginClusterAction implements ActionListener {
        Cluster c;

        public loginClusterAction(Cluster c) {
            this.c = c;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean existAlready = false;
            try {
                String ip = Util.getLocalHostLANAddress().getHostAddress();
                for (Client client : c.getLoginList()) {
                    if (client.getIpAddr().equals(ip)) {
                        existAlready = true;
                        break;
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (existAlready) {
                //0 -ok,1-neg,2-cancel
                int ans = JOptionPane.showConfirmDialog(null, "确认加入集群吗？");
                System.out.println("answer is " + ans);
            } else {
                int ans = JOptionPane.showConfirmDialog(null, "当前无主机，你愿意成为宿主机吗？");
                System.out.println("answer is " + ans);
            }
        }
    }

    class expandClusterAction implements ActionListener {
        Cluster c;
        HostListJFrame mj = new HostListJFrame();

        public expandClusterAction(Cluster c) throws Exception {
            this.c = c;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                mj.showClusterContent(c.getLoginList());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
