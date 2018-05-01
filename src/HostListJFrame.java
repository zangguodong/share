import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;

public class HostListJFrame extends JFrame {
    //private List<Client> list;
    private Client me;

    public HostListJFrame(Client me) throws Exception {
        this.me = me;
    }

    private ListClientPanel lp;

    public void showClusterContent(Map<String, String> map) {
        try {
            lp = new ListClientPanel(map);
            this.add(lp);
            this.setSize(400, 500);
            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNewComponent(String ip, String cont) {

    }

    public void updatePanel(String ip, String text) throws Exception {

        this.lp.update(ip, text);
    }

    class ListClientPanel extends JPanel {
        private Map<String, JLabel> ipLabelmap = new HashMap<>();
        private Map<String, viewJButton> ipButtonmap = new HashMap<>();

        ListClientPanel(Map<String, String> map) throws Exception {
            ipLabelmap.clear();
            GridLayout gridLayout = new GridLayout(0, 2, 10, 10);
            setLayout(gridLayout);
            for (Map.Entry<String, String> c : map.entrySet()) {
                ipLabelmap.put(c.getKey(), new JLabel("<html><body>" + c.getKey() + "<br>" + "<br>"
                        + c.getValue().substring(0, Math.min(10, c.getValue().length())) + "..."
                        + "<body></html>"));
                this.add(ipLabelmap.get(c.getKey()));
                viewJButton tmpbutt;
                if (Util.getLocalHostLANAddress() == null) {
                    System.err.println("无法获取到IP地址，程序退出");
                    return;
                }
                if (!c.getKey().equals(Util.getLocalHostLANAddress().getHostAddress())) {
                    tmpbutt = new viewJButton("查看", c.getKey(), c.getValue(), false);
                } else tmpbutt = new viewJButton("编辑", c.getKey(), c.getValue(), true);
                this.add(tmpbutt);
                ipButtonmap.put(c.getKey(), tmpbutt);
            }

        }

        public void update(String ip, String text) throws Exception {
            if (!ipLabelmap.containsKey(ip)) {
                ipLabelmap.put(ip, new JLabel());
                viewJButton tmpbutt;
                if (!ip.equals(Util.getLocalHostLANAddress().getHostAddress())) {
                    tmpbutt = new viewJButton("查看", ip, text, false);
                } else tmpbutt = new viewJButton("编辑", ip, text, true);
                this.add(ipLabelmap.get(ip));
                this.add(tmpbutt);
                ipButtonmap.put(ip, tmpbutt);
            }
            ipLabelmap.get(ip).setText("<html><body>" + ip + "<br>" + "<br>"
                    + text.substring(0, Math.min(10, text.length())) + "..."
                    + "<body></html>");
            ipButtonmap.get(ip).setBase(text);
            ipButtonmap.get(ip).updateJTAFromBase();
            updateUI();
        }

        private class viewJButton extends JButton {
            //private viewJButton(String s){super(s);}
            private String ip;
            Showlistener sl;
            private String base;

            viewJButton(String showName, String ip, String base, boolean canEdit) {
                super(showName);
                this.ip = ip;
                this.base = base;
                sl = new Showlistener(canEdit, ip, base);
                this.addActionListener(sl);
            }

            public String getIp() {
                return ip;
            }

            public String getBase() {
                return base;
            }

            public void setBase(String base) {
                this.base = base;
            }

            private void updateJTAFromBase() {
                if(sl.getJTA()!=null)sl.getJTA().setText(base);
            }

            class Showlistener implements ActionListener {
                private boolean canedit;
                private String ip;
                private String base;
                public JTextArea getJTA(){return jta;}
                Showlistener(boolean edit, String ip, String base) {
                    this.canedit = edit;
                    this.ip = ip;
                    this.base = base;
                }

                JTextArea jta;

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFrame jf = new JFrame();
                    JPanel jp = new JPanel();
                    jta = new JTextArea(8, 40);
                    jta.setTabSize(8);
                    jta.setLineWrap(true);
                    jta.setWrapStyleWord(true);
                    jta.setEditable(canedit);
                    jta.setText(base);
                    if (canedit) {
                        jp.setLayout(new BorderLayout());
                        jp.add(new JScrollPane(jta), BorderLayout.NORTH);
                        JButton button = new JButton("save");
                        button.addActionListener(a -> {
                            viewJButton vj = ipButtonmap.get(ip);
                            me.setBaseContent(jta.getText());
                            String mainHost = me.getCluster().getHost();
                            System.out.println("mainHost " + mainHost);
                            if (mainHost.equals(me.getIpAddr())) {
                                me.getCluster().getLoginList().stream().filter(ce -> ce.getIpAddr().equals(me.getIpAddr())).forEach(ca -> ca.setBaseContent(me.getBaseContent()));
                                try {
                                    me.getLf().updateHostListPanel(me.getIpAddr(), me.getBaseContent());
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                                return;
                            }
                            Socket s;
                            try {
                                if ((s = me.getSocketMap().get(mainHost)) == null) {
                                    s = new Socket(InetAddress.getByName(mainHost), 7500);
                                    me.getSocketMap().put(mainHost, s);
                                }
                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                                bw.write("update#" + jta.getText());
                                bw.flush();
                                bw.close();
                                System.out.println("we send data " + "update#" + jta.getText() + " to " + s.getInetAddress().getHostName());
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
}