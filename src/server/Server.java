package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    static DataBase db;
    // liste de socketHandler car un client peut se connecter sur plusieurs appareils.
    static Hashtable<String, ArrayList<ClientSocketHandler>> connectedClients = new Hashtable<>();
    static Hashtable<String, ServerSocketHandler> connectedServers = new Hashtable<>();

    private ExecutorService executorService = Executors.newWorkStealingPool();

    private int serverId, port;
    private String serverName;

    public Server(int serverId, int port) throws IOException {
        this.serverId = serverId;
        this.port = port;

        serverName = "serv"+serverId;
        if(serverId == 0){
            serverName = "master";
        }

        System.out.println("Server ready");
        ServerSocket ss = new ServerSocket(port);

        connectToServers();

        try{
            while(true){
                ClientSocketHandler socketHandler = new ClientSocketHandlerClassic(ss.accept());
                executorService.execute(socketHandler);
            }
        } catch(IOException e){
            System.out.println("IOException");
        }
    }

    public void connectToServers() throws IOException {
        Hashtable<String, InetSocketAddress> table = db.getPairs();

        for(Map.Entry<String, InetSocketAddress> entry : table.entrySet()){
            Socket s = new Socket();
            ServerSocketHandler serverSocket;
            try{
                s.connect(entry.getValue());
                serverSocket = new ServerSocketHandler(s);
            } catch (IOException e){
                continue;
            }

            connectedServers.put(entry.getKey(), serverSocket);
            serverSocket.sendServerConnection(serverName);
            serverSocket.setServerSocket();
            serverSocket.setServerName(entry.getKey());

            executorService.execute(serverSocket);
        }
        printConnectedServers();
    }

    static void printConnectedServers(){
        System.out.print("Connected Servers : [");
        for(String name : connectedServers.keySet()){
            System.out.print(" " + name);
        }
        System.out.print(" ]\n");
    }

    static ServerSocketHandler getMasterSocket(){
        if(connectedServers.containsKey("master")){
            return connectedServers.get("master");
        } else {
            return null;
        }
    }

    public static void main(String args[]) throws IOException {
        if(args.length != 2){
            System.out.println("usage : Server.java <serverId> <port>");
            System.exit(-1);
        }

        int serverId = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);
        db = new DataBase(serverId);
        db.getPairs();

        Server server = new Server(serverId, port);
    }
}
