import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

public class Util {
    public static InetAddress getLocalHostLANAddress() throws Exception {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            return InetAddress.getLocalHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
    }

    public static String serializeClientList(List<Client> cli) {
        StringBuilder bu = new StringBuilder(Math.max(cli.size() * 10, 20));
        cli.stream().forEach((k -> {
            bu.append(k.getIpAddr()).append("#").append(k.getBaseContent()).append("@");
        }));
        bu.deleteCharAt(bu.length() - 1);
        return bu.toString();
    }

    public static Map<String, String> derializeClientList(String s) {
        String[] strs = s.split("@");
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < strs.length; i += 1) {
            String tmp=strs[i];
            map.put(tmp.split("#")[0],tmp.split("#")[1]);
        }
        return map;
    }
}
