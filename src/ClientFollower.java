import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientFollower extends Client{

    String message;
    String author;
    String tag;
    String id;
    String limit = "5";

    private String getOption(String message, int length){
        int index = message.indexOf(" ");
        String option = message.substring(0, index+1);
        this.message = message.substring(index+1, length);
        return option;
    }

    private static int majLength(String message){
        return message.length();
    }

    private void reinit(){
        this.limit = "5";
        this.id = null;
        this.author = null;
        this.tag = null;
    }


    public static void main(String args[]) throws IOException {

        ClientFollower clientFollower = new ClientFollower();
        Socket socket = new Socket();
        socket = clientFollower.connection(socket);

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));


        Scanner keyBoard = new Scanner(System.in);
        System.out.println("De qui voulez-vous récupérer les messages ?");
        System.out.println("Format : @user [#tag] [id:id] [limit]\n");


        while(keyBoard.hasNextLine()){
            clientFollower.message = keyBoard.nextLine();
            int length = clientFollower.message.length();
            if(clientFollower.message.contains(" ")){
                clientFollower.author = clientFollower.getOption(clientFollower.message, length);
                length = majLength(clientFollower.message);

                if(clientFollower.message.charAt(0) == '#'){
                    if(clientFollower.message.contains(" ")){
                        clientFollower.tag = clientFollower.getOption(clientFollower.message, length);
                        length = majLength(clientFollower.message);
                    }
                }
                if(clientFollower.message.contains(" ")){ //contient à la fois id et limit
                    clientFollower.id = clientFollower.getOption(clientFollower.message, length);
                    clientFollower.limit = clientFollower.message;
                }
                else{//juste id (ou limit)
                    if(clientFollower.message.contains("id")){
                        int index = clientFollower.message.indexOf(":");
                        clientFollower.id = clientFollower.message.substring(index+1, length);
                    }
                    else{
                        clientFollower.limit = clientFollower.message;
                    }

                }

            }
            else{ //il y a juste le nom de l'auteur
                clientFollower.author = clientFollower.message;
            }
            System.out.println("auteur : "+clientFollower.author+" tag: "+clientFollower.tag+" id: "+clientFollower.id+" limit: "+clientFollower.limit);


            String envoi = "RCV_IDS author:"+clientFollower.author;
            if(clientFollower.tag != null){
                envoi = envoi+" tag:"+clientFollower.tag;
            }
            if(clientFollower.id != null){
                envoi = envoi+" since_id:"+clientFollower.id;
            }
            if(clientFollower.limit != null){
                envoi = envoi+" limit:"+clientFollower.limit;
            }

            envoi = envoi+"\r\n";
            byte[] msg = envoi.getBytes();

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(msg);

            System.out.println(envoi);
            System.out.println("a envoyé");

            //boucle pour avoir tous les id des messages, puis les redonner au serveur pour afficher les messages
            String allID = inputStream.readLine();
            System.out.println(allID);
            int lengthResponse = allID.length();
            while(allID.contains(" ")){
              String id = allID.substring(0, allID.indexOf(" ")+1);
              allID = allID.substring(allID.indexOf(" ")+1, lengthResponse);
              lengthResponse = allID.length();
              String requete = "RCV_MSG msg_id:"+id+"\r\n";
              byte[] message = requete.getBytes();

              OutputStream outputStream2 = socket.getOutputStream();
              outputStream2.write(message);

              System.out.println("Message : " + inputStream.readLine());
            }


            clientFollower.reinit();

        }



    }



}