import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
    public static final ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) throws Exception{
        String str="[{\"ipAddr\":\"192.168.1.102\",\"port\":0,\"baseContent\":\"hello you\",\"filePrev\":null,\"clusterName\":\"\"},{\"ipAddr\":\"192.168.1.103\",\"port\":0,\"baseContent\":\"i am not ok\",\"filePrev\":null,\"clusterName\":\"\"}]";
        JavaType javaType = getCollectionType(ArrayList.class, Client.class);
        List<Client> lst =  (List<Client>)mapper.readValue(str, javaType);
        System.out.println(lst.size());
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}
