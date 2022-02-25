import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class Client {

    public Socket connection() throws IOException {

        InetAddress inetAddress = InetAddress.getLocalHost();
        String ip = inetAddress.getHostAddress();

        int port = 12345;

        Socket socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
        socket.connect(inetSocketAddress);
        return socket;
    }

}
