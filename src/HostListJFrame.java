import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;

public class HostListJFrame extends JFrame {
    //private List<Client> list;
    public HostListJFrame() throws Exception {
    }

    private ListClientPanel lp;

    public void showClusterContent(List<Client> list) {
        HostListJFrame mj;
        try {
            mj = new HostListJFrame();
            lp = new ListClientPanel(list);
            mj.add(lp);
            mj.setSize(400, 500);
            mj.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            mj.setLocationRelativeTo(null);
            mj.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePanel(String ip, String text) {

        this.lp.update(ip, text);
    }

    public static void main(String arg[]) throws Exception {
        HostListJFrame mj = new HostListJFrame();
        // mj.setSize(500, 500);
        mj.setSize(400, 500);
        mj.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mj.setLocationRelativeTo(null);
        // mj.pack();
        mj.setVisible(true);
    }
}

class ListClientPanel extends JPanel {
    private Map<String, JLabel> ipLabelmap = new HashMap<>();
    private Map<String, viewJButton> ipButtonmap = new HashMap<>();

    ListClientPanel(List<Client> list) throws Exception {
        ipLabelmap.clear();
        GridLayout gridLayout = new GridLayout(0, 2, 10, 10);
        setLayout(gridLayout);
        for (Client c : list) {
            ipLabelmap.put(c.getIpAddr(), new JLabel("<html><body>" + c.getIpAddr() + "<br>" + "<br>"
                    + c.getBaseContent().substring(0, Math.min(10, c.getBaseContent().length())) + "..."
                    + "<body></html>"));
            this.add(ipLabelmap.get(c.getIpAddr()));
            viewJButton tmpbutt;
            if (Util.getLocalHostLANAddress() == null) {
                System.err.println("无法获取到IP地址，程序退出");
                return;
            }
            if (!c.getIpAddr().equals(Util.getLocalHostLANAddress().getHostAddress())) {
                tmpbutt = new viewJButton("查看", c, false);
            } else tmpbutt = new viewJButton("编辑", c, true);
            this.add(tmpbutt);
            ipButtonmap.put(c.getIpAddr(), tmpbutt);
        }

    }

    public void update(String ip, String text) {
        //c.setBaseContent(text);
        ipLabelmap.get(ip).setText("<html><body>" + ip + "<br>" + "<br>"
                + text.substring(0, Math.min(10, text.length())) + "..."
                + "<body></html>");
    }

    private class viewJButton extends JButton {
        //private viewJButton(String s){super(s);}
        private Client s;
        Showlistener sl;

        viewJButton(String showName, Client s, boolean canEdit) {
            super(showName);
            this.s = s;
            sl = new Showlistener(s, canEdit);
            this.addActionListener(sl);
        }

        Client getS() {
            return s;
        }

        class Showlistener implements ActionListener {
            private Client s;
            private boolean canedit;
            private String ip;

            Showlistener(Client s, boolean edit) {
                this.s = s;
                this.canedit = edit;
                this.ip = s.getIpAddr();
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jf = new JFrame();
                JPanel jp = new JPanel();
                JTextArea jta = new JTextArea(8, 40);
                jta.setTabSize(8);
                jta.setLineWrap(true);
                jta.setWrapStyleWord(true);
                jta.setEditable(canedit);
                jta.setText(s.getBaseContent());
                if (canedit) {
                    jp.setLayout(new BorderLayout());
                    jp.add(new JScrollPane(jta), BorderLayout.NORTH);
                    JButton button = new JButton("save");
                    button.addActionListener(a -> {
                        viewJButton vj = ipButtonmap.get(ip);
                        Client me = vj.getS();
                        me.setBaseContent(jta.getText());
                        String mainHost = me.getCluster().getHost();
                        Socket s;
                        try {
                            if ((s = me.getSocketMap().get(mainHost)) == null) {
                                s = new Socket(InetAddress.getByName(mainHost), 5000);
                            }
                            s.getOutputStream().write(("update#" + jta.getText()).getBytes());
                            s.getOutputStream().flush();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        //TODO notify all other host


                    });
                    jp.add(button, BorderLayout.SOUTH);
                } else {
                    jp.add(new JScrollPane(jta));
                }
                jf.add(jp);
                jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                jf.setLocationRelativeTo(null);
                jf.pack();
                jf.setVisible(true);
            }
        }
    }
}
