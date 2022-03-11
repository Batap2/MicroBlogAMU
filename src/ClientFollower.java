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

    public String getOption(){
        String option;
        int index;
        if(message.contains(" ")){
            index = message.indexOf(" ");
            option = message.substring(0, index);
            message = message.substring(index+1);
        }
        else{
            option = message;
            message = "";
        }
        return option;
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
            if(clientFollower.message.charAt(0) != '@'){
                System.out.println("erreur de format");
            }
            else{
                clientFollower.author = clientFollower.getOption();
            }

            if(clientFollower.message.length() != 0 && clientFollower.message.charAt(0) == '#'){
                clientFollower.tag = clientFollower.getOption();
            }
            if(clientFollower.message.length() != 0 && clientFollower.message.contains("id:")){
                clientFollower.id = clientFollower.getOption();
            }
            if(clientFollower.message.length() != 0){
                clientFollower.limit = clientFollower.getOption();
            }
            

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

            clientFollower.reinit();

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(msg);

            System.out.println(envoi);

            //boucle pour avoir tous les id des messages, puis les redonner au serveur pour afficher les messages
            String allID = "";
            String response = inputStream.readLine();
            if(!response.equals("MSG_IDS")){
                System.out.println("erreur");
            }
            else{
                while(inputStream.ready()){
                    allID = inputStream.readLine()+"\r\n";
                }

            }

            System.out.println("Messages correspondant à la demande :");
            int lengthResponse = allID.length();
            while(allID.contains(" ")){
                String id = allID.substring(0, allID.indexOf(" "));
                allID = allID.substring(allID.indexOf(" ")+1, lengthResponse);
                lengthResponse = allID.length();
                String requete = "RCV_MSG msg_id:"+id+"\r\n";
                byte[] message = requete.getBytes();

                OutputStream outputStream2 = socket.getOutputStream();
                outputStream2.write(message);

                String response2 = inputStream.readLine();
                String receivedMessage = "";
                if(!response2.equals("MSG")){
                    System.out.println("erreur");
                }
                else{

                    while(inputStream.ready()){
                        receivedMessage = receivedMessage + " "+inputStream.readLine()+"\r\n";
                    }
                }

                System.out.println(receivedMessage);
            }


        }




    }





}






    /*

    private String getOption(String message, int length){
        int index;
        if(message.contains(" ")){
            index = message.indexOf(" ");
        }
        else{
            index = message.length();
        }
        String option = message.substring(0, index);
        if(index != message.length()){
            this.message = message.substring(index+1, length);
        }
        else{
            this.message ="";
        }
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
                System.out.println("("+clientFollower.message+")");

                if(clientFollower.message.charAt(0) == '#'){
                    System.out.println("BOU58");

                    clientFollower.tag = clientFollower.getOption(clientFollower.message, length);
                    length = majLength(clientFollower.message);

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
            System.out.println("@"+clientFollower.author+"@");
            System.out.println("@"+clientFollower.tag+"@");
            System.out.println("@"+clientFollower.id+"@");
            System.out.println("@"+clientFollower.limit+"@");
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

            //boucle pour avoir tous les id des messages, puis les redonner au serveur pour afficher les messages
            String allID = "";
            String response = inputStream.readLine();
            if(!response.equals("MSG_IDS")){
                System.out.println("erreur");
            }
            else{
                while(inputStream.ready()){
                    allID = inputStream.readLine()+"\r\n";
                }

            }

            System.out.println("Messages correspondant à la demande :");
            int lengthResponse = allID.length();
            while(allID.contains(" ")){
              String id = allID.substring(0, allID.indexOf(" "));
              allID = allID.substring(allID.indexOf(" ")+1, lengthResponse);
              lengthResponse = allID.length();
              String requete = "RCV_MSG msg_id:"+id+"\r\n";
              byte[] message = requete.getBytes();

              OutputStream outputStream2 = socket.getOutputStream();
              outputStream2.write(message);

              String response2 = inputStream.readLine();
              String receivedMessage = "";
              if(!response2.equals("MSG")){
                  System.out.println("erreur");
              }
              else{

                  while(inputStream.ready()){
                      receivedMessage = receivedMessage + " "+inputStream.readLine()+"\r\n";
                  }
              }

                System.out.println(receivedMessage);
            }


            }


            clientFollower.reinit();

        }



*/



