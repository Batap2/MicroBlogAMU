package Server;

import java.util.ArrayList;

public class Subscription {

    private String subscription;
    private String subscribersStr;
    private String[] subscribers;

    public Subscription(String subscription, String subscribersStr){
        this.subscription = subscription;
        this.subscribersStr = subscribersStr;
    }
    public Subscription(String subscription, String[] subscribers){
        this.subscription = subscription;
        this.subscribers = subscribers;
    }

    public String getSubscription() {
        return subscription;
    }

    public String[] getSubscribers() {
        if(subscribers != null){
            return subscribers;
        }

        int i = 0;
        ArrayList<String> clients = new ArrayList<>();
        while(i < subscribersStr.length()){
            StringBuilder client = new StringBuilder();
            while(i < subscribersStr.length() && subscribersStr.charAt(i) != ' '){
                client.append(subscribersStr.charAt(i));
                i++;
            }
            clients.add(client.toString());
            i++;
        }

        subscribers = new String[clients.size()];
        for(int j = 0; j < clients.size(); j++){
            subscribers[j] = clients.get(j);
        }

        return subscribers;
    }

    public String getSubscribersStr(){
        if(subscribersStr != null){
            return subscribersStr;
        }
        StringBuilder strB = new StringBuilder();
        for(String s : subscribers){
            strB.append(s).append(' ');
        }
        strB.deleteCharAt(strB.length()-1);
        subscribersStr = strB.toString();

        return subscribersStr;
    }
}
