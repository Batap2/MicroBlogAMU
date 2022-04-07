package testClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientFollowTest {
    public static void main(String args[] ) throws IOException, InterruptedException {
        Socket s = new Socket();
        InetSocketAddress i = new InetSocketAddress("localhost", 12340);
        s.connect(i);
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String str = "CONNECT user:@ClientFollowTest\n";
        byte[] msg = str.getBytes();
        s.getOutputStream().write(msg);

        Thread.sleep(500);

        str = "SUBSCRIBE author:@lol\n";
        msg = str.getBytes();
        s.getOutputStream().write(msg);

        Thread.sleep(500);

        str = "SUBSCRIBE author:@PublishMan\n";
        msg = str.getBytes();
        s.getOutputStream().write(msg);

        while(true){
            String received = inputStream.readLine();
            while(inputStream.ready()){
                received = received + "\r\n" + inputStream.readLine();
            }
            System.out.println("réponse :\n" + received);
        }
    }
}
