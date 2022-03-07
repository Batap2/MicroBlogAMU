import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class Client {

    public Socket connection(Socket socket) throws IOException {

        InetAddress inetAddress = InetAddress.getLocalHost();
        String ip = inetAddress.getHostAddress();

        int port = 12345;

        InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
        socket.connect(inetSocketAddress);
        return socket;
    }

}
